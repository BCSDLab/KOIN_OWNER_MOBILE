package `in`.koreatech.business.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.koreatech.business.ui.theme.KoinTheme
import koreatech.business.designsystem.resources.Res
import koreatech.business.designsystem.resources.desktop_shell_role
import koreatech.business.designsystem.resources.desktop_shell_tagline1
import koreatech.business.designsystem.resources.desktop_shell_tagline2
import koreatech.business.designsystem.resources.greeting
import org.jetbrains.compose.resources.stringResource

private val BrandPanelBg = Color(0xFF1A1235)

@Composable
fun DesktopAppShell(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        if (maxWidth < 960.dp) {
            content()
            return@BoxWithConstraints
        }

        Row(modifier = Modifier.fillMaxSize()) {
            BrandPanel(modifier = Modifier.width(340.dp).fillMaxHeight())

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(KoinTheme.colors.neutral50)
            ) {
                content()
            }
        }
    }
}

@Composable
private fun BrandPanel(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(BrandPanelBg)
            .padding(horizontal = 36.dp, vertical = 40.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "KOIN",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = (-0.5).sp
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(Res.string.greeting),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 3.dp)
                )
            }

            Spacer(Modifier.weight(1f))

            Column {
                Text(
                    text = stringResource(Res.string.desktop_shell_role),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.6f)
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = stringResource(Res.string.desktop_shell_tagline1),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 34.sp,
                    letterSpacing = (-0.5).sp,
                    color = Color.White
                )
                Text(
                    text = stringResource(Res.string.desktop_shell_tagline2),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 34.sp,
                    letterSpacing = (-0.5).sp,
                    color = Color.White
                )
            }

            Spacer(Modifier.weight(1f))
        }
    }
}
