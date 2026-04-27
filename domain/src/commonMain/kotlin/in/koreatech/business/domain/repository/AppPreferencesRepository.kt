package `in`.koreatech.business.domain.repository

import `in`.koreatech.business.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface AppPreferencesRepository {
    val themeMode: Flow<ThemeMode>
    suspend fun setThemeMode(mode: ThemeMode)
}
