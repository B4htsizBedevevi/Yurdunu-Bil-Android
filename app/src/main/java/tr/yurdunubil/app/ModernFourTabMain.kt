package tr.yurdunubil.app

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlin.math.roundToInt

private val G = Color(0xFF18C986)
private val Deep = Color(0xFF06221B)
private val Gold = Color(0xFFFFC857)
private val Red = Color(0xFFE65353)
private val LightBg = Color(0xFFF3F8F5)
private val LightCard = Color(0xFFFFFFFF)
private val LightText = Color(0xFF09251D)
private val LightMuted = Color(0xFF71857D)
private val DarkBg = Color(0xFF061411)
private val DarkCard = Color(0xFF10231D)
private val DarkText = Color(0xFFF0FAF5)
private val DarkMuted = Color(0xFF9AB5AA)

@Composable
fun ModernFourTabMainApp() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("yurdunu_bil_native", 0) }
    var dark by remember { mutableStateOf(prefs.getBoolean("dark_mode", false)) }
    var tab by remember { mutableIntStateOf(0) }
    var quiz by remember { mutableStateOf<List<Question>?>(null) }
    var quizTitle by remember { mutableStateOf("") }

    val bg = if (dark) DarkBg else LightBg
    val card = if (dark) DarkCard else LightCard
    val text = if (dark) DarkText else LightText
    val muted = if (dark) DarkMuted else LightMuted

    MaterialTheme(colorScheme = if (dark) darkColorScheme(primary = G, background = bg, surface = card, onBackground = text, onSurface = text) else lightColorScheme(primary = G, background = bg, surface = card, onBackground = text, onSurface = text)) {
        if (quiz != null) {
            ModernQuizScreen(title = quizTitle, questions = quiz!!, prefs = prefs, dark = dark, onBack = { quiz = null })
            return@MaterialTheme
        }
        Scaffold(
            containerColor = bg,
            bottomBar = {
                NavigationBar(containerColor = card, tonalElevation = 4.dp) {
                    listOf("Ana Sayfa" to Icons.Default.Home, "Kütüphane" to Icons.Default.MenuBook, "Etkinlikler" to Icons.Default.SportsEsports, "Ayarlar" to Icons.Default.Settings).forEachIndexed { i, pair ->
                        NavigationBarItem(selected = tab == i, onClick = { tab = i }, icon = { Icon(pair.second, pair.first, Modifier.size(23.dp)) }, label = { Text(pair.first, fontSize = 10.sp) }, colors = NavigationBarItemDefaults.colors(selectedIconColor = G, selectedTextColor = text, indicatorColor = if (dark) Color(0xFF17392E) else Color(0xFFE0F7EC), unselectedIconColor = muted, unselectedTextColor = muted))
                    }
                }
            }
        ) { pad ->
            Box(Modifier.fillMaxSize().padding(pad)) {
                when (tab) {
                    0 -> ModernHome(text, muted, card, prefs) { title, qs -> quizTitle = title; quiz = qs }
                    1 -> ModernLibrary(text, muted, card) { title, qs -> quizTitle = title; quiz = qs }
                    2 -> ModernEvents(text, muted, card, dark) { title, qs -> quizTitle = title; quiz = qs }
                    else -> ModernSettings(text, muted, card, dark, { value -> dark = value; prefs.edit().putBoolean("dark_mode", value).apply() })
                }
            }
        }
    }
}

@Composable
private fun ModernHome(text: Color, muted: Color, card: Color, prefs: android.content.SharedPreferences, launch: (String, List<Question>) -> Unit) {
    val solved = prefs.getInt("solved", 0)
    val correct = prefs.getInt("correct", 0)
    val xp = prefs.getInt("xp", 0)
    val accuracy = if (solved == 0) 0 else (correct * 100f / solved).roundToInt()
    val daily = SharedQuestionPool.dailyMode()
    var intro by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) { delay(450); intro = false }
    LazyColumn(contentPadding = PaddingValues(16.dp, 18.dp, 16.dp, 26.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            if (intro) {
                Box(Modifier.fillMaxWidth().height(92.dp).clip(RoundedCornerShape(26.dp)).background(Brush.linearGradient(listOf(Deep, Color(0xFF0D5A43)))), contentAlignment = Alignment.CenterStart) {
                    Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) { ImageLogo(52); Spacer(Modifier.width(12.dp)); Column { Text("Yurdunu Bil", color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.Black); Text("Geleceğini Bil.", color = G, fontWeight = FontWeight.Bold, fontSize = 12.sp) } }
                }
            } else {
                Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(26.dp)).background(Brush.linearGradient(listOf(Deep, Color(0xFF0E5B43)))).padding(20.dp)) {
                    Column { Row(verticalAlignment = Alignment.CenterVertically) { ImageLogo(48); Spacer(Modifier.width(11.dp)); Column(Modifier.weight(1f)) { Text("Yurdunu Bil", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black); Text("KPSS Önlisans • Türkiye Coğrafyası", color = Color(0xFFC9F8E1), fontSize = 11.sp) } Text("Lv.${1 + xp / 500}", color = Color.White, fontWeight = FontWeight.Bold) }; Spacer(Modifier.height(15.dp)); Text("Bugün Türkiye'yi biraz daha çöz. ✨", color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold); Spacer(Modifier.height(13.dp)); Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { Stat("$solved", "SORU"); Stat("$xp", "XP"); Stat("%$accuracy", "DOĞRULUK") } }
                }
            }
        }
        item { CompactActionCard("🎯", "Bugünün Görevi", daily.title, "${daily.questions} soru • +${daily.rewardXp} XP", card, text, muted) { launch(daily.title, SharedQuestionPool.pick(daily)) } }
        item { Text("Hızlı Başla", color = text, fontSize = 19.sp, fontWeight = FontWeight.Black) }
        item { Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) { MiniGame("⚡", "Hızlı 10", card, text) { launch("Hızlı 10", SharedQuestionPool.pick(SharedGameModes.quick)) }; MiniGame("🔥", "Bilgi Zinciri", card, text) { launch("Bilgi Zinciri", SharedQuestionPool.pick(SharedGameModes.chain)) } } }
        item { CompactActionCard("📚", "Gelişimin", "$accuracy% genel doğruluk", if (solved == 0) "İlk turunu tamamla." else "$correct doğru cevap", card, text, muted, accent = G) { launch("Hızlı 10", SharedQuestionPool.pick(SharedGameModes.quick)) } }
    }
}

@Composable
private fun ModernLibrary(text: Color, muted: Color, card: Color, launch: (String, List<Question>) -> Unit) {
    var search by remember { mutableStateOf("") }
    val topics = GeographyData.topics.filter { search.isBlank() || it.title.contains(search, true) || it.subtitle.contains(search, true) }
    LazyColumn(contentPadding = PaddingValues(16.dp, 18.dp, 16.dp, 26.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("Kütüphane", color = text, fontSize = 29.sp, fontWeight = FontWeight.Black); Text("Konu bankası büyüyor • oku, çöz, tekrar et", color = G, fontSize = 12.sp, fontWeight = FontWeight.Bold); Spacer(Modifier.height(8.dp)); OutlinedTextField(search, { search = it }, Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(16.dp), placeholder = { Text("Konu ara…") }, leadingIcon = { Icon(Icons.Default.Search, null) }) }
        item { Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp)).background(Brush.linearGradient(listOf(Deep, Color(0xFF11664B)))).padding(17.dp)) { Row(verticalAlignment = Alignment.CenterVertically) { Text("🗺️", fontSize = 31.sp); Spacer(Modifier.width(11.dp)); Column { Text("TÜRKİYE ATLASI", color = Color(0xFFC9F8E1), fontSize = 10.sp, fontWeight = FontWeight.Black); Text("81 il • 7 bölge", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black); Text("İpucu ve KPSS bağlantılarıyla keşfet.", color = Color.White.copy(.68f), fontSize = 11.sp) } } } }
        item { Text("Konu Bankası • ${topics.size} ana konu", color = text, fontSize = 18.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(top = 4.dp)) }
        items(topics, key = { it.title }) { topic ->
            TopicCompact(topic, card, text, muted) { launch(topic.title, SharedQuestionPool.pick(SharedGameMode("topic-${topic.title}", topic.title, topic.subtitle, topic.icon, 12, 180, 120, SharedQuestionPool.topicForLibrary(topic.title)))) }
        }
    }
}

@Composable
private fun ModernEvents(text: Color, muted: Color, card: Color, dark: Boolean, launch: (String, List<Question>) -> Unit) {
    var arena by remember { mutableStateOf(false) }
    LazyColumn(contentPadding = PaddingValues(16.dp, 18.dp, 16.dp, 26.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("Etkinlikler", color = text, fontSize = 29.sp, fontWeight = FontWeight.Black); Text("Oyunlar ve çok oyunculu Arena artık ayrı.", color = G, fontSize = 12.sp, fontWeight = FontWeight.Bold); Spacer(Modifier.height(10.dp)); Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(if (dark) Color(0xFF132A22) else Color(0xFFE4F7ED)).padding(4.dp)) { ModeTab("🎮 Oyunlar", !arena, Modifier.weight(1f)) { arena = false }; ModeTab("⚔️ Arena", arena, Modifier.weight(1f)) { arena = true } } }
        if (!arena) {
            items(SharedGameModes.games, key = { it.id }) { mode -> GameCard(mode, card, text, muted) { launch(mode.title, SharedQuestionPool.pick(mode)) } }
        } else {
            item { Text("Çok Oyunculu", color = text, fontSize = 19.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(top = 5.dp)) }
            item { ArenaHub(card, text, muted) }
            items(SharedGameModes.arenaModes, key = { it.id }) { mode -> ArenaCard(mode, card, text, muted) { ArenaMatchScreen(mode) } }
        }
    }
}

@Composable
private fun ArenaHub(card: Color, text: Color, muted: Color) {
    val scope = rememberCoroutineScope()
    var status by remember { mutableStateOf("Hazır • DB bağlantısı bekleniyor") }
    var busy by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp)).background(card).border(1.dp, G.copy(.18f), RoundedCornerShape(22.dp)).padding(16.dp)) {
        Text("Canlı eşleştirme", color = text, fontSize = 19.sp, fontWeight = FontWeight.Black)
        Text(status, color = muted, fontSize = 11.sp, modifier = Modifier.padding(top = 3.dp))
        Spacer(Modifier.height(10.dp))
        Button(onClick = { scope.launch { busy = true; status = "Rakip aranıyor…"; try { val queue = ArenaBackend.enqueue("duel"); val match = ArenaBackend.tryMatch("duel"); status = if (match != null) "Eşleşme bulundu • Oda ${match.roomCode}" else "Kuyruktasın • rakip bulunduğunda eşleşeceksin" } catch (e: Exception) { status = "Arena bağlantısı: ${e.message?.take(70) ?: "hata"}" }; busy = false } }, enabled = !busy, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) { Text(if (busy) "Aranıyor…" else "1v1 Rakip Bul") }
    }
}

@Composable
private fun ArenaMatchScreen(mode: SharedGameMode) {
    val scope = rememberCoroutineScope()
    var status by remember { mutableStateOf("Rakip bekleniyor") }
    var room by remember { mutableStateOf("") }
    LaunchedEffect(mode.id) { scope.launch { try { ArenaBackend.enqueue(mode.id.removeSuffix("-arena").replace("region-arena", "region").replace("master-arena", "master")); val match = ArenaBackend.tryMatch(mode.id.removeSuffix("-arena").replace("region-arena", "region").replace("master-arena", "master")); if (match != null) { room = match.roomCode; status = "Rakip bulundu • oda hazır" } } catch (_: Exception) {} } }
    AlertDialog(onDismissRequest = {}, title = { Text("${mode.icon} ${mode.title}", fontWeight = FontWeight.Black) }, text = { Column { Text(status); if (room.isNotBlank()) Text("Oda: $room", color = G, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp)) } }, confirmButton = { TextButton({}) { Text("Tamam") } })
}

@Composable
private fun ModernSettings(text: Color, muted: Color, card: Color, dark: Boolean, onDark: (Boolean) -> Unit) {
    val context = LocalContext.current
    var message by remember { mutableStateOf("") }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted -> if (granted) sendTestNotification(context) else message = "Bildirim izni verilmedi." }
    LazyColumn(contentPadding = PaddingValues(16.dp, 18.dp, 16.dp, 26.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("Ayarlar", color = text, fontSize = 29.sp, fontWeight = FontWeight.Black); Text("Uygulamayı gözünü yormadan kişiselleştir.", color = G, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
        item { SettingRow("🌙", "Karanlık Tema", if (dark) "Açık • göz dostu" else "Kapalı", card, text) { onDark(!dark) } }
        item { SettingRow("🔔", "Bildirimler", "Test bildirimi gönder", card, text) { if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) else { sendTestNotification(context); message = "Test bildirimi gönderildi." } } }
        if (message.isNotBlank()) item { Text(message, color = G, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
        item { SettingRow("📊", "İstatistikler", "İlerleme cihazda korunuyor", card, text) {} }
        item { SettingRow("☁️", "Supabase", "Giriş ve Arena altyapısı bağlı", card, text) {} }
        item { SettingRow("⚔️", "Arena", "Canlı eşleştirme altyapısı hazır", card, text) {} }
        item { SettingRow("ℹ️", "Sürüm", "Yurdunu Bil Android • 0.3.0", card, text) {} }
    }
}

@Composable
private fun ModernQuizScreen(title: String, questions: List<Question>, prefs: android.content.SharedPreferences, dark: Boolean, onBack: () -> Unit) {
    var index by remember { mutableIntStateOf(0) }
    var selected by remember { mutableStateOf<Int?>(null) }
    var answered by remember { mutableStateOf(false) }
    var correct by remember { mutableIntStateOf(0) }
    var wrong by remember { mutableIntStateOf(0) }
    var showResult by remember { mutableStateOf(false) }
    val q = questions[index]
    val text = if (dark) DarkText else LightText
    val muted = if (dark) DarkMuted else LightMuted
    val card = if (dark) DarkCard else LightCard
    if (showResult) {
        ResultCard(title, questions.size, correct, wrong, dark, onBack)
        return
    }
    LazyColumn(contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 24.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Row(verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = onBack) { Icon(Icons.Default.Close, "Kapat", tint = text) }; Column(Modifier.weight(1f)) { Text(title, color = text, fontSize = 20.sp, fontWeight = FontWeight.Black); Text("Soru ${index + 1} / ${questions.size}", color = G, fontSize = 11.sp, fontWeight = FontWeight.Bold) }; Text("+${correct * 10} XP", color = G, fontWeight = FontWeight.Bold) } }
        item { LinearProgressIndicator({ (index + 1f) / questions.size }, Modifier.fillMaxWidth().height(7.dp).clip(CircleShape), color = G, trackColor = if (dark) Color(0xFF193A2E) else Color(0xFFDDF2E8)) }
        item { Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp)).background(card).border(1.dp, G.copy(.14f), RoundedCornerShape(22.dp)).padding(18.dp)) { Column { Text(q.topic, color = G, fontSize = 11.sp, fontWeight = FontWeight.Black); Spacer(Modifier.height(7.dp)); Text(q.text, color = text, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 27.sp) } } }
        item { AnswerGrid(q, selected, answered, dark, onSelect = { choice -> if (!answered) { selected = choice; answered = true; if (choice == q.correctIndex) correct++ else wrong++ } }) }
        if (answered) item { ExplanationCard(q, selected, dark) }
        if (answered) item { Button(onClick = { if (index == questions.lastIndex) showResult = true else { index++; selected = null; answered = false } }, Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(17.dp), colors = ButtonDefaults.buttonColors(containerColor = Deep)) { Text(if (index == questions.lastIndex) "Sonucu Gör" else "Sonraki Soru  →", color = Color.White, fontWeight = FontWeight.Bold) } }
    }
}

@Composable
private fun AnswerGrid(q: Question, selected: Int?, answered: Boolean, dark: Boolean, onSelect: (Int) -> Unit) {
    val card = if (dark) DarkCard else LightCard
    val text = if (dark) DarkText else LightText
    LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.fillMaxWidth().height(245.dp), verticalArrangement = Arrangement.spacedBy(9.dp), horizontalArrangement = Arrangement.spacedBy(9.dp), userScrollEnabled = false) {
        items(q.options.indices.toList()) { i ->
            val correct = answered && i == q.correctIndex
            val wrong = answered && i == selected && i != q.correctIndex
            val bg = when { correct -> Color(0xFFDDF8EA); wrong -> Color(0xFFFFE1E1); else -> card }
            val border = when { correct -> G; wrong -> Red; else -> if (dark) Color(0xFF29473D) else Color(0xFFE0EAE5) }
            Box(Modifier.height(70.dp).clip(RoundedCornerShape(17.dp)).background(bg).border(2.dp, border, RoundedCornerShape(17.dp)).clickable { onSelect(i) }.padding(10.dp), contentAlignment = Alignment.CenterStart) {
                Row(verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(29.dp).clip(CircleShape).background(border.copy(.13f)), contentAlignment = Alignment.Center) { Text(('A'.code + i).toChar().toString(), color = if (wrong) Red else if (correct) G else text, fontSize = 12.sp, fontWeight = FontWeight.Black) }; Spacer(Modifier.width(8.dp)); Text(q.options[i], color = if (dark && !correct && !wrong) DarkText else LightText, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, maxLines = 3, overflow = TextOverflow.Ellipsis) }
            }
        }
    }
}

@Composable
private fun ExplanationCard(q: Question, selected: Int?, dark: Boolean) {
    val good = selected == q.correctIndex
    val bg = if (good) Color(0xFFEAF9F1) else Color(0xFFFFF5DF)
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(bg).padding(15.dp)) {
        Text(if (good) "✓ DOĞRU" else "DİKKAT KÖŞESİ", color = if (good) G else Color(0xFFD89A22), fontSize = 11.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(5.dp)); Text(q.explanation, color = Color(0xFF16382C), fontSize = 12.sp, lineHeight = 18.sp)
    }
}

@Composable
private fun ResultCard(title: String, total: Int, correct: Int, wrong: Int, dark: Boolean, onBack: () -> Unit) {
    val text = if (dark) DarkText else LightText
    Box(Modifier.fillMaxSize().background(if (dark) DarkBg else LightBg), contentAlignment = Alignment.Center) { Column(Modifier.padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text("🏆", fontSize = 55.sp); Text("Tur tamamlandı", color = text, fontSize = 27.sp, fontWeight = FontWeight.Black); Text(title, color = G, fontWeight = FontWeight.Bold); Spacer(Modifier.height(18.dp)); Text("$correct / $total doğru", color = text, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold); Text("$wrong yanlış • +${correct * 10} XP", color = if (dark) DarkMuted else LightMuted); Spacer(Modifier.height(22.dp)); Button(onClick = onBack, Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(17.dp)) { Text("Etkinliklere Dön") } } }
}

@Composable private fun ImageLogo(size: Int) { androidx.compose.foundation.Image(painterResource(R.drawable.yurdunu_bil_logo), "Yurdunu Bil", Modifier.size(size.dp).clip(RoundedCornerShape((size / 4).dp))) }
@Composable private fun Stat(value: String, label: String) { Box(Modifier.clip(RoundedCornerShape(12.dp)).background(Color.White.copy(.10f)).padding(horizontal = 10.dp, vertical = 7.dp)) { Column { Text(value, color = Color.White, fontWeight = FontWeight.Black, fontSize = 13.sp); Text(label, color = Color.White.copy(.65f), fontSize = 8.sp) } } }
@Composable private fun CompactActionCard(icon: String, eyebrow: String, title: String, subtitle: String, card: Color, text: Color, muted: Color, accent: Color = G, onClick: () -> Unit) { Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(card).border(1.dp, accent.copy(.15f), RoundedCornerShape(20.dp)).clickable(onClick = onClick).padding(14.dp), verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(50.dp).clip(RoundedCornerShape(15.dp)).background(accent.copy(.12f)), contentAlignment = Alignment.Center) { Text(icon, fontSize = 25.sp) }; Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(eyebrow, color = accent, fontSize = 9.sp, fontWeight = FontWeight.Black); Text(title, color = text, fontSize = 17.sp, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis); Text(subtitle, color = muted, fontSize = 11.sp) }; Icon(Icons.Default.ChevronRight, null, tint = accent) } }
@Composable private fun MiniGame(icon: String, title: String, card: Color, text: Color, onClick: () -> Unit) { Column(Modifier.weight(1f).clip(RoundedCornerShape(18.dp)).background(card).clickable(onClick = onClick).padding(15.dp)) { Text(icon, fontSize = 25.sp); Spacer(Modifier.height(6.dp)); Text(title, color = text, fontWeight = FontWeight.Black, fontSize = 14.sp); Text("Hemen başla", color = G, fontSize = 10.sp) } }
@Composable private fun TopicCompact(topic: Topic, card: Color, text: Color, muted: Color, onClick: () -> Unit) { Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(19.dp)).background(card).clickable(onClick = onClick).padding(13.dp), verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(52.dp).clip(RoundedCornerShape(15.dp)).background(Color(0xFFE3F7ED)), contentAlignment = Alignment.Center) { Text(topic.icon, fontSize = 27.sp) }; Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(topic.title, color = text, fontSize = 16.sp, fontWeight = FontWeight.Black); Text(topic.subtitle, color = muted, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis); LinearProgressIndicator({ topic.progress / 100f }, Modifier.fillMaxWidth().padding(top = 6.dp).height(5.dp).clip(CircleShape), color = G, trackColor = Color(0xFFE1F2EA)) }; Icon(Icons.Default.ArrowForward, null, tint = G) } }
@Composable private fun GameCard(mode: SharedGameMode, card: Color, text: Color, muted: Color, onClick: () -> Unit) { Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(19.dp)).background(card).clickable(onClick = onClick).padding(14.dp), verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(52.dp).clip(RoundedCornerShape(15.dp)).background(Color(0xFFE4F7ED)), contentAlignment = Alignment.Center) { Text(mode.icon, fontSize = 26.sp) }; Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(mode.title, color = text, fontSize = 16.sp, fontWeight = FontWeight.Black); Text(mode.subtitle, color = muted, fontSize = 11.sp); Text("${mode.questions} soru • ${mode.seconds} sn • +${mode.rewardXp} XP", color = G, fontSize = 10.sp, fontWeight = FontWeight.Bold) }; Icon(Icons.Default.PlayArrow, null, tint = G) } }
@Composable private fun ArenaCard(mode: SharedGameMode, card: Color, text: Color, muted: Color, onClick: () -> Unit) { Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(19.dp)).background(card).border(1.dp, Gold.copy(.28f), RoundedCornerShape(19.dp)).clickable(onClick = onClick).padding(14.dp), verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(52.dp).clip(RoundedCornerShape(15.dp)).background(Color(0xFFFFF5DF)), contentAlignment = Alignment.Center) { Text(mode.icon, fontSize = 26.sp) }; Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text("ARENA", color = Gold, fontSize = 9.sp, fontWeight = FontWeight.Black); Text(mode.title, color = text, fontSize = 16.sp, fontWeight = FontWeight.Black); Text(mode.subtitle, color = muted, fontSize = 11.sp); Text("Canlı eşleşme • +${mode.rewardXp} XP", color = Gold, fontSize = 10.sp, fontWeight = FontWeight.Bold) }; Icon(Icons.Default.ChevronRight, null, tint = Gold) } }
@Composable private fun ModeTab(text: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) { Box(modifier.clip(RoundedCornerShape(13.dp)).background(if (selected) G.copy(.14f) else Color.Transparent).clickable(onClick = onClick).padding(vertical = 11.dp), contentAlignment = Alignment.Center) { Text(text, color = if (selected) G else LightMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold) } }
@Composable private fun SettingRow(icon: String, title: String, subtitle: String, card: Color, text: Color, onClick: () -> Unit) { Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(19.dp)).background(card).clickable(onClick = onClick).padding(15.dp), verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(45.dp).clip(RoundedCornerShape(14.dp)).background(G.copy(.12f)), contentAlignment = Alignment.Center) { Text(icon, fontSize = 22.sp) }; Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(title, color = text, fontSize = 15.sp, fontWeight = FontWeight.Black); Text(subtitle, color = LightMuted, fontSize = 11.sp) }; Icon(Icons.Default.ChevronRight, null, tint = G) } }

@Serializable private data class ArenaQueueRow(val userId: String, val mode: String, val rating: Int, val queuedAt: String, val expiresAt: String)
@Serializable private data class ArenaMatchRow(val id: String, val mode: String, val status: String, val hostId: String, val guestId: String? = null, val roomCode: String, val currentRound: Int = 0, val totalRounds: Int = 10)

private object ArenaBackend {
    suspend fun enqueue(mode: String): ArenaQueueRow = SupabaseClientProvider.client.postgrest.rpc("enqueue_arena", mapOf("p_mode" to mode)).decodeSingle()
    suspend fun tryMatch(mode: String): ArenaMatchRow? = try { SupabaseClientProvider.client.postgrest.rpc("try_match_arena", mapOf("p_mode" to mode)).decodeSingle<ArenaMatchRow>() } catch (_: Exception) { null }
}

private fun sendTestNotification(context: Context) {
    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val channelId = "yurdunu_bil_test"
    if (Build.VERSION.SDK_INT >= 26) manager.createNotificationChannel(NotificationChannel(channelId, "Yurdunu Bil Bildirimleri", NotificationManager.IMPORTANCE_DEFAULT))
    manager.notify(9001, NotificationCompat.Builder(context, channelId).setSmallIcon(android.R.drawable.ic_dialog_info).setContentTitle("Yurdunu Bil hazır! 🧭").setContentText("Test bildirimi başarıyla ulaştı. Bildirim sistemi cihazında çalışıyor.").setAutoCancel(true).build())
}
