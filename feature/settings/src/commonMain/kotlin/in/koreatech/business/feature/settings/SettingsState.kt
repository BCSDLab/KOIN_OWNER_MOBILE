package `in`.koreatech.business.feature.settings

import `in`.koreatech.business.domain.model.ThemeMode

data class SettingsState(
    val themeMode: ThemeMode = ThemeMode.System,
    val ownerName: String = "",
    val ownerEmail: String = "",
    val isProfileLoading: Boolean = false,
    val profileError: String = ""
)
