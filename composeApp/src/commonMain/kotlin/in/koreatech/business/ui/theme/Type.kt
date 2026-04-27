package `in`.koreatech.business.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp
import koin_owner_mobile.composeapp.generated.resources.Res
import koin_owner_mobile.composeapp.generated.resources.pretendard_bold
import koin_owner_mobile.composeapp.generated.resources.pretendard_medium
import koin_owner_mobile.composeapp.generated.resources.pretendard_regular
import org.jetbrains.compose.resources.Font

@Immutable
data class KoinTypography(
    val regular10: TextStyle,
    val regular12: TextStyle,
    val regular13: TextStyle,
    val regular14: TextStyle,
    val regular15: TextStyle,
    val regular16: TextStyle,
    val regular18: TextStyle,
    val medium12: TextStyle,
    val medium13: TextStyle,
    val medium14: TextStyle,
    val medium15: TextStyle,
    val medium16: TextStyle,
    val medium18: TextStyle,
    val bold12: TextStyle,
    val bold13: TextStyle,
    val bold14: TextStyle,
    val bold15: TextStyle,
    val bold16: TextStyle,
    val bold18: TextStyle,
    val bold20: TextStyle,
    val bold28: TextStyle
)

@Composable
internal fun Pretendard(): FontFamily = FontFamily(
    Font(Res.font.pretendard_regular, FontWeight.Normal, FontStyle.Normal),
    Font(Res.font.pretendard_bold, FontWeight.Bold, FontStyle.Normal),
    Font(Res.font.pretendard_medium, FontWeight.Medium, FontStyle.Normal)
)

@Composable
internal fun DefaultTextStyle(): TextStyle = TextStyle(
    fontStyle = FontStyle.Normal,
    fontFamily = Pretendard(),
    lineHeightStyle =
    LineHeightStyle(
        alignment = LineHeightStyle.Alignment.Center,
        trim = LineHeightStyle.Trim.None
    ),
    letterSpacing = 0.sp
)

@Composable
internal fun RegularStyle1() = DefaultTextStyle().copy(
    fontSize = 10.sp,
    fontWeight = FontWeight.Normal,
    lineHeight = 16.sp
)

@Composable
internal fun RegularStyle2() = DefaultTextStyle().copy(
    fontSize = 12.sp,
    fontWeight = FontWeight.Normal,
    lineHeight = 19.2.sp
)

@Composable
internal fun RegularStyle3() = DefaultTextStyle().copy(
    fontSize = 13.sp,
    fontWeight = FontWeight.Normal,
    lineHeight = 20.8.sp
)

@Composable
internal fun RegularStyle4() = DefaultTextStyle().copy(
    fontSize = 14.sp,
    fontWeight = FontWeight.Normal,
    lineHeight = 22.4.sp
)

@Composable
internal fun RegularStyle5() = DefaultTextStyle().copy(
    fontSize = 15.sp,
    fontWeight = FontWeight.Normal,
    lineHeight = 24.sp
)

@Composable
internal fun RegularStyle6() = DefaultTextStyle().copy(
    fontSize = 16.sp,
    fontWeight = FontWeight.Normal,
    lineHeight = 25.6.sp
)

@Composable
internal fun RegularStyle7() = DefaultTextStyle().copy(
    fontSize = 18.sp,
    fontWeight = FontWeight.Normal,
    lineHeight = 28.8.sp
)

@Composable
internal fun MediumStyle1() = DefaultTextStyle().copy(
    fontSize = 12.sp,
    fontWeight = FontWeight.Medium,
    lineHeight = 19.2.sp
)

@Composable
internal fun MediumStyle2() = DefaultTextStyle().copy(
    fontSize = 13.sp,
    fontWeight = FontWeight.Medium,
    lineHeight = 20.8.sp
)

@Composable
internal fun MediumStyle3() = DefaultTextStyle().copy(
    fontSize = 14.sp,
    fontWeight = FontWeight.Medium,
    lineHeight = 22.4.sp
)

@Composable
internal fun MediumStyle4() = DefaultTextStyle().copy(
    fontSize = 15.sp,
    fontWeight = FontWeight.Medium,
    lineHeight = 24.sp
)

@Composable
internal fun MediumStyle5() = DefaultTextStyle().copy(
    fontSize = 16.sp,
    fontWeight = FontWeight.Medium,
    lineHeight = 25.6.sp
)

@Composable
internal fun MediumStyle6() = DefaultTextStyle().copy(
    fontSize = 18.sp,
    fontWeight = FontWeight.Medium,
    lineHeight = 28.8.sp
)

@Composable
internal fun BoldStyle1() = DefaultTextStyle().copy(
    fontSize = 12.sp,
    fontWeight = FontWeight.Bold,
    lineHeight = 19.2.sp
)

@Composable
internal fun BoldStyle2() = DefaultTextStyle().copy(
    fontSize = 13.sp,
    fontWeight = FontWeight.Bold,
    lineHeight = 20.8.sp
)

@Composable
internal fun BoldStyle3() = DefaultTextStyle().copy(
    fontSize = 14.sp,
    fontWeight = FontWeight.Bold,
    lineHeight = 22.4.sp
)

@Composable
internal fun BoldStyle4() = DefaultTextStyle().copy(
    fontSize = 15.sp,
    fontWeight = FontWeight.Bold,
    lineHeight = 24.sp
)

@Composable
internal fun BoldStyle5() = DefaultTextStyle().copy(
    fontSize = 16.sp,
    fontWeight = FontWeight.Bold,
    lineHeight = 25.6.sp
)

@Composable
internal fun BoldStyle6() = DefaultTextStyle().copy(
    fontSize = 18.sp,
    fontWeight = FontWeight.Bold,
    lineHeight = 28.8.sp
)

@Composable
internal fun BoldStyle7() = DefaultTextStyle().copy(
    fontSize = 20.sp,
    fontWeight = FontWeight.Bold,
    lineHeight = 30.sp
)

@Composable
internal fun BoldStyle8() = DefaultTextStyle().copy(
    fontSize = 28.sp,
    fontWeight = FontWeight.Bold,
    lineHeight = 33.6.sp
)
