package `in`.koreatech.business.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.koreatech.business.domain.model.ThemeMode
import `in`.koreatech.business.domain.repository.AppPreferencesRepository
import `in`.koreatech.business.domain.repository.OwnerRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.System,
    val ownerName: String = "",
    val ownerEmail: String = "",
    val isProfileLoading: Boolean = false,
    val profileError: String = ""
)

class SettingsViewModel(
    private val preferences: AppPreferencesRepository,
    private val ownerRepository: OwnerRepository
) : ViewModel(),
    ContainerHost<SettingsUiState, Nothing> {
    override val container = container<SettingsUiState, Nothing>(SettingsUiState())

    init {
        preferences.themeMode
            .onEach { mode -> intent { reduce { state.copy(themeMode = mode) } } }
            .launchIn(viewModelScope)
        loadOwnerProfile()
    }

    private fun loadOwnerProfile() = intent {
        reduce { state.copy(isProfileLoading = true, profileError = "") }
        try {
            val profile = ownerRepository.getOwnerProfile()
            reduce {
                state.copy(
                    ownerName = profile.name,
                    ownerEmail = profile.email,
                    isProfileLoading = false
                )
            }
        } catch (e: Exception) {
            reduce {
                state.copy(
                    isProfileLoading = false,
                    profileError = e.message.orEmpty()
                )
            }
        }
    }

    fun setThemeMode(mode: ThemeMode) = intent {
        preferences.setThemeMode(mode)
    }
}
