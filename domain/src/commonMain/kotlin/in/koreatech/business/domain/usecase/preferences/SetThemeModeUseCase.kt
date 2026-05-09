package `in`.koreatech.business.domain.usecase.preferences

import `in`.koreatech.business.domain.model.ThemeMode
import `in`.koreatech.business.domain.repository.AppPreferencesRepository
import `in`.koreatech.business.domain.util.runCatchingCancellable

class SetThemeModeUseCase(private val repository: AppPreferencesRepository) {
    suspend operator fun invoke(mode: ThemeMode): Result<Unit> = runCatchingCancellable {
        repository.setThemeMode(mode)
    }
}
