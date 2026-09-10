package tr.yurdunubil.app

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import compose.icons.tablericons.OutlineGroup
import compose.icons.tablericons.outline.Map
import compose.icons.tablericons.outline.Swords
import compose.icons.tablericons.outline.Target

/** Product-wide icon language for Yurdunu Bil. */
object YBIcons {
    val Back: ImageVector get() = Icons.Default.ArrowBack
    val Target: ImageVector get() = OutlineGroup.Target
    val Map: ImageVector get() = OutlineGroup.Map
    val Swords: ImageVector get() = OutlineGroup.Swords
    val Trophy: ImageVector get() = Icons.Default.EmojiEvents
    val Star: ImageVector get() = Icons.Default.Star
    val Flame: ImageVector get() = Icons.Default.Whatshot
    val Bolt: ImageVector get() = Icons.Default.Bolt
    val Library: ImageVector get() = Icons.Default.Book
    val Tip: ImageVector get() = Icons.Default.Lightbulb
    val Settings: ImageVector get() = Icons.Default.Settings
}

@Composable
fun YBGameIcon(id: String, tint: Color, size: androidx.compose.ui.unit.Dp = 24.dp) {
    val icon = when {
        id.contains("quick", true) -> YBIcons.Target
        id.contains("map", true) -> YBIcons.Map
        id.contains("duel", true) || id.contains("arena", true) || id.contains("speed", true) || id.contains("hard", true) -> YBIcons.Swords
        id.contains("chain", true) -> YBIcons.Target
        id.contains("master", true) -> YBIcons.Trophy
        id.contains("region", true) -> YBIcons.Map
        id.contains("mine", true) -> YBIcons.Target
        id.contains("agriculture", true) -> YBIcons.Map
        id.contains("climate", true) -> YBIcons.Target
        id.contains("water", true) -> YBIcons.Map
        id.contains("population", true) -> YBIcons.Target
        else -> YBIcons.Library
    }
    androidx.compose.material3.Icon(icon, contentDescription = null, tint = tint, modifier = androidx.compose.ui.Modifier.size(size))
}
