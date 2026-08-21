package me.sensta.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Nubo 기본 스킨의 따뜻한 중성 표면과 테라코타 포인트를 Android 의미 색상에 대응한다.
internal val NuboLightColorScheme = lightColorScheme(
    primary = Color(0xFFB2583A),
    onPrimary = Color(0xFFFFF8F2),
    primaryContainer = Color(0xFFEFDECD),
    onPrimaryContainer = Color(0xFF3F1E14),
    secondary = Color(0xFF73645A),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFEDE5D9),
    onSecondaryContainer = Color(0xFF30261F),
    tertiary = Color(0xFF776456),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFF2E3D6),
    onTertiaryContainer = Color(0xFF30231B),
    error = Color(0xFFB3261E),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFF8F3EB),
    onBackground = Color(0xFF2A211B),
    surface = Color(0xFFFDFBF6),
    onSurface = Color(0xFF2A211B),
    surfaceVariant = Color(0xFFF2ECE3),
    onSurfaceVariant = Color(0xFF73645A),
    outline = Color(0xFF9A8C80),
    outlineVariant = Color(0xFFDAD1C6),
    inverseSurface = Color(0xFF342B25),
    inverseOnSurface = Color(0xFFF8F3EB),
    inversePrimary = Color(0xFFDB8F6C),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFBF7F1),
    surfaceContainer = Color(0xFFF2ECE3),
    surfaceContainerHigh = Color(0xFFEDE5D9),
    surfaceContainerHighest = Color(0xFFE5DDD3)
)

internal val NuboDarkColorScheme = darkColorScheme(
    primary = Color(0xFFDB8F6C),
    onPrimary = Color(0xFF30170F),
    primaryContainer = Color(0xFF5B3022),
    onPrimaryContainer = Color(0xFFFFDBCA),
    secondary = Color(0xFFA2988E),
    onSecondary = Color(0xFF2A211B),
    secondaryContainer = Color(0xFF403630),
    onSecondaryContainer = Color(0xFFECE2D8),
    tertiary = Color(0xFFD2BCAA),
    onTertiary = Color(0xFF38291F),
    tertiaryContainer = Color(0xFF504033),
    onTertiaryContainer = Color(0xFFF0DCCB),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF1C1612),
    onBackground = Color(0xFFEBE5DE),
    surface = Color(0xFF261F1A),
    onSurface = Color(0xFFEBE5DE),
    surfaceVariant = Color(0xFF2D241F),
    onSurfaceVariant = Color(0xFFA2988E),
    outline = Color(0xFF887A70),
    outlineVariant = Color(0xFF453B34),
    inverseSurface = Color(0xFFEBE5DE),
    inverseOnSurface = Color(0xFF2A211B),
    inversePrimary = Color(0xFFB2583A),
    surfaceContainerLowest = Color(0xFF17110E),
    surfaceContainerLow = Color(0xFF221B17),
    surfaceContainer = Color(0xFF2D241F),
    surfaceContainerHigh = Color(0xFF322924),
    surfaceContainerHighest = Color(0xFF403630)
)

internal val SenstaShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

@Immutable
data class SenstaExtendedColors(
    val media: Color,
    val onMedia: Color,
    val success: Color,
    val warning: Color
)

internal val LocalSenstaExtendedColors = staticCompositionLocalOf {
    SenstaExtendedColors(
        media = Color(0xFF131416),
        onMedia = Color(0xFFF4F1ED),
        success = Color(0xFF31855C),
        warning = Color(0xFFB97716)
    )
}
