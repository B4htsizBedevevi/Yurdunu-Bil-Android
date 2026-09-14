package tr.yurdunubil.app


import androidx.activity.compose.BackHandler
import tr.yurdunubil.app.YurdunuBilCopy
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import java.time.Instant

@Serializable
data class ArenaQuestionRowV2(
    val match_id: String,
    val round_no: Int,
    val question_id: String,
    val question_topic: String? = null,
    val question_payload: JsonObject,
    val answer_deadline_at: String? = null
)

@Serializable
data class ArenaPlayerRowV2(
    val match_id: String,
    val user_id: String,
    val slot: Int,
    val score: Int = 0,
    val correct_answers: Int = 0,
    val ready: Boolean = false,
    val connected: Boolean = false
)

@Serializable
data class ArenaMatchStateV2(
    val id: String,
    val mode: String,
    val status: String,
    val host_id: String,
    val guest_id: String? = null,
    val current_round: Int = 0,
    val total_rounds: Int = 10,
    val winner_id: String? = null
)

private object ArenaOnlineRepositoryV2 {
    private val client get() = SupabaseClientProvider.client

    suspend fun match(id: String): ArenaMatchStateV2 = client.postgrest.from("arena_matches")
        .select { filter { eq("id", id) } }.decodeSingle()

    suspend fun questions(id: String): List<ArenaQuestionRowV2> = client.postgrest.from("arena_match_questions")
        .select { filter { eq("match_id", id) } }.decodeList()

    suspend fun players(id: String): List<ArenaPlayerRowV2> = client.postgrest.from("arena_players")
        .select { filter { eq("match_id", id) } }.decodeList()

    suspend fun ready(id: String) = client.postgrest.rpc("mark_arena_ready", buildJsonObject { put("p_match_id", JsonPrimitive(id)) })

    suspend fun answer(id: String, round: Int, selected: Int, responseMs: Int) = client.postgrest.rpc("record_arena_answer", buildJsonObject {
        put("p_match_id", JsonPrimitive(id)); put("p_round_no", JsonPrimitive(round)); put("p_selected_index", JsonPrimitive(selected)); put("p_response_ms", JsonPrimitive(responseMs))
    }).decodeAs<JsonObject>()

    suspend fun advance(id: String) = client.postgrest.rpc("advance_arena_round", buildJsonObject { put("p_match_id", JsonPrimitive(id)) })
}

@Composable
fun ArenaOnlineMatchScreenV2(darkMode: Boolean, mode: SharedGameMode, matchId: String, onBack: () -> Unit) {
    BackHandler { onBack() }
    val scope = rememberCoroutineScope()
    val client = remember { SupabaseClientProvider.client }
    val text = if (darkMode) Color(0xFFF1F7F4) else Color(0xFF06221B)
    val muted = if (darkMode) Color(0xFF92ADA2) else Color(0xFF70847B)
    val bg = if (darkMode) Color(0xFF06100D) else Color(0xFFF2F6F4)
    val surface = if (darkMode) Color(0xFF10221C) else Color.White
    var match by remember { mutableStateOf<ArenaMatchStateV2?>(null) }
    var players by remember { mutableStateOf(emptyList<ArenaPlayerRowV2>()) }
    var questions by remember { mutableStateOf(emptyList<ArenaQuestionRowV2>()) }
    var selected by remember { mutableStateOf<Int?>(null) }
    var answered by remember { mutableStateOf(false) }
    var secondsLeft by remember { mutableStateOf(mode.seconds.coerceAtMost(18)) }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(true) }

    var readySent by remember { mutableStateOf(false) }
    var lastSyncAt by remember { mutableStateOf(System.currentTimeMillis()) }
    var syncHealthy by remember { mutableStateOf(true) }

    suspend fun refresh(loadQuestions: Boolean = false) {
        try {
            withTimeout(5000L) {
                val m = ArenaOnlineRepositoryV2.match(matchId)
                val p = ArenaOnlineRepositoryV2.players(matchId)
                match = m
                players = p

                if (loadQuestions) {
                    questions = ArenaOnlineRepositoryV2.questions(matchId)
                }

                // Send READY once per screen lifecycle instead of once per polling tick.
                if ((m.status == "waiting" || m.status == "ready") && !readySent) {
                    ArenaOnlineRepositoryV2.ready(matchId)
                    readySent = true
                }

                lastSyncAt = System.currentTimeMillis()
                syncHealthy = true
                error = null
            }
        } catch (e: Exception) {
            syncHealthy = false
            error = e.message ?: "Arena verisi alınamadı."
        } finally {
            loading = false
        }
    }

    LaunchedEffect(matchId) {
        refresh(loadQuestions = true)
    }

    // Match/player state is refreshed frequently; the question list is kept in memory
    // and refreshed only when the server advances the round. This cuts unnecessary
    // network traffic while keeping the screen responsive.
    LaunchedEffect(Unit) {
        while (true) {
            val interval = when (match?.status) {
                "active" -> 700L
                "waiting", "ready" -> 1200L
                "finished" -> 6000L
                else -> 1500L
            }
            delay(interval)
            val beforeRound = match?.current_round
            refresh(loadQuestions = false)
            val afterRound = match?.current_round
            if (afterRound != null && beforeRound != null && afterRound != beforeRound) {
                refresh(loadQuestions = true)
            }
        }
    }

    val current = match?.let { m -> questions.firstOrNull { it.round_no == m.current_round } }
    val deadline = current?.answer_deadline_at?.let { runCatching { Instant.parse(it) }.getOrNull() }
    LaunchedEffect(deadline, match?.current_round) {
        while (true) {
            val d = deadline ?: break
            val left = ((d.toEpochMilli() - System.currentTimeMillis()) / 1000L).toInt()
            secondsLeft = left.coerceAtLeast(0)
            if (left <= 0) {
                answered = true
                scope.launch { runCatching { ArenaOnlineRepositoryV2.advance(matchId) }; refresh() }
                break
            }
            delay(250L)
        }
    }

    LaunchedEffect(match?.current_round) { selected = null; answered = false }

    val myId = client.auth.currentUserOrNull()?.id
    val mine = players.firstOrNull { it.user_id == myId }
    val opponent = players.firstOrNull { it.user_id != myId }
    val finished = match?.status == "finished"

    Box(Modifier.fillMaxSize().background(if (darkMode) Brush.verticalGradient(listOf(Color(0xFF08231B), bg, Color(0xFF04100D))) else Brush.verticalGradient(listOf(Color(0xFFE9F8F1), Color.White, bg)))) {
        Column(Modifier.fillMaxSize().padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Arena'ya dön", tint = text) }
                Column(Modifier.weight(1f)) {
                    Text("ARENA • CANLI", color = Color(0xFFFFC857), fontSize = 9.sp, fontWeight = FontWeight.Black, letterSpacing = 1.3.sp)
                    Text(mode.title, color = text, fontSize = 20.sp, fontWeight = FontWeight.Black)
                }
                Surface(color = (if (syncHealthy) Color(0xFF18C986) else Color(0xFFFFC857)).copy(alpha = .1f), shape = RoundedCornerShape(12.dp)) {
                    Text(
                        when { finished -> "BİTTİ"; syncHealthy -> "ANLIK"; else -> "SENKRON KONTROL" },
                        color = if (syncHealthy) Color(0xFF18C986) else Color(0xFFFFC857),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 7.dp)
                    )
                }
            }

            if (error != null) Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF3A211F))) { Text(error!!, color = Color(0xFFFFB5AD), fontSize = 11.sp, modifier = Modifier.padding(12.dp)) }
            if (!syncHealthy && error != null) {
                Text("Bağlantı geçici olarak zayıf. Otomatik yeniden denenecek.", color = Color(0xFFFFC857), fontSize = 9.sp, modifier = Modifier.padding(horizontal = 4.dp))
            } else {
                val age = ((System.currentTimeMillis() - lastSyncAt) / 1000L).coerceAtLeast(0L)
                Text(if (age <= 2) "● Sunucu ile senkron" else "○ Son senkron ${age}s önce", color = if (age <= 2) Color(0xFF18C986) else muted, fontSize = 8.sp, modifier = Modifier.padding(horizontal = 4.dp))
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ScoreCardV2("SEN", mine?.score ?: 0, Color(0xFF18C986), text, Modifier.weight(1f))
                ScoreCardV2("RAKİP", opponent?.score ?: 0, Color(0xFFFFC857), text, Modifier.weight(1f))
            }

            if (loading) {
                Box(Modifier.fillMaxWidth().height(300.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Color(0xFF18C986)) }
            } else if (finished) {
                val title = when (match?.winner_id) { null -> "BERABERE"; myId -> "ZAFER!"; else -> "MAÇ BİTTİ" }
                Card(Modifier.fillMaxWidth(), RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = surface)) {
                    Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(title, color = Color(0xFF18C986), fontSize = 28.sp, fontWeight = FontWeight.Black)
                        Spacer(Modifier.height(10.dp))
                        Text("${mine?.score ?: 0}  —  ${opponent?.score ?: 0}", color = text, fontSize = 34.sp, fontWeight = FontWeight.Black)
                        Spacer(Modifier.height(8.dp))
                        Text("Arena sonucu hesabına işlendi.", color = muted, fontSize = 11.sp)
                        Spacer(Modifier.height(18.dp))
                        Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF18C986)), shape = RoundedCornerShape(16.dp)) { Text("Arena'ya Dön", color = Color(0xFF06221B), fontWeight = FontWeight.Black) }
                    }
                }
            } else if (match?.status != "active" || current == null) {
                Card(Modifier.fillMaxWidth(), RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = surface)) {
                    Column(Modifier.padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Shield, null, tint = Color(0xFF18C986), modifier = Modifier.size(36.dp))
                        Spacer(Modifier.height(10.dp))
                        Text(if (match?.status == "ready") "Rakibin hazır olması bekleniyor" else "Maç hazırlanıyor…", color = text, fontSize = 17.sp, fontWeight = FontWeight.Black)
                        Spacer(Modifier.height(7.dp))
                        Text("İki oyuncu hazır olduğunda ilk soru otomatik açılır.", color = muted, fontSize = 11.sp)
                    }
                }
            } else {
                Card(Modifier.fillMaxWidth(), RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = surface)) {
                    Column(Modifier.padding(17.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("SORU ${match!!.current_round}/${match!!.total_rounds}", color = Color(0xFF18C986), fontSize = 10.sp, fontWeight = FontWeight.Black)
                            Spacer(Modifier.weight(1f))
                            Text("${secondsLeft}s", color = if (secondsLeft <= 3) Color(0xFFE65353) else Color(0xFFFFC857), fontSize = 15.sp, fontWeight = FontWeight.Black)
                        }
                        Spacer(Modifier.height(13.dp))
                        Text(current.question_payload["text"]?.jsonPrimitive?.content ?: "Soru yüklenemedi.", color = text, fontSize = 18.sp, fontWeight = FontWeight.Bold, lineHeight = 25.sp)
                        Spacer(Modifier.height(13.dp))
                        val optionValues = current.question_payload["options"]?.jsonArray?.map { it.jsonPrimitive.content } ?: emptyList()
                        optionValues.forEachIndexed { i, option ->
                            val picked = selected == i
                            Button(
                                onClick = {
                                    if (answered || picked) return@Button
                                    selected = i; answered = true
                                    val responseMs = (mode.seconds - secondsLeft).coerceAtLeast(0) * 1000
                                    scope.launch {
                                        val result = runCatching { ArenaOnlineRepositoryV2.answer(matchId, match!!.current_round, i, responseMs) }
                                        if (result.isFailure) { answered = false; error = result.exceptionOrNull()?.message ?: "Cevap gönderilemedi." }
                                        refresh()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp).height(52.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = if (picked) Color(0xFF18C986).copy(alpha = .18f) else if (darkMode) Color.White.copy(alpha = .05f) else Color.Black.copy(alpha = .04f))
                            ) { Text("${('A'.code + i).toChar()}  $option", color = text, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth()) }
                        }
                        if (answered) {
                            val explanationQuestion = current.question_id
                                ?.let { id -> SharedQuestionPool.all.firstOrNull { it.id.toString() == id.toString() } }
                            Card(
                                Modifier.fillMaxWidth().padding(top = 10.dp),
                                RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (darkMode) Color(0xFF143128) else Color(0xFFEAF8F2)
                                )
                            ) {
                                Column(Modifier.padding(14.dp)) {
                                    Text(
                                        if (selected == null) "SORU AÇIKLAMASI" else "SORU AÇIKLAMASI • ÖĞREN",
                                        color = Color(0xFF18C986),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Spacer(Modifier.height(5.dp))
                                    Text(
                                        explanationQuestion?.explanation
                                            ?: "Cevabın kaydedildi. Rakibin cevabı bekleniyor.",
                                        color = text,
                                        fontSize = 12.sp,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                            Text(
                                "Cevabın kaydedildi. Rakibin bekleniyor…",
                                color = Color(0xFF18C986),
                                fontSize = 10.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScoreCardV2(name: String, score: Int, accent: Color, text: Color, modifier: Modifier) {
    Card(modifier, RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = accent.copy(alpha = .08f))) {
        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Person, null, tint = accent, modifier = Modifier.size(23.dp))
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) { Text(name, color = text, fontSize = 9.sp, fontWeight = FontWeight.Black); Text(score.toString(), color = accent, fontSize = 22.sp, fontWeight = FontWeight.Black) }
        }
    }
}
