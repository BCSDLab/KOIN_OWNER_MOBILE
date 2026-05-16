package `in`.koreatech.business.feature.auth.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import `in`.koreatech.business.feature.auth.navigation.authGraph
import `in`.koreatech.business.ui.theme.KoinTheme
import `in`.koreatech.business.ui.theme.WindowSizeClass
import koreatech.business.designsystem.resources.Res
import koreatech.business.designsystem.resources.desktop_shell_role
import koreatech.business.designsystem.resources.desktop_shell_tagline1
import koreatech.business.designsystem.resources.desktop_shell_tagline2
import koreatech.business.designsystem.resources.greeting
import org.jetbrains.compose.resources.stringResource

private val BrandPanelBg = Color(0xFF1A1235)

/**
 * Auth 화면 전용 장식 Supporting Pane.
 *
 * 화면 너비가 [WindowSizeClass.Expanded] (>= 840dp) 일 때 왼쪽에 고정 너비(340dp)의 브랜드 패널
 * ([BrandPanel])을 표시하고 오른쪽에 [content]를 배치하는 2단 레이아웃을 제공한다. 그 외 너비에서는
 * [content]만 표시한다.
 *
 * **이 컴포저블은 네비게이션 항목을 보유하지 않는 순수 장식 패널이다.**
 * 네비게이션의 단일 소스(single source of navigation)는 store 모듈의
 * `MainTabScreen` (NavigationSuiteScaffold / NavigationRail 기반)이다.
 *
 * 사용 범위: [AuthNavigation.authGraph] 내 `AuthRoute.SignIn` 컴포저블 한정.
 */
@Composable
fun AuthBrandSupportingPane(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    if (KoinTheme.windowSizeClass is WindowSizeClass.Expanded) {
        Row(modifier = modifier.fillMaxSize()) {
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
    } else {
        Box(modifier = modifier.fillMaxSize()) {
            content()
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
