package `in`.koreatech.business.domain.usecase.preferences

import `in`.koreatech.business.domain.model.ThemeMode
import `in`.koreatech.business.domain.repository.AppPreferencesRepository

class SetThemeModeUseCase(private val repository: AppPreferencesRepository) {
    suspend operator fun invoke(mode: ThemeMode) = repository.setThemeMode(mode)
}
