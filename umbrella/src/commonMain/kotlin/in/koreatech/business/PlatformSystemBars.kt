package `in`.koreatech.business

import androidx.compose.runtime.Composable

/**
 * Aligns the platform's system bar (status bar / navigation bar) icon appearance with the
 * current app theme so that the bar icons remain visible regardless of the OS theme setting.
 */
@Composable
internal expect fun SyncSystemBarsAppearance(darkTheme: Boolean)
