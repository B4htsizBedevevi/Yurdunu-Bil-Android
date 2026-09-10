package tr.yurdunubil.app

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Yurdunu Bil visual language.
 * Keep product-wide UI decisions here so new screens do not invent a new style.
 */
object YBColors {
    // Brand
    val Green = Color(0xFF18C986)
    val Deep = Color(0xFF06221B)
    val Deep2 = Color(0xFF0B342B)
    val Mint = Color(0xFFC9F8E1)
    val Gold = Color(0xFFFFC857)

    // Light theme
    val LightBg = Color(0xFFF2F6F4)
    val LightSurface = Color.White
    val LightText = Color(0xFF06221B)
    val LightMuted = Color(0xFF70847B)
    val LightSoftGreen = Color(0xFFE4F7ED)

    // Dark theme
    val DarkBg = Color(0xFF07110E)
    val DarkSurface = Color(0xFF10221C)
    val DarkSurface2 = Color(0xFF153129)
    val DarkText = Color(0xFFF1F7F4)
    val DarkMuted = Color(0xFF9AB4A9)
    val DarkSoftGreen = Color(0xFF17372D)

    // Semantic feedback
    val Success = Color(0xFF25C77A)
    val Error = Color(0xFFE65353)
    val Warning = Color(0xFFFFB547)
    val Info = Color(0xFF5AA8FF)
}

object YBSpacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 20.dp
    val xxl = 24.dp
}

object YBRadius {
    val small = 10.dp
    val medium = 14.dp
    val card = 18.dp
    val large = 22.dp
    val hero = 26.dp
    val pill = 999.dp
}

object YBTypography {
    val hero = 28.sp
    val title = 21.sp
    val section = 16.sp
    val body = 13.sp
    val caption = 11.sp
    val label = 9.sp
}

val YBShapes = Shapes(
    extraSmall = RoundedCornerShape(YBRadius.small),
    small = RoundedCornerShape(YBRadius.medium),
    medium = RoundedCornerShape(YBRadius.card),
    large = RoundedCornerShape(YBRadius.large),
    extraLarge = RoundedCornerShape(YBRadius.hero)
)
