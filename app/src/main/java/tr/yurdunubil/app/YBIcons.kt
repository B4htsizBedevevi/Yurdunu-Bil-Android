package tr.yurdunubil.app

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.DirectionsBoat
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/** One product-wide vector language. */
object YBIcons {
    val Back: ImageVector get() = Icons.Default.ArrowBack
    val Home: ImageVector get() = Icons.Default.Home
    val Target: ImageVector get() = Icons.Default.TrackChanges
    val Map: ImageVector get() = Icons.Default.Map
    val Swords: ImageVector get() = Icons.Default.SportsEsports
    val Trophy: ImageVector get() = Icons.Default.EmojiEvents
    val Star: ImageVector get() = Icons.Default.Star
    val Flame: ImageVector get() = Icons.Default.Whatshot
    val Bolt: ImageVector get() = Icons.Default.Bolt
    val Library: ImageVector get() = Icons.Default.AutoStories
    val Tip: ImageVector get() = Icons.Default.Lightbulb
    val Factory: ImageVector get() = Icons.Default.Business
    val Volcano: ImageVector get() = Icons.Default.Terrain
    val Population: ImageVector get() = Icons.Default.People
    val Settings: ImageVector get() = Icons.Default.Settings
    val AvatarUser: ImageVector get() = Icons.Default.Person
    val Shield: ImageVector get() = Icons.Default.Shield
    val Explore: ImageVector get() = Icons.Default.Explore
    val AvatarExplorer: ImageVector get() = Icons.Default.Explore
    val AvatarCompass: ImageVector get() = Icons.Default.TravelExplore
    val AvatarMountain: ImageVector get() = Icons.Default.Terrain
    val AvatarForest: ImageVector get() = Icons.Default.Forest
    val AvatarCloud: ImageVector get() = Icons.Default.Cloud
    val AvatarSun: ImageVector get() = Icons.Default.WbSunny
    val AvatarFlag: ImageVector get() = Icons.Default.Flag
    val AvatarTurkey: ImageVector get() = Icons.Default.Public
    val AvatarCrown: ImageVector get() = Icons.Default.MilitaryTech
    val AvatarShield: ImageVector get() = Icons.Default.Shield
    val AvatarRocket: ImageVector get() = Icons.Default.RocketLaunch
    val AvatarScholar: ImageVector get() = Icons.Default.School
    val AvatarBook: ImageVector get() = Icons.Default.Book
    val AvatarBrain: ImageVector get() = Icons.Default.Psychology
    val AvatarScience: ImageVector get() = Icons.Default.Science
    val AvatarFace: ImageVector get() = Icons.Default.Face
    val AvatarCat: ImageVector get() = Icons.Default.Pets
    val AvatarHeart: ImageVector get() = Icons.Default.Favorite
    val AvatarDiamond: ImageVector get() = Icons.Default.Diamond
    val AvatarSports: ImageVector get() = Icons.Default.SportsEsports
    val AvatarAnchor: ImageVector get() = Icons.Default.DirectionsBoat
    val AvatarWaves: ImageVector get() = Icons.Default.Waves
    val AvatarWater: ImageVector get() = Icons.Default.WaterDrop
    val AvatarPark: ImageVector get() = Icons.Default.Park
    val AvatarHome: ImageVector get() = Icons.Default.Home
    val AvatarLight: ImageVector get() = Icons.Default.Lightbulb
    val AvatarFire: ImageVector get() = Icons.Default.Whatshot
    val AvatarBolt: ImageVector get() = Icons.Default.Bolt
    val AvatarBookmark: ImageVector get() = Icons.Default.AutoStories
}

data class YBAvatar(val id: String, val name: String, val icon: ImageVector, val category: AvatarCategory)
enum class AvatarCategory(val label: String) { GEOGRAPHY("Kaşif"), STUDY("Ders"), ARENA("Arena"), NATURE("Doğa"), CHARACTER("Karakter") }

val YBAvatars = listOf(
    YBAvatar("explorer", "Kaşif", YBIcons.AvatarExplorer, AvatarCategory.GEOGRAPHY), YBAvatar("compass", "Pusula", YBIcons.AvatarCompass, AvatarCategory.GEOGRAPHY), YBAvatar("mountain", "Dağcı", YBIcons.AvatarMountain, AvatarCategory.GEOGRAPHY), YBAvatar("flag", "Bayrak", YBIcons.AvatarFlag, AvatarCategory.GEOGRAPHY), YBAvatar("turkey", "Yurtsever", YBIcons.AvatarTurkey, AvatarCategory.GEOGRAPHY), YBAvatar("anchor", "Denizci", YBIcons.AvatarAnchor, AvatarCategory.GEOGRAPHY),
    YBAvatar("waves", "Dalgacı", YBIcons.AvatarWaves, AvatarCategory.NATURE), YBAvatar("water", "Su Ustası", YBIcons.AvatarWater, AvatarCategory.NATURE), YBAvatar("forest", "Orman", YBIcons.AvatarForest, AvatarCategory.NATURE), YBAvatar("park", "Doğa", YBIcons.AvatarPark, AvatarCategory.NATURE), YBAvatar("cloud", "Gökyüzü", YBIcons.AvatarCloud, AvatarCategory.NATURE), YBAvatar("sun", "Güneş", YBIcons.AvatarSun, AvatarCategory.NATURE),
    YBAvatar("scholar", "Bilgin", YBIcons.AvatarScholar, AvatarCategory.STUDY), YBAvatar("book", "Okur", YBIcons.AvatarBook, AvatarCategory.STUDY), YBAvatar("brain", "Stratejist", YBIcons.AvatarBrain, AvatarCategory.STUDY), YBAvatar("science", "Analist", YBIcons.AvatarScience, AvatarCategory.STUDY), YBAvatar("light", "Fikir", YBIcons.AvatarLight, AvatarCategory.STUDY), YBAvatar("bookmark", "Notçu", YBIcons.AvatarBookmark, AvatarCategory.STUDY),
    YBAvatar("crown", "Lider", YBIcons.AvatarCrown, AvatarCategory.ARENA), YBAvatar("shield", "Savunmacı", YBIcons.AvatarShield, AvatarCategory.ARENA), YBAvatar("rocket", "Roket", YBIcons.AvatarRocket, AvatarCategory.ARENA), YBAvatar("sports", "Mücadeleci", YBIcons.AvatarSports, AvatarCategory.ARENA), YBAvatar("fire", "Seri Avcısı", YBIcons.AvatarFire, AvatarCategory.ARENA), YBAvatar("bolt", "Hızlı", YBIcons.AvatarBolt, AvatarCategory.ARENA),
    YBAvatar("face", "Renkli", YBIcons.AvatarFace, AvatarCategory.CHARACTER), YBAvatar("cat", "Kedi", YBIcons.AvatarCat, AvatarCategory.CHARACTER), YBAvatar("heart", "Kalp", YBIcons.AvatarHeart, AvatarCategory.CHARACTER), YBAvatar("diamond", "Elmas", YBIcons.AvatarDiamond, AvatarCategory.CHARACTER), YBAvatar("home", "Yuvacı", YBIcons.AvatarHome, AvatarCategory.CHARACTER),
    YBAvatar("map_master", "Haritacı", YBIcons.Map, AvatarCategory.GEOGRAPHY), YBAvatar("world", "Dünya Gezgini", YBIcons.AvatarTurkey, AvatarCategory.GEOGRAPHY), YBAvatar("compass_pro", "Kaptan", YBIcons.AvatarCompass, AvatarCategory.GEOGRAPHY), YBAvatar("terrain", "Arazi Ustası", YBIcons.AvatarMountain, AvatarCategory.GEOGRAPHY),
    YBAvatar("teacher", "Öğretmen", YBIcons.AvatarScholar, AvatarCategory.STUDY), YBAvatar("reader", "Kâşif Okur", YBIcons.AvatarBook, AvatarCategory.STUDY), YBAvatar("thinker", "Düşünür", YBIcons.AvatarBrain, AvatarCategory.STUDY), YBAvatar("idea", "Üretken", YBIcons.AvatarLight, AvatarCategory.STUDY),
    YBAvatar("champion", "Şampiyon", YBIcons.Trophy, AvatarCategory.ARENA), YBAvatar("diamond_rank", "Elmas Lig", YBIcons.AvatarDiamond, AvatarCategory.ARENA), YBAvatar("power", "Güçlü", YBIcons.AvatarBolt, AvatarCategory.ARENA), YBAvatar("runner", "Sprinter", YBIcons.AvatarRocket, AvatarCategory.ARENA),
    YBAvatar("dog", "Dost", YBIcons.AvatarFace, AvatarCategory.CHARACTER), YBAvatar("owl", "Baykuş", YBIcons.AvatarScholar, AvatarCategory.CHARACTER), YBAvatar("smile", "Neşeli", YBIcons.AvatarFace, AvatarCategory.CHARACTER), YBAvatar("star", "Yıldız", YBIcons.AvatarDiamond, AvatarCategory.CHARACTER),
    YBAvatar("leaf", "Yaprak", YBIcons.AvatarForest, AvatarCategory.NATURE), YBAvatar("rain", "Yağmur", YBIcons.AvatarCloud, AvatarCategory.NATURE), YBAvatar("river", "Nehir", YBIcons.AvatarWaves, AvatarCategory.NATURE), YBAvatar("sunrise", "Gün Doğumu", YBIcons.AvatarSun, AvatarCategory.NATURE)
)

private val legacyAvatarAliases = mapOf("atlas-user" to "face", "atlas-compass" to "compass", "atlas-mountain" to "mountain", "atlas-trees" to "forest", "atlas-cloud" to "cloud", "atlas-flag" to "flag", "atlas-crown" to "crown", "atlas-shield" to "shield", "atlas-rocket" to "rocket", "atlas-cat" to "cat", "atlas-dog" to "face", "atlas-bird" to "face", "atlas-fish" to "water", "atlas-plant" to "park", "atlas-sun" to "sun", "atlas-tent" to "explorer", "atlas-anchor" to "anchor", "atlas-heart" to "heart", "atlas-diamond" to "diamond", "atlas-waves" to "waves")
fun ybAvatar(id: String?): YBAvatar = YBAvatars.firstOrNull { it.id == id } ?: YBAvatars.firstOrNull { it.id == legacyAvatarAliases[id] } ?: YBAvatars.first()

@Composable
fun YBGameIcon(id: String, tint: Color, size: androidx.compose.ui.unit.Dp = 24.dp) {
    val icon = when {
        id.contains("speed", true) -> YBIcons.Bolt
        id.contains("mountain", true) -> YBIcons.AvatarMountain
        id.contains("agriculture", true) -> YBIcons.AvatarPark
        id.contains("factory", true) -> YBIcons.Factory
        id.contains("mine", true) -> YBIcons.AvatarDiamond
        id.contains("tourism", true) -> YBIcons.AvatarExplorer
        id.contains("danger", true) -> YBIcons.AvatarShield
        id.contains("match", true) -> YBIcons.Target
        id.contains("hard", true) || id.contains("master", true) -> YBIcons.Trophy
        id.contains("region", true) || id.contains("agriculture", true) -> YBIcons.Map
        id.contains("duel", true) || id.contains("arena", true) -> YBIcons.Swords
        id.contains("quick", true) || id.contains("chain", true) -> YBIcons.Target
        id.contains("map", true) -> YBIcons.Map
        id.contains("climate", true) -> YBIcons.AvatarCloud
        id.contains("water", true) -> YBIcons.AvatarWater
        id.contains("population", true) -> YBIcons.Population
        id.contains("library", true) || id.contains("study", true) -> YBIcons.Library
        else -> YBIcons.Library
    }
    androidx.compose.material3.Icon(icon, contentDescription = null, tint = tint, modifier = androidx.compose.ui.Modifier.size(size))
}

fun ybGameAccent(id: String, arena: Boolean = false): Color = when {
    arena || id.contains("duel", true) || id.contains("arena", true) || id.contains("master", true) -> Color(0xFFFFC857)
    id.contains("water", true) -> Color(0xFF3B9EFF)
    id.contains("climate", true) -> Color(0xFF6B9AF7)
    id.contains("agriculture", true) -> Color(0xFF35B86B)
    id.contains("mine", true) -> Color(0xFFFFA63D)
    id.contains("factory", true) -> Color(0xFF8B6FFF)
    id.contains("tourism", true) -> Color(0xFFE45D9A)
    id.contains("population", true) -> Color(0xFF5B7CFA)
    id.contains("region", true) || id.contains("map", true) -> Color(0xFF18C98A)
    id.contains("danger", true) -> Color(0xFFE65353)
    id.contains("quick", true) || id.contains("speed", true) -> Color(0xFFFFB83D)
    id.contains("chain", true) -> Color(0xFF14C8B1)
    else -> Color(0xFF18C98A)
}
