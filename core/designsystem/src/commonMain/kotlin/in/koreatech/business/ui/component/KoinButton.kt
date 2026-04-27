package `in`.koreatech.business.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.koreatech.business.ui.theme.KoinTheme

enum class KoinButtonVariant { Primary, Outlined, Ghost }
enum class KoinButtonSize(val height: Dp, val padding: Dp, val fontSize: Int) {
    Small(36.dp, 12.dp, 13),
    Default(48.dp, 20.dp, 15),
    Large(56.dp, 24.dp, 16)
}

@Composable
fun KoinButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: KoinButtonVariant = KoinButtonVariant.Primary,
    size: KoinButtonSize = KoinButtonSize.Default,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    val shape = RoundedCornerShape(12.dp)
    val (bg, contentColor, borderColor) = when (variant) {
        KoinButtonVariant.Primary -> Triple(
            if (enabled) KoinTheme.colors.primary500 else KoinTheme.colors.neutral300,
            if (enabled) KoinTheme.colors.neutral0 else KoinTheme.colors.neutral500,
            Color.Transparent
        )
        KoinButtonVariant.Outlined -> Triple(
            KoinTheme.colors.neutral0,
            KoinTheme.colors.neutral800,
            KoinTheme.colors.neutral400
        )
        KoinButtonVariant.Ghost -> Triple(
            Color.Transparent,
            KoinTheme.colors.neutral800Variant,
            Color.Transparent
        )
    }

    Box(
        modifier = modifier
            .defaultMinSize(minHeight = size.height)
            .height(size.height)
            .clip(shape)
            .background(bg)
            .then(if (borderColor != Color.Transparent) Modifier.border(1.dp, borderColor, shape) else Modifier)
            .clickable(enabled = enabled && !isLoading, onClick = onClick)
            .padding(horizontal = size.padding),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                color = contentColor,
                strokeWidth = 2.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (leadingIcon != null) {
                    leadingIcon()
                }
                Text(
                    text = text,
                    color = contentColor,
                    fontSize = size.fontSize.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.2).sp
                )
            }
        }
    }
}
