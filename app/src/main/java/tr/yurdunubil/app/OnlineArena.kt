package tr.yurdunubil.app

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

@Serializable
data class ArenaQueueRow(
    val user_id: String,
    val mode: String,
    val rating: Int = 1000,
    val queued_at: String? = null,
    val expires_at: String? = null
)

@Serializable
data class ArenaMatchRow(
    val id: String,
    val mode: String,
    val status: String,
    val host_id: String,
    val guest_id: String? = null,
    val room_code: String? = null,
    val current_round: Int = 0,
    val total_rounds: Int = 10
)

object OnlineArenaRepository {
    private val client get() = SupabaseClientProvider.client

    fun signedIn(): Boolean = client.auth.currentSessionOrNull() != null

    suspend fun queue(mode: String): ArenaQueueRow = client.postgrest
        .rpc("enqueue_arena", buildJsonObject {
            put("p_mode", JsonPrimitive(mode))
        }).decodeSingle()

    suspend fun tryMatch(mode: String): ArenaMatchRow = client.postgrest
        .rpc("try_match_arena", buildJsonObject {
            put("p_mode", JsonPrimitive(mode))
        }).decodeSingle()

    suspend fun leaveQueue() {
        client.postgrest.rpc("leave_arena_queue")
    }

    suspend fun markReady(matchId: String) {
        client.postgrest.rpc("mark_arena_ready", buildJsonObject {
            put("p_match_id", JsonPrimitive(matchId))
        })
    }
}

@Composable
fun OnlineArenaScreen(
    darkMode: Boolean,
    mode: SharedGameMode = SharedGameModes.duel,
    onBack: () -> Unit,
    onMatched: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val text = if (darkMode) Color(0xFFF1F7F4) else Color(0xFF06221B)
    val muted = if (darkMode) Color(0xFF9AB4A9) else Color(0xFF70847B)
    val surface = if (darkMode) Color(0xFF10221C) else Color.White
    val green = Color(0xFF18C986)
    val gold = Color(0xFFFFC857)
    var searching by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var matched by remember { mutableStateOf<ArenaMatchRow?>(null) }

    LaunchedEffect(searching) {
        if (!searching) return@LaunchedEffect
        while (searching && matched == null) {
            try {
                val result = OnlineArenaRepository.tryMatch(mode.id)
                matched = result
                searching = false
                onMatched(result.id)
                break
            } catch (_: Exception) {
                delay(1800L)
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            if (searching) scope.launch { runCatching { OnlineArenaRepository.leaveQueue() } }
        }
    }

    if (!OnlineArenaRepository.signedIn()) {
        Column(
            Modifier.fillMaxSize().padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🔐", fontSize = 42.sp)
            Spacer(Modifier.height(12.dp))
            Text("Online Arena için hesap gerekli", color = text, fontSize = 21.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(7.dp))
            Text("İki farklı cihazın aynı Arena maçında buluşabilmesi için güvenli bir Supabase hesabıyla giriş yapmalısın.", color = muted, fontSize = 12.sp, lineHeight = 18.sp)
            Spacer(Modifier.height(18.dp))
            Button(onClick = { context.startActivity(Intent(context, LaunchActivity::class.java)) }, colors = ButtonDefaults.buttonColors(containerColor = green), shape = RoundedCornerShape(16.dp)) {
                Text("Hesapla devam et", color = Color(0xFF06221B), fontWeight = FontWeight.Black)
            }
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = onBack) { Text("Geri", color = muted) }
        }
        return
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        TextButton(onClick = onBack) { Text("‹ Arena", color = green, fontWeight = FontWeight.Bold) }
        Box(
            Modifier.fillMaxWidth().background(
                Brush.linearGradient(listOf(Color(0xFF06221B), Color(0xFF0B4A38)))
            ).padding(18.dp)
        ) {
            Column {
                Text("🌐 ONLINE ARENA", color = Color(0xFFC9F8E1), fontSize = 10.sp, fontWeight = FontWeight.Black)
                Spacer(Modifier.height(5.dp))
                Text(mode.title, color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.Black)
                Text("İki cihaz • gerçek zamanlı eşleşme", color = Color.White.copy(alpha = .72f), fontSize = 11.sp)
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OnlinePill("${mode.questions}", "SORU")
                    OnlinePill("${mode.seconds}s", "SÜRE")
                    OnlinePill("+${mode.rewardXp}", "XP")
                }
            }
        }
        Spacer(Modifier.height(18.dp))
        if (error != null) {
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF3A211F))) {
                Text(error!!, color = Color(0xFFFFB5AD), modifier = Modifier.padding(13.dp), fontSize = 11.sp)
            }
            Spacer(Modifier.height(10.dp))
        }
        Card(Modifier.fillMaxWidth(), RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = surface)) {
            Column(Modifier.padding(16.dp)) {
                Text(if (searching) "Rakip aranıyor…" else "Hazır mısın?", color = text, fontSize = 18.sp, fontWeight = FontWeight.Black)
                Spacer(Modifier.height(5.dp))
                Text(if (searching) "Aynı moddaki başka bir oyuncu bulunduğunda maç otomatik başlayacak." else "İki farklı telefondaki oyuncular aynı eşleştirme havuzuna alınır.", color = muted, fontSize = 11.sp, lineHeight = 17.sp)
                Spacer(Modifier.height(14.dp))
                Button(
                    onClick = {
                        error = null
                        searching = true
                        scope.launch {
                            try {
                                OnlineArenaRepository.queue(mode.id)
                            } catch (e: Exception) {
                                searching = false
                                error = e.message ?: "Arena kuyruğuna bağlanılamadı."
                            }
                        }
                    },
                    enabled = !searching,
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = green),
                    shape = RoundedCornerShape(16.dp)
                ) { Text(if (searching) "Rakip Bekleniyor…" else "Rakip Bul", color = Color(0xFF06221B), fontWeight = FontWeight.Black) }
                if (searching) {
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(onClick = { searching = false; scope.launch { runCatching { OnlineArenaRepository.leaveQueue() } } }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                        Text("Aramayı İptal Et", color = gold, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Text("🔒 Güvenli eşleşme", color = green, fontSize = 10.sp, fontWeight = FontWeight.Black)
        Text("Skor ve maç durumu cihazlar arasında Supabase üzerinden senkronize edilecek.", color = muted, fontSize = 10.sp)
    }
}

@Composable
private fun OnlinePill(value: String, label: String) {
    Column(Modifier.background(Color.White.copy(alpha = .09f), RoundedCornerShape(11.dp)).padding(horizontal = 10.dp, vertical = 6.dp)) {
        Text(value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Black)
        Text(label, color = Color.White.copy(alpha = .55f), fontSize = 7.sp, fontWeight = FontWeight.Bold)
    }
}
