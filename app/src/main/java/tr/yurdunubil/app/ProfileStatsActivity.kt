@file:OptIn(ExperimentalMaterial3Api::class)

package tr.yurdunubil.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

class ProfileStatsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ProfileStatsScreen(onBack = { finish() }) }
    }
}

@Serializable
data class ProfileStatsProgress(
    val xp: Int = 0,
    val level: Int = 1,
    val streak_days: Int = 0,
    val best_streak: Int = 0,
    val total_questions: Int = 0,
    val correct_answers: Int = 0
)

@Serializable
data class ProfileStatsProfile(
    val username: String? = null,
    val display_name: String? = null,
    val avatar_id: String? = null
)

@Serializable
data class ProfileStatsArena(
    val rating: Int = 1000,
    val wins: Int = 0,
    val losses: Int = 0,
    val draws: Int = 0,
    val games_played: Int = 0
)

@Serializable
data class ArenaLeaderboardRow(
    val user_id: String,
    val username: String? = null,
    val display_name: String? = null,
    val avatar_id: String? = null,
    val rating: Int = 1000,
    val wins: Int = 0,
    val losses: Int = 0,
    val draws: Int = 0,
    val games_played: Int = 0
)

private object ProfileStatsRepository {
    private val client get() = SupabaseClientProvider.client

    suspend fun profile(id: String): ProfileStatsProfile = client.postgrest.from("profiles").select { filter { eq("id", id) } }.decodeSingle()
    suspend fun progress(id: String): ProfileStatsProgress = client.postgrest.from("user_progress").select { filter { eq("user_id", id) } }.decodeSingle()
    suspend fun arena(id: String): ProfileStatsArena = client.postgrest.from("arena_profiles").select { filter { eq("user_id", id) } }.decodeSingle()
    suspend fun leaderboard(): List<ArenaLeaderboardRow> = client.postgrest.rpc("get_arena_leaderboard", buildJsonObject { put("p_limit", JsonPrimitive(30)) }).decodeList()
}

@Composable
private fun ProfileStatsScreen(onBack: () -> Unit) {
    BackHandler { onBack() }
    val scope = rememberCoroutineScope()
    val client = remember { SupabaseClientProvider.client }
    val myId = client.auth.currentUserOrNull()?.id
    val bg = Color(0xFF06140F)
    val card = Color(0xFF10251E)
    val soft = Color(0xFF12352A)
    val text = Color(0xFFF3FBF7)
    val muted = Color(0xFF91AAA1)
    val green = Color(0xFF28DE98)
    val gold = Color(0xFFFFC857)
    var profile by remember { mutableStateOf<ProfileStatsProfile?>(null) }
    var progress by remember { mutableStateOf<ProfileStatsProgress?>(null) }
    var arena by remember { mutableStateOf<ProfileStatsArena?>(null) }
    var leaderboard by remember { mutableStateOf(emptyList<ArenaLeaderboardRow>()) }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(myId) {
        if (myId == null) { error = "Oturum bulunamadı."; loading = false; return@LaunchedEffect }
        scope.launch {
            runCatching {
                profile = ProfileStatsRepository.profile(myId)
                progress = ProfileStatsRepository.progress(myId)
                arena = ProfileStatsRepository.arena(myId)
                leaderboard = ProfileStatsRepository.leaderboard()
            }.onFailure { error = it.message ?: "Profil verileri alınamadı." }
            loading = false
        }
    }

    Scaffold(
        containerColor = bg,
        topBar = {
            TopAppBar(
                title = { Text("Profil & Liderlik", color = text, fontWeight = FontWeight.Black) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Geri", tint = text) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = bg)
            )
        }
    ) { pad ->
        if (loading) Box(Modifier.fillMaxSize().padding(pad), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = green) }
        else LazyColumn(Modifier.fillMaxSize().padding(pad), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (error != null) item { Text(error!!, color = Color(0xFFFF8E83), fontSize = 11.sp) }
            item {
                Card(Modifier.fillMaxWidth(), RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = soft)) {
                    Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, null, tint = green, modifier = Modifier.size(42.dp))
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text(profile?.display_name?.takeIf { it.isNotBlank() } ?: profile?.username ?: "Yurdunu Bil Kullanıcısı", color = text, fontSize = 21.sp, fontWeight = FontWeight.Black)
                            Text("@${profile?.username ?: "kullanici"}", color = muted, fontSize = 11.sp)
                            Text("Seviye ${progress?.level ?: 1} • ${progress?.xp ?: 0} XP", color = green, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatBox("${progress?.total_questions ?: 0}", "SORU", green, card, text, Modifier.weight(1f))
                    StatBox("%${if ((progress?.total_questions ?: 0) == 0) 0 else ((progress?.correct_answers ?: 0) * 100 / (progress?.total_questions ?: 1))}", "DOĞRULUK", green, card, text, Modifier.weight(1f))
                    StatBox("${progress?.streak_days ?: 0}", "SERİ", gold, card, text, Modifier.weight(1f))
                }
            }
            item {
                Card(Modifier.fillMaxWidth(), RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = card)) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.SportsEsports, null, tint = gold); Spacer(Modifier.width(8.dp)); Text("Arena Karnesi", color = text, fontWeight = FontWeight.Black, fontSize = 16.sp) }
                        Spacer(Modifier.height(10.dp))
                        Text("${arena?.rating ?: 1000} Rating", color = gold, fontSize = 28.sp, fontWeight = FontWeight.Black)
                        Text("${arena?.wins ?: 0} galibiyet • ${arena?.losses ?: 0} mağlubiyet • ${arena?.draws ?: 0} beraberlik", color = muted, fontSize = 11.sp)
                        Text("${arena?.games_played ?: 0} maç oynandı", color = muted, fontSize = 11.sp)
                    }
                }
            }
            item {
                Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.EmojiEvents, null, tint = gold); Spacer(Modifier.width(8.dp)); Text("Arena Liderliği", color = text, fontSize = 19.sp, fontWeight = FontWeight.Black) }
            }
            itemsIndexed(leaderboard) { index, row ->
                Card(Modifier.fillMaxWidth(), RoundedCornerShape(17.dp), colors = CardDefaults.cardColors(containerColor = if (row.user_id == myId) soft else card)) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("${index + 1}", color = if (index < 3) gold else muted, fontWeight = FontWeight.Black, fontSize = 17.sp, modifier = Modifier.width(30.dp))
                        Icon(Icons.Default.Person, null, tint = green, modifier = Modifier.size(27.dp)); Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) { Text(row.display_name?.takeIf { it.isNotBlank() } ?: row.username ?: "Kullanıcı", color = text, fontWeight = FontWeight.Black, fontSize = 13.sp); Text("${row.wins} galibiyet • ${row.games_played} maç", color = muted, fontSize = 10.sp) }
                        Text(row.rating.toString(), color = gold, fontWeight = FontWeight.Black, fontSize = 17.sp)
                    }
                }
            }
            if (leaderboard.isEmpty()) item { Text("Henüz Arena sıralaması oluşmadı.", color = muted, fontSize = 12.sp) }
        }
    }
}

@Composable
private fun StatBox(value: String, label: String, accent: Color, card: Color, text: Color, modifier: Modifier) {
    Card(modifier, RoundedCornerShape(17.dp), colors = CardDefaults.cardColors(containerColor = card)) {
        Column(Modifier.fillMaxWidth().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text(value, color = accent, fontSize = 20.sp, fontWeight = FontWeight.Black); Text(label, color = text.copy(alpha = .58f), fontSize = 7.sp, fontWeight = FontWeight.Bold) }
    }
}
