package `in`.koreatech.business.feature.settings

import `in`.koreatech.business.domain.error.DomainError
import `in`.koreatech.business.domain.model.ThemeMode
import `in`.koreatech.business.domain.model.owner.OwnerProfile
import `in`.koreatech.business.domain.model.owner.OwnerStore
import `in`.koreatech.business.domain.model.signup.ShopSearchResult
import `in`.koreatech.business.domain.repository.AppPreferencesRepository
import `in`.koreatech.business.domain.repository.OwnerRepository
import `in`.koreatech.business.domain.usecase.owner.GetOwnerProfileUseCase
import `in`.koreatech.business.domain.usecase.preferences.ObserveThemeModeUseCase
import `in`.koreatech.business.domain.usecase.preferences.SetThemeModeUseCase
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.orbitmvi.orbit.test.test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun newViewModel(
        initialTheme: ThemeMode = ThemeMode.System,
        profile: OwnerProfile = OwnerProfile(name = "테스트 사장님", email = "owner@example.com", companyNumber = null),
        profileError: DomainError? = null
    ): Pair<SettingsViewModel, FakeAppPreferencesRepository> {
        val prefs = FakeAppPreferencesRepository(initialTheme)
        val owner = FakeOwnerRepository(profile, profileError)
        val vm = SettingsViewModel(
            observeThemeModeUseCase = ObserveThemeModeUseCase(prefs),
            setThemeModeUseCase = SetThemeModeUseCase(prefs),
            getOwnerProfileUseCase = GetOwnerProfileUseCase(owner)
        )
        return vm to prefs
    }

    @Test
    fun loadOwnerProfileOnInit() = runTest {
        val (vm, _) = newViewModel(
            profile = OwnerProfile(name = "홍길동", email = "hong@example.com", companyNumber = "1234567890")
        )
        vm.test(this, SettingsState()) {
            runOnCreate()
            expectState { copy(isProfileLoading = true, profileError = "") }
            expectState {
                copy(ownerName = "홍길동", ownerEmail = "hong@example.com", isProfileLoading = false)
            }
        }
    }

    @Test
    fun setThemeModePropagatesThroughObserveFlow() = runTest {
        val (vm, prefs) = newViewModel(initialTheme = ThemeMode.System)
        vm.test(this, SettingsState()) {
            runOnCreate()
            expectState { copy(isProfileLoading = true, profileError = "") }
            expectState {
                copy(ownerName = "테스트 사장님", ownerEmail = "owner@example.com", isProfileLoading = false)
            }
            containerHost.setThemeMode(ThemeMode.Dark)
            expectState { copy(themeMode = ThemeMode.Dark) }
            assertEquals(ThemeMode.Dark, prefs.currentTheme())
        }
    }

    @Test
    fun profileLoadFailureSurfacesErrorMessage() = runTest {
        val (vm, _) = newViewModel(profileError = DomainError.Network("프로필 로드 실패"))
        vm.test(this, SettingsState()) {
            runOnCreate()
            expectState { copy(isProfileLoading = true, profileError = "") }
            expectState { copy(isProfileLoading = false, profileError = "프로필 로드 실패") }
        }
    }
}

private class FakeAppPreferencesRepository(initial: ThemeMode) : AppPreferencesRepository {
    private val state = MutableStateFlow(initial)
    override val themeMode: Flow<ThemeMode> get() = state
    override suspend fun setThemeMode(mode: ThemeMode) {
        state.value = mode
    }

    fun currentTheme(): ThemeMode = state.value
}

private class FakeOwnerRepository(
    private val profile: OwnerProfile,
    private val profileError: DomainError? = null
) : OwnerRepository {
    override suspend fun getShopList(): List<OwnerStore> = emptyList()
    override suspend fun getOwnerProfile(): OwnerProfile {
        profileError?.let { throw it }
        return profile
    }
    override suspend fun getRequiredVersion(): String = ""
    override suspend fun uploadFile(fileName: String, mimeType: String, bytes: ByteArray): String = ""
    override suspend fun searchShops(query: String): List<ShopSearchResult> = emptyList()
}
