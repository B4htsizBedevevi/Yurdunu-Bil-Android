package tr.yurdunubil.app

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import compose.icons.tablericons.outline.Anchor
import compose.icons.tablericons.outline.Bird
import compose.icons.tablericons.outline.Cat
import compose.icons.tablericons.outline.CloudSun
import compose.icons.tablericons.outline.Compass
import compose.icons.tablericons.outline.Crown
import compose.icons.tablericons.outline.Diamond
import compose.icons.tablericons.outline.Dog
import compose.icons.tablericons.outline.Fish
import compose.icons.tablericons.outline.Flag
import compose.icons.tablericons.outline.Heart
import compose.icons.tablericons.outline.Map
import compose.icons.tablericons.outline.Mountain
import compose.icons.tablericons.outline.Plant2
import compose.icons.tablericons.outline.Rocket
import compose.icons.tablericons.outline.Shield
import compose.icons.tablericons.outline.Sun
import compose.icons.tablericons.outline.Swords
import compose.icons.tablericons.outline.Target
import compose.icons.tablericons.outline.Tent
import compose.icons.tablericons.outline.Trees
import compose.icons.tablericons.outline.User
import compose.icons.tablericons.outline.Waves

/** Product-wide icon language for Yurdunu Bil. All user-facing symbols are vector icons, not emoji. */
object YBIcons {
    val Back: ImageVector get() = Icons.Default.ArrowBack
    val Home: ImageVector get() = Icons.Default.Home
    val Target: ImageVector get() = Target
    val Map: ImageVector get() = Map
    val Swords: ImageVector get() = Swords
    val Trophy: ImageVector get() = Icons.Default.EmojiEvents
    val Star: ImageVector get() = Icons.Default.Star
    val Flame: ImageVector get() = Icons.Default.Whatshot
    val Bolt: ImageVector get() = Icons.Default.Bolt
    val Library: ImageVector get() = Icons.Default.Book
    val Tip: ImageVector get() = Icons.Default.Lightbulb
    val Settings: ImageVector get() = Icons.Default.Settings

    // Stable avatar set. IDs are persisted in profiles.avatar_id so the visual never changes between builds.
    val AvatarUser: ImageVector get() = User
    val AvatarCompass: ImageVector get() = Compass
    val AvatarMountain: ImageVector get() = Mountain
    val AvatarTrees: ImageVector get() = Trees
    val AvatarCloudSun: ImageVector get() = CloudSun
    val AvatarFlag: ImageVector get() = Flag
    val AvatarCrown: ImageVector get() = Crown
    val AvatarShield: ImageVector get() = Shield
    val AvatarRocket: ImageVector get() = Rocket
    val AvatarCat: ImageVector get() = Cat
    val AvatarDog: ImageVector get() = Dog
    val AvatarBird: ImageVector get() = Bird
    val AvatarFish: ImageVector get() = Fish
    val AvatarPlant: ImageVector get() = Plant2
    val AvatarSun: ImageVector get() = Sun
    val AvatarTent: ImageVector get() = Tent
    val AvatarAnchor: ImageVector get() = Anchor
    val AvatarHeart: ImageVector get() = Heart
    val AvatarDiamond: ImageVector get() = Diamond
    val AvatarWaves: ImageVector get() = Waves
}

data class YBAvatar(val id: String, val name: String, val icon: ImageVector)

val YBAvatars = listOf(
    YBAvatar("atlas-user", "Kaşif", YBIcons.AvatarUser),
    YBAvatar("atlas-compass", "Pusula", YBIcons.AvatarCompass),
    YBAvatar("atlas-mountain", "Dağcı", YBIcons.AvatarMountain),
    YBAvatar("atlas-trees", "Orman", YBIcons.AvatarTrees),
    YBAvatar("atlas-cloud", "Gökyüzü", YBIcons.AvatarCloudSun),
    YBAvatar("atlas-flag", "Bayrak", YBIcons.AvatarFlag),
    YBAvatar("atlas-crown", "Lider", YBIcons.AvatarCrown),
    YBAvatar("atlas-shield", "Kalkan", YBIcons.AvatarShield),
    YBAvatar("atlas-rocket", "Roket", YBIcons.AvatarRocket),
    YBAvatar("atlas-cat", "Kedi", YBIcons.AvatarCat),
    YBAvatar("atlas-dog", "Köpek", YBIcons.AvatarDog),
    YBAvatar("atlas-bird", "Kuş", YBIcons.AvatarBird),
    YBAvatar("atlas-fish", "Balık", YBIcons.AvatarFish),
    YBAvatar("atlas-plant", "Bitki", YBIcons.AvatarPlant),
    YBAvatar("atlas-sun", "Güneş", YBIcons.AvatarSun),
    YBAvatar("atlas-tent", "Kampçı", YBIcons.AvatarTent),
    YBAvatar("atlas-anchor", "Denizci", YBIcons.AvatarAnchor),
    YBAvatar("atlas-heart", "Kalp", YBIcons.AvatarHeart),
    YBAvatar("atlas-diamond", "Elmas", YBIcons.AvatarDiamond),
    YBAvatar("atlas-waves", "Dalgalar", YBIcons.AvatarWaves)
)

fun ybAvatar(id: String?): YBAvatar = YBAvatars.firstOrNull { it.id == id } ?: YBAvatars.first()

@Composable
fun YBGameIcon(id: String, tint: Color, size: androidx.compose.ui.unit.Dp = 24.dp) {
    val icon = when {
        id.contains("speed", true) -> YBIcons.Bolt
        id.contains("hard", true) -> YBIcons.Trophy
        id.contains("region", true) -> YBIcons.Map
        id.contains("duel", true) -> YBIcons.Swords
        id.contains("arena", true) -> YBIcons.Swords
        id.contains("quick", true) -> YBIcons.Target
        id.contains("map", true) -> YBIcons.Map
        id.contains("chain", true) -> YBIcons.Target
        id.contains("master", true) -> YBIcons.Trophy
        id.contains("mine", true) -> YBIcons.Target
        id.contains("agriculture", true) -> YBIcons.Map
        id.contains("climate", true) -> YBIcons.Target
        id.contains("water", true) -> YBIcons.AvatarWaves
        id.contains("population", true) -> YBIcons.AvatarUser
        else -> YBIcons.Library
    }
    androidx.compose.material3.Icon(icon, contentDescription = null, tint = tint, modifier = androidx.compose.ui.Modifier.size(size))
}
