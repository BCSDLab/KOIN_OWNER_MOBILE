package `in`.koreatech.business.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.koreatech.business.ui.theme.KoinTheme

@Composable
fun DesktopTopBar(
    title: String,
    modifier: Modifier = Modifier,
    breadcrumb: String? = null,
    subtitle: String? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Column(modifier = modifier.fillMaxWidth().background(KoinTheme.colors.neutral50)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                if (breadcrumb != null) {
                    Text(
                        text = breadcrumb,
                        style = MaterialTheme.typography.bodySmall,
                        color = KoinTheme.colors.neutral500
                    )
                    Spacer(Modifier.height(4.dp))
                }
                Text(
                    text = title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = KoinTheme.colors.neutral800,
                    letterSpacing = (-0.5).sp
                )
                if (subtitle != null) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = KoinTheme.colors.neutral500
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                content = actions
            )
        }
        HorizontalDivider(color = KoinTheme.colors.neutral400)
    }
}
