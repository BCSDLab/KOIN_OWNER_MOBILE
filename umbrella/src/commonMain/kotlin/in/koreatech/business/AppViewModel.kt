package `in`.koreatech.business

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.koreatech.business.domain.error.DomainError
import `in`.koreatech.business.domain.usecase.auth.DeleteAccountUseCase
import `in`.koreatech.business.domain.usecase.owner.GetOwnerProfileUseCase
import `in`.koreatech.business.domain.usecase.owner.GetRequiredVersionUseCase
import `in`.koreatech.business.domain.usecase.preferences.ObserveThemeModeUseCase
import `in`.koreatech.business.domain.usecase.store.SetActiveStoreIdUseCase
import `in`.koreatech.business.domain.usecase.token.ClearTokensUseCase
import `in`.koreatech.business.domain.usecase.token.GetAccessTokenUseCase
import `in`.koreatech.business.domain.usecase.token.ObserveAccessTokenUseCase
import `in`.koreatech.business.platform.getAppVersion
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

class AppViewModel(
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val getRequiredVersionUseCase: GetRequiredVersionUseCase,
    private val getAccessTokenUseCase: GetAccessTokenUseCase,
    private val getOwnerProfileUseCase: GetOwnerProfileUseCase,
    private val observeAccessTokenUseCase: ObserveAccessTokenUseCase,
    private val clearTokensUseCase: ClearTokensUseCase,
    private val setActiveStoreIdUseCase: SetActiveStoreIdUseCase,
    private val observeThemeModeUseCase: ObserveThemeModeUseCase
) : ViewModel(),
    ContainerHost<AppState, Nothing> {
    override val container = container<AppState, Nothing>(AppState())

    private val _launchState = MutableStateFlow(LaunchState.Loading)
    val launchState: StateFlow<LaunchState> = _launchState.asStateFlow()

    /**
     * 외부 토큰 변화로 세션이 만료된 시점에 emit되는 일회성 이벤트.
     * AppNavigation이 collect해서 root nav를 AuthGraph로 교체하는 데 사용된다.
     *
     * SharedFlow(replay=0)는 collector가 비활성인 동안의 emission을 late
     * subscriber에게 전달하지 않으므로(replay 0), Configuration change 윈도우에
     * 발사된 이벤트가 유실될 수 있다. Channel은 미수신 이벤트를 1개 보존하다가
     * 다음 collector가 구독하면 drain한다(CONFLATED는 항상 latest 1개 유지).
     */
    private val _sessionExpired = Channel<Unit>(
        capacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val sessionExpired: Flow<Unit> = _sessionExpired.receiveAsFlow()

    init {
        observeThemeMode()
        observeAccessTokenChanges()
        refreshLaunchState()
    }

    private fun observeAccessTokenChanges() {
        viewModelScope.launch {
            // observeAccessTokenUseCase()는 첫 구독 시점의 저장값부터 emit한다.
            // 그 첫 값은 앱 시작 시점이므로 무시(refreshLaunchState가 별도로 결정).
            // 이후 토큰이 빈 문자열로 바뀌면(=Bearer refresh 실패) sessionExpired emit.
            var initialEmissionConsumed = false
            observeAccessTokenUseCase()
                // DataStore가 IOException(파일 손상, 디스크 오류 등)을 flow로
                // 전파하면 이 catch 없이는 collectLatest가 예외로 종료되어 이후
                // sessionExpired가 더 이상 발사되지 않는다. 로깅 후 흡수해 적어도
                // 다른 viewModelScope의 코루틴들은 영향받지 않게 한다.
                .catch { error ->
                    Napier.e(
                        "Token observer flow terminated by error: ${error.message}",
                        error,
                        tag = "AppViewModel"
                    )
                }
                .collectLatest { token ->
                    if (!initialEmissionConsumed) {
                        initialEmissionConsumed = true
                        return@collectLatest
                    }
                    if (token.isBlank()) {
                        Napier.i("Access token cleared — emitting sessionExpired", tag = "AppViewModel")
                        // 동기 알림 먼저: collectLatest가 다음 emission으로 이 lambda를
                        // 취소하더라도 sessionExpired가 누락되지 않도록 보장.
                        _launchState.value = LaunchState.Unauthenticated
                        _sessionExpired.trySend(Unit)
                        // 마지막으로 활성 매장 정리(suspending). 취소되어도 다음 로그인
                        // 시점에 어차피 새로 선택하므로 idempotent.
                        // UseCase가 Result<Unit>을 반환하므로 별도의 try/catch 없이 호출만 하고
                        // 실패는 무시한다(runCatchingCancellable이 CE는 이미 재전파).
                        setActiveStoreIdUseCase(null)
                    }
                }
        }
    }

    private fun observeThemeMode() {
        viewModelScope.launch {
            observeThemeModeUseCase().collectLatest { mode ->
                intent(registerIdling = false) { reduce { state.copy(themeMode = mode) } }
            }
        }
    }

    fun refreshLaunchState() {
        viewModelScope.launch {
            _launchState.value = LaunchState.Loading
            val state = when {
                isForceUpdateRequired() -> LaunchState.RequiresUpdate
                hasValidOwnerSession() -> LaunchState.Authenticated
                else -> LaunchState.Unauthenticated
            }
            _launchState.value = state
        }
    }

    fun clearSession() {
        viewModelScope.launch {
            // UseCase가 Result<Unit>을 반환 — runCatchingCancellable이 이미 CE를 재전파하므로
            // 호출 후 실패는 무시한다.
            clearTokensUseCase()
            setActiveStoreIdUseCase(null)
            _launchState.value = LaunchState.Unauthenticated
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            deleteAccountUseCase()
            clearTokensUseCase()
            setActiveStoreIdUseCase(null)
            _launchState.value = LaunchState.Unauthenticated
        }
    }

    private suspend fun hasValidOwnerSession(): Boolean {
        val token = getAccessTokenUseCase()
            .onFailure { e ->
                // CancellationException은 runCatchingCancellable이 이미 재전파하지만,
                // Result.failure로 도착하는 다른 throwable은 여기서 흡수해 launchState가
                // 영구히 Loading에 갇히지 않게 한다 — 안전 기본값은 "세션 없음".
                if (e is CancellationException) throw e
                Napier.e("hasValidOwnerSession token read failed: ${e.message}", e, tag = "AppViewModel")
            }
            .getOrDefault("")
            .trim()

        // 토큰 자체가 없으면 네트워크 호출 없이 미인증.
        if (token.isBlank() || token.lowercase() == "null") return false

        // 토큰이 있으면 서버로 실제 검증한다(깜빡임 제거: Loading 화면을 유지한 채
        // /owner 호출이 끝난 뒤 곧장 StoreGraph 또는 AuthGraph로 분기).
        //  - 성공             → 유효(또는 Bearer가 내부적으로 refresh 성공) → 인증됨
        //  - DomainError.Auth → 401 + refresh 실패 → 인증 클라이언트 validator가
        //                       이미 토큰을 비웠고 sessionExpired도 발사됨 → 미인증
        //  - 그 외(네트워크/서버 오류) → 토큰은 아직 살아있으므로 강제 로그아웃하지
        //    않고 낙관적으로 인증됨 처리(오프라인/일시 장애로 로그인 튕김 방지)
        return getOwnerProfileUseCase().fold(
            onSuccess = { true },
            onFailure = { e ->
                if (e is CancellationException) throw e
                if (e is DomainError.Auth) {
                    Napier.i("Session invalid (401) on launch — routing to login", tag = "AppViewModel")
                    false
                } else {
                    Napier.w(
                        "Session check failed with non-auth error — assuming authenticated: ${e.message}",
                        tag = "AppViewModel"
                    )
                    true
                }
            }
        )
    }

    private suspend fun isForceUpdateRequired(): Boolean = getRequiredVersionUseCase()
        .onFailure { if (it is CancellationException) throw it }
        .map { required -> compareVersions(getAppVersion(), required) < 0 }
        .getOrDefault(false)

    internal fun compareVersions(current: String, required: String): Int {
        val c = current.split(".").map { it.toIntOrNull() ?: 0 }
        val r = required.split(".").map { it.toIntOrNull() ?: 0 }
        for (i in 0..2) {
            val diff = (c.getOrElse(i) { 0 }).compareTo(r.getOrElse(i) { 0 })
            if (diff != 0) return diff
        }
        return 0
    }
}
