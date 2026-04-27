package `in`.koreatech.business.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import `in`.koreatech.business.ui.theme.KoinTheme

@Composable
fun KoinCard(
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(24.dp),
    spacing: Dp = 12.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(12.dp)
    Column(
        modifier = modifier
            .shadow(elevation = 1.dp, shape = shape, clip = false)
            .clip(shape)
            .background(KoinTheme.colors.neutral0)
            .border(1.dp, KoinTheme.colors.neutral400, shape)
            .padding(padding),
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        content()
    }
}
