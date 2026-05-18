package `in`.koreatech.business

import `in`.koreatech.business.domain.model.ThemeMode
import `in`.koreatech.business.domain.model.owner.OwnerProfile
import `in`.koreatech.business.domain.model.owner.OwnerStore
import `in`.koreatech.business.domain.model.signup.ShopSearchResult
import `in`.koreatech.business.domain.repository.ActiveStoreRepository
import `in`.koreatech.business.domain.repository.AppPreferencesRepository
import `in`.koreatech.business.domain.repository.AuthRepository
import `in`.koreatech.business.domain.repository.OwnerRepository
import `in`.koreatech.business.domain.repository.TokenRepository
import `in`.koreatech.business.domain.error.DomainError
import `in`.koreatech.business.domain.usecase.auth.DeleteAccountUseCase
import `in`.koreatech.business.domain.usecase.owner.GetOwnerProfileUseCase
import `in`.koreatech.business.domain.usecase.owner.GetRequiredVersionUseCase
import `in`.koreatech.business.domain.usecase.preferences.ObserveThemeModeUseCase
import `in`.koreatech.business.domain.usecase.store.SetActiveStoreIdUseCase
import `in`.koreatech.business.domain.usecase.token.ClearTokensUseCase
import `in`.koreatech.business.domain.usecase.token.GetAccessTokenUseCase
import `in`.koreatech.business.domain.usecase.token.ObserveAccessTokenUseCase
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withTimeoutOrNull

/**
 * AppViewModel의 sessionExpired 이벤트 경로를 실제 인스턴스로 검증한다.
 * Fake 리포지토리들을 직접 주입해 토큰 변화를 흉내내고, observer가 의도한 만큼
 * 정확한 시점에 sessionExpired를 emit하는지를 채널 timeout으로 측정.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AppViewModelSessionTest {
    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun newViewModel(
        initialAccessToken: String = "",
        ownerProfile: suspend () -> OwnerProfile = { OwnerProfile("owner", "owner@test.com", null) }
    ): Pair<AppViewModel, FakeTokenRepository> {
        val tokenRepo = FakeTokenRepository(access = initialAccessToken)
        val ownerRepo = FakeOwnerRepository(onGetOwnerProfile = ownerProfile)
        val authRepo = FakeAuthRepository()
        val activeStoreRepo = FakeActiveStoreRepository()
        val prefsRepo = FakeAppPreferencesRepository()
        val vm = AppViewModel(
            deleteAccountUseCase = DeleteAccountUseCase(authRepo),
            getRequiredVersionUseCase = GetRequiredVersionUseCase(ownerRepo),
            getAccessTokenUseCase = GetAccessTokenUseCase(tokenRepo),
            getOwnerProfileUseCase = GetOwnerProfileUseCase(ownerRepo),
            observeAccessTokenUseCase = ObserveAccessTokenUseCase(tokenRepo),
            clearTokensUseCase = ClearTokensUseCase(tokenRepo),
            setActiveStoreIdUseCase = SetActiveStoreIdUseCase(activeStoreRepo),
            observeThemeModeUseCase = ObserveThemeModeUseCase(prefsRepo)
        )
        return vm to tokenRepo
    }

    @Test
    fun initialBlankTokenDoesNotEmitSessionExpired() = runTest {
        val (vm, _) = newViewModel(initialAccessToken = "")
        advanceUntilIdle()
        // 첫 emission(저장된 빈 토큰)은 initial로 소비되어야 함.
        val event = withTimeoutOrNull(50) { vm.sessionExpired.firstOrNull() }
        assertNull(event, "Initial blank token must not trigger sessionExpired")
    }

    @Test
    fun initialNonBlankTokenDoesNotEmitSessionExpired() = runTest {
        val (vm, _) = newViewModel(initialAccessToken = "valid-token")
        advanceUntilIdle()
        val event = withTimeoutOrNull(50) { vm.sessionExpired.firstOrNull() }
        assertNull(event, "Initial non-blank token must not trigger sessionExpired")
    }

    @Test
    fun nonBlankToBlankTransitionEmitsSessionExpiredOnce() = runTest {
        val (vm, repo) = newViewModel(initialAccessToken = "valid-token")
        advanceUntilIdle()
        // 토큰 클리어(refresh 영구 실패 흉내)
        repo.saveAccessToken("")
        advanceUntilIdle()
        val event = withTimeoutOrNull(200) { vm.sessionExpired.first() }
        assertNotNull(event, "Cleared token must trigger sessionExpired")
    }

    @Test
    fun consecutiveBlankWritesEmitOnlyOneSessionExpired() = runTest {
        val (vm, repo) = newViewModel(initialAccessToken = "valid-token")
        advanceUntilIdle()
        // 같은 빈 값 3번 저장 → distinctUntilChanged로 한 번만 emit
        repeat(3) { repo.saveAccessToken("") }
        advanceUntilIdle()
        // 첫 sessionExpired 소비
        val first = withTimeoutOrNull(200) { vm.sessionExpired.first() }
        assertNotNull(first)
        // 두 번째는 없어야 함(Channel은 1건만 보존, distinctUntilChanged로 추가 emit X)
        val second = withTimeoutOrNull(50) { vm.sessionExpired.firstOrNull() }
        assertNull(second, "Duplicate blank writes must not emit additional sessionExpired")
    }

    @Test
    fun loginThenLogoutCycleEmitsExactlyOneSessionExpiredPerLogout() = runTest {
        val (vm, repo) = newViewModel(initialAccessToken = "")
        advanceUntilIdle()
        // 로그인: 빈 토큰 → 새 토큰
        repo.saveAccessToken("first-token")
        advanceUntilIdle()
        val noEventOnLogin = withTimeoutOrNull(50) { vm.sessionExpired.firstOrNull() }
        assertNull(noEventOnLogin, "Login must not trigger sessionExpired")

        // 로그아웃: 새 토큰 → 빈 토큰
        repo.saveAccessToken("")
        advanceUntilIdle()
        val logoutEvent = withTimeoutOrNull(200) { vm.sessionExpired.first() }
        assertNotNull(logoutEvent, "Logout must trigger sessionExpired")

        // 재로그인: 빈 → 새
        repo.saveAccessToken("second-token")
        advanceUntilIdle()
        val noEventOnRelogin = withTimeoutOrNull(50) { vm.sessionExpired.firstOrNull() }
        assertNull(noEventOnRelogin, "Re-login must not trigger sessionExpired")

        // 재로그아웃: 새 → 빈
        repo.saveAccessToken("")
        advanceUntilIdle()
        val secondLogoutEvent = withTimeoutOrNull(200) { vm.sessionExpired.first() }
        assertNotNull(secondLogoutEvent, "Second logout must trigger sessionExpired")
    }

    @Test
    fun channelPreservesEventForLateSubscriber() = runTest {
        // Channel(replay-equivalent) 검증: collector가 부재할 때 emit된 이벤트가
        // 이후 첫 collector에게 drain되는지 확인. SharedFlow(replay=0)는 이를 못 함.
        val (vm, repo) = newViewModel(initialAccessToken = "valid-token")
        advanceUntilIdle()
        // collector가 없는 상태에서 토큰 클리어
        repo.saveAccessToken("")
        advanceUntilIdle()
        // 그 후 처음으로 collector를 붙임 — Channel 버퍼에서 event를 받을 수 있어야 함
        val event = withTimeoutOrNull(200) { vm.sessionExpired.first() }
        assertNotNull(event, "Channel must deliver buffered event to late subscriber")
    }

    @Test
    fun refreshLaunchStateFallsBackToUnauthenticatedOnIOException() = runTest {
        // getAccessToken()이 IOException을 던지면 hasValidOwnerSession은
        // false를 반환해야 하고, 결과적으로 launchState는 Unauthenticated로 안착.
        val tokenRepo = GetAccessTokenThrowingRepository()
        val ownerRepo = FakeOwnerRepository()
        val authRepo = FakeAuthRepository()
        val activeStoreRepo = FakeActiveStoreRepository()
        val prefsRepo = FakeAppPreferencesRepository()
        val vm = AppViewModel(
            deleteAccountUseCase = DeleteAccountUseCase(authRepo),
            getRequiredVersionUseCase = GetRequiredVersionUseCase(ownerRepo),
            getAccessTokenUseCase = GetAccessTokenUseCase(tokenRepo),
            getOwnerProfileUseCase = GetOwnerProfileUseCase(ownerRepo),
            observeAccessTokenUseCase = ObserveAccessTokenUseCase(tokenRepo),
            clearTokensUseCase = ClearTokensUseCase(tokenRepo),
            setActiveStoreIdUseCase = SetActiveStoreIdUseCase(activeStoreRepo),
            observeThemeModeUseCase = ObserveThemeModeUseCase(prefsRepo)
        )
        advanceUntilIdle()
        assertEquals(
            LaunchState.Unauthenticated,
            vm.launchState.value,
            "DataStore I/O error during getAccessToken must fallthrough to Unauthenticated, not stick on Loading"
        )
    }

    @Test
    fun observerSurvivesIOExceptionFromTokenFlow() = runTest {
        // observeAccessToken() flow가 IOException을 던지더라도 (DataStore 손상 시뮬)
        // .catch 블록이 흡수해 어떤 sessionExpired emit도 발생하지 않아야 한다.
        val tokenRepo = FlowThrowingTokenRepository()
        val ownerRepo = FakeOwnerRepository()
        val authRepo = FakeAuthRepository()
        val activeStoreRepo = FakeActiveStoreRepository()
        val prefsRepo = FakeAppPreferencesRepository()
        val vm = AppViewModel(
            deleteAccountUseCase = DeleteAccountUseCase(authRepo),
            getRequiredVersionUseCase = GetRequiredVersionUseCase(ownerRepo),
            getAccessTokenUseCase = GetAccessTokenUseCase(tokenRepo),
            getOwnerProfileUseCase = GetOwnerProfileUseCase(ownerRepo),
            observeAccessTokenUseCase = ObserveAccessTokenUseCase(tokenRepo),
            clearTokensUseCase = ClearTokensUseCase(tokenRepo),
            setActiveStoreIdUseCase = SetActiveStoreIdUseCase(activeStoreRepo),
            observeThemeModeUseCase = ObserveThemeModeUseCase(prefsRepo)
        )
        advanceUntilIdle()
        val event = withTimeoutOrNull(50) { vm.sessionExpired.firstOrNull() }
        assertNull(event, "DataStore I/O error must not synthesize a sessionExpired event")
    }

    @Test
    fun launchStateTransitionsToUnauthenticatedOnTokenClear() = runTest {
        val (vm, repo) = newViewModel(initialAccessToken = "valid-token")
        advanceUntilIdle()
        // 초기 launchState 확인
        assertEquals(LaunchState.Authenticated, vm.launchState.value)
        // 토큰 클리어
        repo.saveAccessToken("")
        advanceUntilIdle()
        assertEquals(
            LaunchState.Unauthenticated,
            vm.launchState.value,
            "launchState must reflect Unauthenticated after token clear"
        )
    }

    @Test
    fun validTokenWithSuccessfulProfileResolvesAuthenticated() = runTest {
        // 토큰이 있고 /owner 검증이 성공하면 그대로 인증됨.
        val (vm, _) = newViewModel(
            initialAccessToken = "valid-token",
            ownerProfile = { OwnerProfile("owner", "owner@test.com", null) }
        )
        advanceUntilIdle()
        assertEquals(LaunchState.Authenticated, vm.launchState.value)
    }

    @Test
    fun validTokenWith401ResolvesUnauthenticated() = runTest {
        // 토큰은 있으나 서버 검증이 401(DomainError.Auth)이면 미인증으로 분기 →
        // Loading에서 곧장 AuthGraph로(StoreGraph 깜빡임 없음).
        val (vm, _) = newViewModel(
            initialAccessToken = "expired-token",
            ownerProfile = { throw DomainError.Auth("인증이 필요합니다.") }
        )
        advanceUntilIdle()
        assertEquals(
            LaunchState.Unauthenticated,
            vm.launchState.value,
            "Expired token (401 on /owner) must resolve to Unauthenticated, not Authenticated"
        )
    }

    @Test
    fun validTokenWithNetworkErrorResolvesAuthenticated() = runTest {
        // 토큰이 있고 검증이 네트워크 오류(비-Auth)면 토큰을 유지하고 낙관적으로
        // 인증됨 — 오프라인/일시 장애로 로그인 화면에 튕기지 않게 한다.
        val (vm, _) = newViewModel(
            initialAccessToken = "valid-token",
            ownerProfile = { throw DomainError.Network("서버 오류가 발생했습니다.") }
        )
        advanceUntilIdle()
        assertEquals(
            LaunchState.Authenticated,
            vm.launchState.value,
            "Non-auth network error must not force logout when a token is present"
        )
    }
}

/**
 * observeAccessToken의 flow만 IOException을 던지는 fake — 본 fix 범위(1번)에 한정.
 * getAccessToken은 정상 ""를 반환해 refreshLaunchState 경로의 다른 IOException 처리
 * 누락(별도 영역)이 본 테스트에 섞이지 않도록 격리.
 */
private class FlowThrowingTokenRepository : TokenRepository {
    override suspend fun getAccessToken(): String = ""
    override fun observeAccessToken(): Flow<String> = flow {
        throw kotlinx.io.IOException("DataStore corrupt")
    }
    override suspend fun saveAccessToken(accessToken: String) = Unit
    override suspend fun getRefreshToken(): String = ""
    override suspend fun saveRefreshToken(refreshToken: String) = Unit
}

/**
 * getAccessToken만 IOException을 던지는 fake — refreshLaunchState 경로의
 * IOException 흡수를 단독 검증. observeAccessToken은 빈 값을 emit해
 * observer 측 catch와 분리해서 테스트 가능.
 */
private class GetAccessTokenThrowingRepository : TokenRepository {
    override suspend fun getAccessToken(): String = throw kotlinx.io.IOException("DataStore corrupt")
    override fun observeAccessToken(): Flow<String> = MutableStateFlow("")
    override suspend fun saveAccessToken(accessToken: String) = Unit
    override suspend fun getRefreshToken(): String = ""
    override suspend fun saveRefreshToken(refreshToken: String) = Unit
}

private class FakeTokenRepository(access: String) : TokenRepository {
    private val accessFlow = MutableStateFlow(access)
    private val refreshFlow = MutableStateFlow("")
    override suspend fun getAccessToken(): String = accessFlow.value

    // StateFlow는 이미 distinctUntilChanged 동작을 내장. 본래 구현인
    // TokenLocalDataSource는 DataStore.data에 distinctUntilChanged를 명시적으로
    // 적용하지만, 여기 fake는 StateFlow라 동등 효과.
    override fun observeAccessToken(): Flow<String> = accessFlow
    override suspend fun saveAccessToken(accessToken: String) {
        accessFlow.value = accessToken
    }
    override suspend fun getRefreshToken(): String = refreshFlow.value
    override suspend fun saveRefreshToken(refreshToken: String) {
        refreshFlow.value = refreshToken
    }
}

private class FakeOwnerRepository(
    private val onGetOwnerProfile: suspend () -> OwnerProfile =
        { OwnerProfile("owner", "owner@test.com", null) }
) : OwnerRepository {
    override suspend fun getShopList(): List<OwnerStore> = emptyList()
    override suspend fun getOwnerProfile(): OwnerProfile = onGetOwnerProfile()
    override suspend fun getRequiredVersion(): String = "1.0.0"
    override suspend fun uploadFile(fileName: String, mimeType: String, bytes: ByteArray): String = ""
    override suspend fun searchShops(query: String): List<ShopSearchResult> = emptyList()
}

private class FakeAuthRepository : AuthRepository {
    override suspend fun signIn(phoneNumber: String, password: String) = Unit
    override suspend fun signOut() = Unit
    override suspend fun deleteAccount() = Unit
    override suspend fun checkPhoneExists(phoneNumber: String): Boolean = false
    override suspend fun sendSignupSms(phoneNumber: String) = Unit
    override suspend fun verifySmsCode(phoneNumber: String, code: String): String = ""
    override suspend fun register(
        phoneNumber: String,
        password: String,
        name: String,
        companyNumber: String,
        shopNumber: String,
        shopId: Int?,
        shopName: String,
        attachmentUrls: List<String>
    ) = Unit
    override suspend fun sendFindPasswordSms(phoneNumber: String) = Unit
    override suspend fun verifyFindPasswordSms(phoneNumber: String, code: String) = Unit
    override suspend fun changePasswordBySms(phoneNumber: String, password: String) = Unit
}

private class FakeActiveStoreRepository : ActiveStoreRepository {
    private val state = MutableStateFlow<String?>(null)
    override val activeStoreId: Flow<String?> get() = state
    override suspend fun setActiveStoreId(id: String?) {
        state.value = id
    }
}

private class FakeAppPreferencesRepository : AppPreferencesRepository {
    private val theme = MutableStateFlow(ThemeMode.System)
    override val themeMode: Flow<ThemeMode> get() = theme
    override suspend fun setThemeMode(mode: ThemeMode) {
        theme.value = mode
    }
}
