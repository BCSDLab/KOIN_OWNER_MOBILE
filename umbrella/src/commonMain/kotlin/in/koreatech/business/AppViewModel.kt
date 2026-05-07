package `in`.koreatech.business

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.koreatech.business.domain.model.ThemeMode
import `in`.koreatech.business.domain.usecase.auth.DeleteAccountUseCase
import `in`.koreatech.business.domain.usecase.owner.GetRequiredVersionUseCase
import `in`.koreatech.business.domain.usecase.preferences.ObserveThemeModeUseCase
import `in`.koreatech.business.domain.usecase.store.SetActiveStoreIdUseCase
import `in`.koreatech.business.domain.usecase.token.ClearTokensUseCase
import `in`.koreatech.business.domain.usecase.token.GetAccessTokenUseCase
import `in`.koreatech.business.platform.getAppVersion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

enum class LaunchState {
    Loading,
    RequiresUpdate,
    Authenticated,
    Unauthenticated
}

data class AppUiState(
    val themeMode: ThemeMode = ThemeMode.System
)

class AppViewModel(
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val getRequiredVersionUseCase: GetRequiredVersionUseCase,
    private val getAccessTokenUseCase: GetAccessTokenUseCase,
    private val clearTokensUseCase: ClearTokensUseCase,
    private val setActiveStoreIdUseCase: SetActiveStoreIdUseCase,
    private val observeThemeModeUseCase: ObserveThemeModeUseCase
) : ViewModel(),
    ContainerHost<AppUiState, Nothing> {
    override val container = container<AppUiState, Nothing>(AppUiState())

    private val _launchState = MutableStateFlow(LaunchState.Loading)
    val launchState: StateFlow<LaunchState> = _launchState.asStateFlow()

    init {
        observeThemeMode()
        refreshLaunchState()
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
            runCatching { setActiveStoreIdUseCase(null) }
            _launchState.value = LaunchState.Unauthenticated
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            runCatching { deleteAccountUseCase() }
            runCatching { clearTokensUseCase() }
            runCatching { setActiveStoreIdUseCase(null) }
            _launchState.value = LaunchState.Unauthenticated
        }
    }

    private suspend fun hasValidOwnerSession(): Boolean {
        val token = getAccessTokenUseCase().trim()
        return token.isNotBlank() && token.lowercase() != "null"
    }

    private suspend fun isForceUpdateRequired(): Boolean = try {
        compareVersions(getAppVersion(), getRequiredVersionUseCase()) < 0
    } catch (_: Exception) {
        false
    }

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
