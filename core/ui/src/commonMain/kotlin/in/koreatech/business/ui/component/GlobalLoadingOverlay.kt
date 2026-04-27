package `in`.koreatech.business.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun GlobalLoadingOverlay(
    visible: Boolean,
    modifier: Modifier = Modifier
) {
    if (!visible) return

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.24f)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
