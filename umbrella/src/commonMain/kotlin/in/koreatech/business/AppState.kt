package `in`.koreatech.business

import `in`.koreatech.business.domain.model.ThemeMode

enum class LaunchState {
    Loading,
    RequiresUpdate,
    Authenticated,
    Unauthenticated
}

data class AppState(
    val themeMode: ThemeMode = ThemeMode.System
)
