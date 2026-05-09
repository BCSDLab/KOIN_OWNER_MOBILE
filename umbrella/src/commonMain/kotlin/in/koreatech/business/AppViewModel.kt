package `in`.koreatech.business

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.koreatech.business.domain.usecase.auth.DeleteAccountUseCase
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
                        runCatching { setActiveStoreIdUseCase(null) }
                            .onFailure { if (it is CancellationException) throw it }
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
            runCatching { clearTokensUseCase() }
                .onFailure { if (it is CancellationException) throw it }
            runCatching { setActiveStoreIdUseCase(null) }
                .onFailure { if (it is CancellationException) throw it }
            _launchState.value = LaunchState.Unauthenticated
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            runCatching { deleteAccountUseCase() }
                .onFailure { if (it is CancellationException) throw it }
            runCatching { clearTokensUseCase() }
                .onFailure { if (it is CancellationException) throw it }
            runCatching { setActiveStoreIdUseCase(null) }
                .onFailure { if (it is CancellationException) throw it }
            _launchState.value = LaunchState.Unauthenticated
        }
    }

    private suspend fun hasValidOwnerSession(): Boolean = runCatching {
        val token = getAccessTokenUseCase().trim()
        token.isNotBlank() && token.lowercase() != "null"
    }.onFailure { e ->
        // CancellationException은 구조적 동시성을 위해 즉시 재전파.
        // DataStore 손상/디스크 오류 시 IOException은 흡수해서 refreshLaunchState
        // 코루틴이 launchState를 Loading에 영구 고립시키지 않도록 한다 — 안전
        // 기본값은 "세션 없음"으로 보고 재로그인을 유도하는 것.
        if (e is CancellationException) throw e
        Napier.e("hasValidOwnerSession failed: ${e.message}", e, tag = "AppViewModel")
    }.getOrDefault(false)

    private suspend fun isForceUpdateRequired(): Boolean = runCatching {
        compareVersions(getAppVersion(), getRequiredVersionUseCase()) < 0
    }.onFailure { if (it is CancellationException) throw it }
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
