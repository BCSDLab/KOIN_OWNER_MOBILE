package `in`.koreatech.business.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.koreatech.business.domain.model.ThemeMode
import `in`.koreatech.business.domain.usecase.owner.GetOwnerProfileUseCase
import `in`.koreatech.business.domain.usecase.preferences.ObserveThemeModeUseCase
import `in`.koreatech.business.domain.usecase.preferences.SetThemeModeUseCase
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.Syntax
import org.orbitmvi.orbit.viewmodel.container

data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.System,
    val ownerName: String = "",
    val ownerEmail: String = "",
    val isProfileLoading: Boolean = false,
    val profileError: String = ""
)

class SettingsViewModel(
    private val observeThemeModeUseCase: ObserveThemeModeUseCase,
    private val setThemeModeUseCase: SetThemeModeUseCase,
    private val getOwnerProfileUseCase: GetOwnerProfileUseCase
) : ViewModel(),
    ContainerHost<SettingsUiState, Nothing> {
    override val container = container<SettingsUiState, Nothing>(
        initialState = SettingsUiState(),
        onCreate = {
            observeThemeModeUseCase()
                .onEach { mode -> reduce { state.copy(themeMode = mode) } }
                .launchIn(viewModelScope)
            loadOwnerProfile()
        }
    )

    private suspend fun Syntax<SettingsUiState, Nothing>.loadOwnerProfile() {
        reduce { state.copy(isProfileLoading = true, profileError = "") }
        try {
            val profile = getOwnerProfileUseCase()
            reduce { state.copy(ownerName = profile.name, ownerEmail = profile.email, isProfileLoading = false) }
        } catch (e: Exception) {
            reduce { state.copy(isProfileLoading = false, profileError = e.message.orEmpty()) }
        }
    }

    fun setThemeMode(mode: ThemeMode) = intent {
        setThemeModeUseCase(mode)
    }
}
