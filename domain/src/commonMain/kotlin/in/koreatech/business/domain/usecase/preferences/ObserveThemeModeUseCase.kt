package `in`.koreatech.business.domain.usecase.preferences

import `in`.koreatech.business.domain.model.ThemeMode
import `in`.koreatech.business.domain.repository.AppPreferencesRepository
import kotlinx.coroutines.flow.Flow

class ObserveThemeModeUseCase(private val repository: AppPreferencesRepository) {
    operator fun invoke(): Flow<ThemeMode> = repository.themeMode
}
