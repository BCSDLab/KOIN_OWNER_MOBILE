package `in`.koreatech.business.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircleOutline
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import `in`.koreatech.business.ui.theme.KoinTheme

enum class KoinTextFieldAlertType {
    Error,
    Warning,
    Success
}

@Composable
fun KoinTextFieldAlert(
    message: String,
    type: KoinTextFieldAlertType,
    modifier: Modifier = Modifier
) {
    val color: Color = when (type) {
        KoinTextFieldAlertType.Error -> KoinTheme.colors.danger600
        KoinTextFieldAlertType.Warning -> KoinTheme.colors.sub500
        KoinTextFieldAlertType.Success -> KoinTheme.colors.success700
    }
    val icon = when (type) {
        KoinTextFieldAlertType.Error -> Icons.Rounded.Warning
        KoinTextFieldAlertType.Warning -> Icons.Rounded.Warning
        KoinTextFieldAlertType.Success -> Icons.Rounded.CheckCircleOutline
    }

    Row(
        modifier = modifier.padding(top = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = color
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = message,
            color = color,
            style = KoinTheme.typography.regular12.copy(color = color)
        )
    }
}
