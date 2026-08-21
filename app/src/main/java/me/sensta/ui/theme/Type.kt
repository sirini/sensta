package me.sensta.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import me.sensta.R

val robotoSlabFontFamily = FontFamily(
    Font(R.font.robotoslab_extra_light, FontWeight.ExtraLight),
    Font(R.font.robotoslab_light, FontWeight.Light),
    Font(R.font.robotoslab_thin, FontWeight.Thin),
    Font(R.font.robotoslab_regular, FontWeight.Normal),
    Font(R.font.robotoslab_medium, FontWeight.Medium),
    Font(R.font.robotoslab_semi_bold, FontWeight.SemiBold),
    Font(R.font.robotoslab_bold, FontWeight.Bold),
    Font(R.font.robotoslab_black, FontWeight.Black),
    Font(R.font.robotoslab_extra_bold, FontWeight.ExtraBold),
)

// Pretendard 폰트 패밀리
val pretendardFontFamily = FontFamily(
    Font(R.font.pretendard_extra_light, FontWeight.ExtraLight),
    Font(R.font.pretendard_light, FontWeight.Light),
    Font(R.font.pretendard_thin, FontWeight.Thin),
    Font(R.font.pretendard_regular, FontWeight.Normal),
    Font(R.font.pretendard_medium, FontWeight.Medium),
    Font(R.font.pretendard_semi_bold, FontWeight.SemiBold),
    Font(R.font.pretendard_bold, FontWeight.Bold),
    Font(R.font.pretendard_black, FontWeight.Black),
    Font(R.font.pretendard_extra_bold, FontWeight.ExtraBold),
)

// Default Material 3 typography values
val baseline = Typography()

val AppTypography = Typography(
    displayLarge = baseline.displayLarge.copy(
        fontFamily = pretendardFontFamily, fontWeight = FontWeight.Bold
    ),
    displayMedium = baseline.displayMedium.copy(
        fontFamily = pretendardFontFamily, fontWeight = FontWeight.Bold
    ),
    displaySmall = baseline.displaySmall.copy(
        fontFamily = pretendardFontFamily, fontWeight = FontWeight.SemiBold
    ),
    headlineLarge = baseline.headlineLarge.copy(
        fontFamily = pretendardFontFamily, fontWeight = FontWeight.Bold
    ),
    headlineMedium = baseline.headlineMedium.copy(
        fontFamily = pretendardFontFamily, fontWeight = FontWeight.Bold
    ),
    headlineSmall = baseline.headlineSmall.copy(
        fontFamily = pretendardFontFamily, fontWeight = FontWeight.Medium
    ),
    titleLarge = baseline.titleLarge.copy(
        fontFamily = pretendardFontFamily, fontWeight = FontWeight.Bold
    ),
    titleMedium = baseline.titleMedium.copy(
        fontFamily = pretendardFontFamily, fontWeight = FontWeight.SemiBold
    ),
    titleSmall = baseline.titleSmall.copy(
        fontFamily = pretendardFontFamily, fontWeight = FontWeight.Medium
    ),
    bodyLarge = baseline.bodyLarge.copy(
        fontFamily = pretendardFontFamily, fontWeight = FontWeight.Normal
    ),
    bodyMedium = baseline.bodyMedium.copy(
        fontFamily = pretendardFontFamily, fontWeight = FontWeight.Normal
    ),
    bodySmall = baseline.bodySmall.copy(
        fontFamily = pretendardFontFamily, fontWeight = FontWeight.Normal
    ),
    labelLarge = baseline.labelLarge.copy(
        fontFamily = pretendardFontFamily, fontWeight = FontWeight.Medium
    ),
    labelMedium = baseline.labelMedium.copy(
        fontFamily = pretendardFontFamily, fontWeight = FontWeight.Normal
    ),
    labelSmall = baseline.labelSmall.copy(
        fontFamily = pretendardFontFamily, fontWeight = FontWeight.Normal
    ),
)
