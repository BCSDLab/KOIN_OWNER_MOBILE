package `in`.koreatech.business.ui.component

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import `in`.koreatech.business.ui.theme.KoinTheme

@Composable
fun BusinessSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    SnackbarHost(
        hostState = hostState,
        modifier = modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        snackbar = { data ->
            BusinessSnackbar(data = data)
        }
    )
}

@Composable
private fun BusinessSnackbar(
    data: SnackbarData
) {
    Snackbar(
        snackbarData = data,
        containerColor = KoinTheme.colors.neutral800,
        contentColor = KoinTheme.colors.neutral50,
        actionContentColor = KoinTheme.colors.primary200,
        dismissActionContentColor = KoinTheme.colors.neutral50
    )
}
