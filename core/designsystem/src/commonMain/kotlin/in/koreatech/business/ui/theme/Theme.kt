package `in`.koreatech.business.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle

internal val LocalKoinColorPalette =
    staticCompositionLocalOf {
        KoinLightColorPalette
    }

internal val LocalKoinTypography =
    staticCompositionLocalOf {
        KoinTypography(
            regular10 = TextStyle.Default,
            regular12 = TextStyle.Default,
            regular13 = TextStyle.Default,
            regular14 = TextStyle.Default,
            regular15 = TextStyle.Default,
            regular16 = TextStyle.Default,
            regular18 = TextStyle.Default,
            medium12 = TextStyle.Default,
            medium13 = TextStyle.Default,
            medium14 = TextStyle.Default,
            medium15 = TextStyle.Default,
            medium16 = TextStyle.Default,
            medium18 = TextStyle.Default,
            bold12 = TextStyle.Default,
            bold13 = TextStyle.Default,
            bold14 = TextStyle.Default,
            bold15 = TextStyle.Default,
            bold16 = TextStyle.Default,
            bold18 = TextStyle.Default,
            bold20 = TextStyle.Default,
            bold28 = TextStyle.Default
        )
    }

internal val LocalShapes =
    staticCompositionLocalOf {
        Shapes
    }

internal val LocalWindowSizeClass =
    staticCompositionLocalOf<WindowSizeClass> { WindowSizeClass.Compact }

@Composable
fun KoinTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    windowSizeClass: WindowSizeClass = WindowSizeClass.Compact,
    content: @Composable () -> Unit
) {
    val extendedColors =
        if (darkTheme) {
            KoinDarkColorPalette
        } else {
            KoinLightColorPalette
        }

    val colorScheme = ColorScheme(
        primary = extendedColors.primary500,
        onPrimary = extendedColors.neutral0,
        primaryContainer = extendedColors.primary600,
        onPrimaryContainer = extendedColors.primary100,
        inversePrimary = extendedColors.primary300,
        secondary = extendedColors.primary600,
        onSecondary = extendedColors.neutral0,
        secondaryContainer = extendedColors.primary200,
        onSecondaryContainer = extendedColors.neutral800,
        tertiary = extendedColors.success700,
        onTertiary = extendedColors.neutral0,
        tertiaryContainer = extendedColors.success200,
        onTertiaryContainer = extendedColors.success700,
        background = extendedColors.neutral50,
        onBackground = extendedColors.neutral800,
        surface = extendedColors.neutral50,
        onSurface = extendedColors.neutral800,
        surfaceVariant = extendedColors.neutral100,
        onSurfaceVariant = extendedColors.neutral700,
        surfaceTint = extendedColors.primary500,
        inverseSurface = extendedColors.neutral800,
        inverseOnSurface = extendedColors.neutral50,
        error = extendedColors.danger600,
        onError = extendedColors.neutral0,
        errorContainer = extendedColors.danger600.copy(alpha = 0.12f),
        onErrorContainer = extendedColors.danger700,
        outline = extendedColors.neutral500,
        outlineVariant = extendedColors.neutral400,
        scrim = extendedColors.neutral800.copy(alpha = 0.26f),
        surfaceBright = extendedColors.neutral50,
        surfaceContainer = extendedColors.neutral100,
        surfaceContainerHigh = extendedColors.neutral200,
        surfaceContainerHighest = extendedColors.neutral300,
        surfaceContainerLow = extendedColors.neutral100,
        surfaceContainerLowest = extendedColors.neutral0,
        surfaceDim = extendedColors.neutral300
    )

    val koinTypography = KoinTypography(
        regular10 = RegularStyle1(),
        regular12 = RegularStyle2(),
        regular13 = RegularStyle3(),
        regular14 = RegularStyle4(),
        regular15 = RegularStyle5(),
        regular16 = RegularStyle6(),
        regular18 = RegularStyle7(),
        medium12 = MediumStyle1(),
        medium13 = MediumStyle2(),
        medium14 = MediumStyle3(),
        medium15 = MediumStyle4(),
        medium16 = MediumStyle5(),
        medium18 = MediumStyle6(),
        bold12 = BoldStyle1(),
        bold13 = BoldStyle2(),
        bold14 = BoldStyle3(),
        bold15 = BoldStyle4(),
        bold16 = BoldStyle5(),
        bold18 = BoldStyle6(),
        bold20 = BoldStyle7(),
        bold28 = BoldStyle8()
    )

    val typography = Typography(
        displayLarge = koinTypography.bold28,
        displayMedium = koinTypography.bold28,
        headlineLarge = koinTypography.bold20,
        headlineMedium = koinTypography.bold18,
        titleLarge = koinTypography.medium18,
        titleMedium = koinTypography.medium16,
        titleSmall = koinTypography.medium14,
        bodyLarge = koinTypography.regular16,
        bodyMedium = koinTypography.regular14,
        bodySmall = koinTypography.regular12,
        labelLarge = koinTypography.bold16,
        labelMedium = koinTypography.medium14,
        labelSmall = koinTypography.medium12
    )

    CompositionLocalProvider(
        LocalKoinColorPalette provides extendedColors,
        LocalKoinTypography provides koinTypography,
        LocalShapes provides Shapes,
        LocalWindowSizeClass provides windowSizeClass
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            shapes = Shapes,
            content = content
        )
    }
}

object KoinTheme {
    val colors: KoinColorPalette
        @Composable
        @ReadOnlyComposable
        get() = LocalKoinColorPalette.current

    val typography: KoinTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalKoinTypography.current

    val shapes: Shapes
        @Composable
        @ReadOnlyComposable
        get() = LocalShapes.current

    val windowSizeClass: WindowSizeClass
        @Composable
        @ReadOnlyComposable
        get() = LocalWindowSizeClass.current
}
