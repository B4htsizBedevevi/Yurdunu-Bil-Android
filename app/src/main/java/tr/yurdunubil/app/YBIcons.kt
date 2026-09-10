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
import compose.icons.tablericons.outline.Anchor as TablerAnchor
import compose.icons.tablericons.outline.Bird as TablerBird
import compose.icons.tablericons.outline.Cat as TablerCat
import compose.icons.tablericons.outline.CloudSun as TablerCloudSun
import compose.icons.tablericons.outline.Compass as TablerCompass
import compose.icons.tablericons.outline.Crown as TablerCrown
import compose.icons.tablericons.outline.Diamond as TablerDiamond
import compose.icons.tablericons.outline.Dog as TablerDog
import compose.icons.tablericons.outline.Fish as TablerFish
import compose.icons.tablericons.outline.Flag as TablerFlag
import compose.icons.tablericons.outline.Heart as TablerHeart
import compose.icons.tablericons.outline.Map as TablerMap
import compose.icons.tablericons.outline.Mountain as TablerMountain
import compose.icons.tablericons.outline.Plant2 as TablerPlant2
import compose.icons.tablericons.outline.Rocket as TablerRocket
import compose.icons.tablericons.outline.Shield as TablerShield
import compose.icons.tablericons.outline.Sun as TablerSun
import compose.icons.tablericons.outline.Swords as TablerSwords
import compose.icons.tablericons.outline.Target as TablerTarget
import compose.icons.tablericons.outline.Tent as TablerTent
import compose.icons.tablericons.outline.Trees as TablerTrees
import compose.icons.tablericons.outline.User as TablerUser
import compose.icons.tablericons.outline.Waves as TablerWaves

/** Product-wide icon language for Yurdunu Bil. All user-facing symbols are vector icons, not emoji. */
object YBIcons {
    val Back: ImageVector get() = Icons.Default.ArrowBack
    val Home: ImageVector get() = Icons.Default.Home
    val Target: ImageVector get() = TablerTarget
    val Map: ImageVector get() = TablerMap
    val Swords: ImageVector get() = TablerSwords
    val Trophy: ImageVector get() = Icons.Default.EmojiEvents
    val Star: ImageVector get() = Icons.Default.Star
    val Flame: ImageVector get() = Icons.Default.Whatshot
    val Bolt: ImageVector get() = Icons.Default.Bolt
    val Library: ImageVector get() = Icons.Default.Book
    val Tip: ImageVector get() = Icons.Default.Lightbulb
    val Settings: ImageVector get() = Icons.Default.Settings

    // Stable avatar set. IDs are persisted in profiles.avatar_id so the visual never changes between builds.
    val AvatarUser: ImageVector get() = TablerUser
    val AvatarCompass: ImageVector get() = TablerCompass
    val AvatarMountain: ImageVector get() = TablerMountain
    val AvatarTrees: ImageVector get() = TablerTrees
    val AvatarCloudSun: ImageVector get() = TablerCloudSun
    val AvatarFlag: ImageVector get() = TablerFlag
    val AvatarCrown: ImageVector get() = TablerCrown
    val AvatarShield: ImageVector get() = TablerShield
    val AvatarRocket: ImageVector get() = TablerRocket
    val AvatarCat: ImageVector get() = TablerCat
    val AvatarDog: ImageVector get() = TablerDog
    val AvatarBird: ImageVector get() = TablerBird
    val AvatarFish: ImageVector get() = TablerFish
    val AvatarPlant: ImageVector get() = TablerPlant2
    val AvatarSun: ImageVector get() = TablerSun
    val AvatarTent: ImageVector get() = TablerTent
    val AvatarAnchor: ImageVector get() = TablerAnchor
    val AvatarHeart: ImageVector get() = TablerHeart
    val AvatarDiamond: ImageVector get() = TablerDiamond
    val AvatarWaves: ImageVector get() = TablerWaves
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
