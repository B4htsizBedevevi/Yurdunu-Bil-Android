package tr.yurdunubil.app

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloat
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/** Reusable motion/effect layer for Yurdunu Bil cards. */
@Composable
fun PremiumCard(
    modifier: Modifier = Modifier,
    accent: Color = Color(0xFF18B77A),
    dark: Boolean = false,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.975f else 1f,
        animationSpec = tween(140, easing = FastOutSlowInEasing),
        label = "card_press"
    )
    val transition = rememberInfiniteTransition(label = "card_glow")
    val glow by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(tween(1700), RepeatMode.Reverse),
        label = "card_glow_alpha"
    )
    val bg = if (dark) Color(0xFF061A16) else Color.White
    val click = if (onClick != null) {
        Modifier.clickable {
            pressed = true
            onClick()
            pressed = false
        }
    } else Modifier

    Card(
        modifier = modifier.then(click).scale(scale).fillMaxWidth(),
        shape = RoundedCornerShape(21.dp),
        colors = CardDefaults.cardColors(bg),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(Modifier.fillMaxWidth()) {
            Box(
                Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(accent)
            )
            Box(
                Modifier
                    .matchParentSize()
                    .alpha(glow * 0.08f)
                    .background(Brush.horizontalGradient(listOf(accent, Color.Transparent)))
            )
            Column(
                Modifier.padding(17.dp).padding(start = 4.dp),
                content = content
            )
        }
    }
}
