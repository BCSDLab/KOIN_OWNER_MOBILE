package `in`.koreatech.business.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import `in`.koreatech.business.ui.theme.KoinTheme

@Composable
fun KoinProgressHeader(
    currentStep: Int,
    totalSteps: Int,
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$currentStep. $title",
                style = KoinTheme.typography.medium16,
                color = KoinTheme.colors.primary500,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "$currentStep / $totalSteps",
                style = KoinTheme.typography.medium16,
                color = KoinTheme.colors.primary500
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(KoinTheme.colors.neutral300)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = currentStep.toFloat() / totalSteps.toFloat())
                    .height(6.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(KoinTheme.colors.primary500)
            )
        }
        if (subtitle != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = subtitle,
                style = KoinTheme.typography.regular14,
                color = KoinTheme.colors.neutral500
            )
        }
    }
}
