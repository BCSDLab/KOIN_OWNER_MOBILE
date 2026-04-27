package `in`.koreatech.business.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.koreatech.business.ui.theme.KoinTheme
import koreatech.business.designsystem.resources.*
import koreatech.business.designsystem.resources.Res
import org.jetbrains.compose.resources.stringResource

private val BrandPanelBg = Color(0xFF1A1235)
private val BrandGlow1 = Color(0x667C4DFF)
private val BrandGlow2 = Color(0x4D7C4DFF)

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
            .clipToBounds()
            .drawBehind {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(BrandGlow1, Color.Transparent),
                        center = Offset(size.width + 40f, size.height + 40f),
                        radius = size.minDimension * 0.6f
                    ),
                    center = Offset(size.width + 40f, size.height + 40f),
                    radius = size.minDimension * 0.6f
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(BrandGlow2, Color.Transparent),
                        center = Offset(-40f, size.height * 0.4f),
                        radius = size.minDimension * 0.45f
                    ),
                    center = Offset(-40f, size.height * 0.4f),
                    radius = size.minDimension * 0.45f
                )
            }
            .padding(horizontal = 36.dp, vertical = 40.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top: KOIN 사장님 wordmark (white)
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

            // Middle: tagline block (centered between wordmark and mockup)
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
                Spacer(Modifier.height(14.dp))
                Text(
                    text = stringResource(Res.string.desktop_shell_sub1),
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
                Text(
                    text = stringResource(Res.string.desktop_shell_sub2),
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }

            Spacer(Modifier.weight(1f))

            // Bottom: frosted glass dashboard mockup
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.06f))
                    .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MockStat(label = stringResource(Res.string.mock_stat_order), value = "24건", modifier = Modifier.weight(1f))
                    MockStat(label = stringResource(Res.string.mock_stat_menu), value = "18개", modifier = Modifier.weight(1f))
                }
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.White.copy(alpha = 0.06f))
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0x80CF5BFF))
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color.White.copy(alpha = 0.1f))
                    )
                }
            }
        }
    }
}

@Composable
private fun MockStat(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.1f))
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            color = Color.White.copy(alpha = 0.5f)
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}
