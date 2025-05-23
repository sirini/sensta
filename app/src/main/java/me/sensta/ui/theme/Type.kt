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
        fontFamily = robotoSlabFontFamily, fontWeight = FontWeight.ExtraBold
    ),
    displayMedium = baseline.displayMedium.copy(
        fontFamily = robotoSlabFontFamily, fontWeight = FontWeight.Bold
    ),
    displaySmall = baseline.displaySmall.copy(
        fontFamily = robotoSlabFontFamily, fontWeight = FontWeight.Medium
    ),
    headlineLarge = baseline.headlineLarge.copy(
        fontFamily = pretendardFontFamily, fontWeight = FontWeight.ExtraBold
    ),
    headlineMedium = baseline.headlineMedium.copy(
        fontFamily = pretendardFontFamily, fontWeight = FontWeight.Bold
    ),
    headlineSmall = baseline.headlineSmall.copy(
        fontFamily = pretendardFontFamily, fontWeight = FontWeight.Medium
    ),
    titleLarge = baseline.titleLarge.copy(
        fontFamily = robotoSlabFontFamily, fontWeight = FontWeight.Bold
    ),
    titleMedium = baseline.titleMedium.copy(
        fontFamily = robotoSlabFontFamily, fontWeight = FontWeight.Medium
    ),
    titleSmall = baseline.titleSmall.copy(
        fontFamily = robotoSlabFontFamily, fontWeight = FontWeight.Thin
    ),
    bodyLarge = baseline.bodyLarge.copy(
        fontFamily = pretendardFontFamily, fontWeight = FontWeight.Medium /* Default */
    ),
    bodyMedium = baseline.bodyMedium.copy(
        fontFamily = pretendardFontFamily, fontWeight = FontWeight.Normal
    ),
    bodySmall = baseline.bodySmall.copy(
        fontFamily = pretendardFontFamily, fontWeight = FontWeight.Thin
    ),
    labelLarge = baseline.labelLarge.copy(
        fontFamily = pretendardFontFamily, fontWeight = FontWeight.Medium
    ),
    labelMedium = baseline.labelMedium.copy(
        fontFamily = pretendardFontFamily, fontWeight = FontWeight.Normal
    ),
    labelSmall = baseline.labelSmall.copy(
        fontFamily = pretendardFontFamily, fontWeight = FontWeight.Thin
    ),
)

