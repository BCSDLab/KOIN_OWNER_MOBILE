package `in`.koreatech.business.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
sealed class WindowSizeClass {
    data object Compact : WindowSizeClass()
    data object Medium : WindowSizeClass()
    data object Expanded : WindowSizeClass()

    companion object {
        fun of(width: Dp): WindowSizeClass = when {
            width < 600.dp -> Compact
            width < 840.dp -> Medium
            else -> Expanded
        }
    }
}
