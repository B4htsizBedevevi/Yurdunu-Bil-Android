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

    /** The RPC returns the composite row as a JSON object, not an array. */
    suspend fun queue(mode: String): ArenaQueueRow = client.postgrest
        .rpc("enqueue_arena", buildJsonObject {
            put("p_mode", JsonPrimitive(mode))
        }).decodeAs()

    /** Returns a match when found; null means the queue is still waiting. */
    suspend fun tryMatch(mode: String): ArenaMatchRow? = client.postgrest
        .rpc("try_match_arena", buildJsonObject {
            put("p_mode", JsonPrimitive(mode))
        }).decodeAs<ArenaMatchRow?>()

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
    val softSurface = if (darkMode) Color(0xFF152B23) else Color(0xFFF4F8F6)
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
                if (result != null) {
                    matched = result
                    searching = false
                    onMatched(result.id)
                    break
                }
                delay(1800L)
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
            Text("🌐", fontSize = 42.sp)
            Spacer(Modifier.height(12.dp))
            Text("Çevrimiçi Arena için giriş yap", color = text, fontSize = 21.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(7.dp))
            Text("Canlı eşleşmelere katılmak, rakip bulmak ve Arena ilerlemeni hesabında saklamak için giriş yapmalısın.", color = muted, fontSize = 12.sp, lineHeight = 18.sp)
            Spacer(Modifier.height(18.dp))
            Button(onClick = { context.startActivity(Intent(context, LaunchActivity::class.java)) }, colors = ButtonDefaults.buttonColors(containerColor = green), shape = RoundedCornerShape(16.dp)) {
                Text("Hesapla devam et", color = Color(0xFF06221B), fontWeight = FontWeight.Black)
            }
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = onBack) { Text("Geri", color = muted) }
        }
        return
    }

    Column(
        Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TextButton(onClick = onBack, contentPadding = PaddingValues(horizontal = 2.dp, vertical = 4.dp)) {
            Text("‹ Arena", color = green, fontWeight = FontWeight.Bold)
        }

        Box(
            Modifier.fillMaxWidth().background(
                Brush.linearGradient(listOf(Color(0xFF041E17), Color(0xFF087451), Color(0xFF0B4A38)))
            ).padding(20.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("●", color = green, fontSize = 12.sp, fontWeight = FontWeight.Black)
                    Spacer(Modifier.width(5.dp))
                    Text("ÇEVRİMİÇİ ARENA", color = Color(0xFFC9F8E1), fontSize = 10.sp, fontWeight = FontWeight.Black)
                }
                Spacer(Modifier.height(6.dp))
                Text("${mode.title}", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Black)
                Spacer(Modifier.height(3.dp))
                Text("Canlı 1v1 bilgi mücadelesine hazır ol.", color = Color.White.copy(alpha = .76f), fontSize = 12.sp)
                Spacer(Modifier.height(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OnlinePill("${mode.questions}", "SORU")
                    OnlinePill("${mode.seconds}s", "SÜRE")
                    OnlinePill("+${mode.rewardXp}", "XP")
                }
            }
        }

        if (error != null) {
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF3A211F))) {
                Column(Modifier.padding(13.dp)) {
                    Text("Bir sorun oluştu", color = Color(0xFFFFC1BA), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(3.dp))
                    Text(error!!, color = Color(0xFFFFB5AD), fontSize = 11.sp, lineHeight = 16.sp)
                }
            }
        }

        Card(
            Modifier.fillMaxWidth(),
            RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = surface)
        ) {
            Column(Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(if (searching) "Rakip aranıyor…" else "Maça hazır mısın?", color = text, fontSize = 19.sp, fontWeight = FontWeight.Black)
                    Spacer(Modifier.weight(1f))
                    if (searching) {
                        Text("CANLI", color = green, fontSize = 9.sp, fontWeight = FontWeight.Black)
                    }
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    if (searching) "Uygun bir rakip aranıyor. Eşleşme bulunduğunda maça geçeceksin."
                    else "Aynı oyun modunda bekleyen bir rakiple otomatik olarak eşleş.",
                    color = muted,
                    fontSize = 11.sp,
                    lineHeight = 17.sp
                )
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
                                error = e.message ?: "Arena eşleşmesine bağlanılamadı."
                            }
                        }
                    },
                    enabled = !searching,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = green),
                    shape = RoundedCornerShape(17.dp)
                ) {
                    Text(if (searching) "Rakip Bekleniyor…" else "Rakip Bul", color = Color(0xFF06221B), fontWeight = FontWeight.Black)
                }
                if (searching) {
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = {
                            searching = false
                            scope.launch { runCatching { OnlineArenaRepository.leaveQueue() } }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Eşleşmeyi İptal Et", color = gold, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Card(
            Modifier.fillMaxWidth(),
            RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = softSurface)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Arena nasıl çalışır?", color = text, fontSize = 13.sp, fontWeight = FontWeight.Black)
                Spacer(Modifier.height(9.dp))
                ArenaStep("01", "Rakibini bul", "Sisteme gir ve uygun rakip için eşleşme başlat.", text, muted, green)
                ArenaStep("02", "Mücadeleye başla", "Eşleşme bulunduğunda oyun ekranına geç.", text, muted, green)
                ArenaStep("03", "Bilgini göster", "Soruları cevapla, skorunu yükselt ve XP kazan.", text, muted, green)
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("🔒", fontSize = 13.sp)
            Spacer(Modifier.width(6.dp))
            Column {
                Text("Güvenli Arena altyapısı", color = green, fontSize = 10.sp, fontWeight = FontWeight.Black)
                Text("Eşleşme ve oyun verileri hesabınla güvenli şekilde yönetilir.", color = muted, fontSize = 10.sp)
            }
        }
    }
}

@Composable
private fun ArenaStep(
    number: String,
    title: String,
    description: String,
    text: Color,
    muted: Color,
    green: Color
) {
    Row(Modifier.fillMaxWidth().padding(vertical = 5.dp), verticalAlignment = Alignment.Top) {
        Box(
            Modifier.size(28.dp).background(green.copy(alpha = .12f), RoundedCornerShape(9.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(number, color = green, fontSize = 9.sp, fontWeight = FontWeight.Black)
        }
        Spacer(Modifier.width(10.dp))
        Column {
            Text(title, color = text, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(description, color = muted, fontSize = 9.sp, lineHeight = 14.sp)
        }
    }
}

@Composable
private fun OnlinePill(value: String, label: String) {
    Column(
        Modifier.background(Color.White.copy(alpha = .09f), RoundedCornerShape(11.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Black)
        Text(label, color = Color.White.copy(alpha = .55f), fontSize = 7.sp, fontWeight = FontWeight.Bold)
    }
}
