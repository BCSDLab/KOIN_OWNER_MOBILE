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
import org.orbitmvi.orbit.viewmodel.container

class SettingsViewModel(
    private val observeThemeModeUseCase: ObserveThemeModeUseCase,
    private val setThemeModeUseCase: SetThemeModeUseCase,
    private val getOwnerProfileUseCase: GetOwnerProfileUseCase
) : ViewModel(),
    ContainerHost<SettingsState, Nothing> {
    override val container = container<SettingsState, Nothing>(
        initialState = SettingsState(),
        onCreate = {
            observeThemeModeUseCase()
                .onEach { mode -> reduce { state.copy(themeMode = mode) } }
                .launchIn(viewModelScope)
            loadOwnerProfile()
        }
    )

    private fun loadOwnerProfile() = intent {
        reduce { state.copy(isProfileLoading = true, profileError = "") }
        getOwnerProfileUseCase()
            .onSuccess { profile -> applyProfile(profile.name, profile.email) }
            .onFailure { showProfileError(it.message.orEmpty()) }
    }

    private fun applyProfile(name: String, email: String) = intent {
        reduce { state.copy(ownerName = name, ownerEmail = email, isProfileLoading = false) }
    }

    private fun showProfileError(message: String) = intent {
        reduce { state.copy(isProfileLoading = false, profileError = message) }
    }

    fun setThemeMode(mode: ThemeMode) = intent {
        setThemeModeUseCase(mode)
    }
}
