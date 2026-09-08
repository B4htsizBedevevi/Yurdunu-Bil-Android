package tr.yurdunubil.app

import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

private val VBg = Color(0xFFF3F8F5)
private val VDeep = Color(0xFF06221B)
private val VDeep2 = Color(0xFF0B342B)
private val VGreen = Color(0xFF18C986)
private val VMint = Color(0xFFC9F8E1)
private val VGold = Color(0xFFFFC857)
private val VMuted = Color(0xFF70847B)
private val VSoft = Color(0xFFE4F7ED)

@Composable
fun YurdunuBilMainV2App() {
    val prefs = remember { androidx.compose.ui.platform.LocalContext.current.getSharedPreferences("yurdunu_bil_native", 0) }
    var tab by remember { mutableIntStateOf(0) }
    var study by remember { mutableStateOf<Topic?>(null) }
    var quiz by remember { mutableStateOf<List<Question>?>(null) }
    var quizTitle by remember { mutableStateOf("") }

    fun launch(title: String, mode: SharedGameMode) {
        val questions = SharedQuestionPool.pick(mode)
        if (questions.isNotEmpty()) { quizTitle = title; quiz = questions }
    }

    if (study != null) {
        val selected = study!!
        LibraryStudyScreen(selected, { study = null }) {
            study = null
            launch(selected.title, SharedGameMode("study-${selected.title}", selected.title, selected.subtitle, selected.icon, 10, 180, 100, SharedQuestionPool.topicForLibrary(selected.title)))
        }
        return
    }
    if (quiz != null) {
        V2QuizScreen(quizTitle, quiz!!, prefs) { quiz = null }
        return
    }

    Scaffold(containerColor = VBg, bottomBar = {
        NavigationBar(containerColor = Color.White) {
            val nav = listOf("Ana Sayfa" to Icons.Default.Home, "Kütüphane" to Icons.Default.MenuBook, "Etkinlikler" to Icons.Default.SportsEsports, "Ayarlar" to Icons.Default.Settings)
            nav.forEachIndexed { i, pair -> NavigationBarItem(tab == i, { tab = i }, icon = { Icon(pair.second, null) }, label = { Text(pair.first, fontSize = 10.sp) }) }
        }
    }) { padding ->
        Box(Modifier.fillMaxSize().padding(padding).statusBarsPadding()) {
            when (tab) {
                0 -> HomeScreen(prefs, { launch("Hızlı 10", SharedGameModes.quick) }, { tab = 2 })
                1 -> LibraryScreen { study = it }
                2 -> EventsScreen { launch(it.title, it) }
                else -> SettingsScreen(prefs)
            }
        }
    }
}

@Composable private fun HomeScreen(p: SharedPreferences, quick: () -> Unit, events: () -> Unit) {
    val solved = p.getInt("solved", 0); val correct = p.getInt("correct", 0); val xp = p.getInt("xp", 0); val streak = p.getInt("streak", 0)
    val accuracy = if (solved == 0) 0 else (correct * 100f / solved).roundToInt()
    LazyColumn(contentPadding = PaddingValues(bottom = 28.dp), verticalArrangement = Arrangement.spacedBy(13.dp)) {
        item { HeaderCard(xp, solved, streak) }
        item { ActionCard("🎯", "BUGÜNÜN GÖREVİ", SharedGameModes.daily(java.time.LocalDate.now()).title, "Kısa bir coğrafya turuyla ritmini koru.", VGreen, quick) }
        item { Row(Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) { SmallAction("⚡", "Hızlı 10", quick); SmallAction("📚", "Kütüphane") { } } }
        item { ActionCard("⚔️", "ARENA", "Bilgini sahaya çıkar.", "1v1 • hız • bölge • Türkiye Ustası", VGold, events, dark = true) }
        item { Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp), colors = CardDefaults.cardColors(Color.White), shape = RoundedCornerShape(20.dp)) { Column(Modifier.padding(16.dp)) { Text("GELİŞİMİN", color = VGreen, fontSize = 10.sp, fontWeight = FontWeight.Black); Text("%$accuracy genel doğruluk", color = VDeep, fontSize = 19.sp, fontWeight = FontWeight.Black); Text(if (solved == 0) "İlk testini çöz ve ilerlemeni başlat." else "$correct doğru cevapla devam ediyorsun.", color = VMuted, fontSize = 11.sp); Spacer(Modifier.height(10.dp)); LinearProgressIndicator({ accuracy / 100f }, Modifier.fillMaxWidth().height(7.dp).clip(RoundedCornerShape(8.dp)), color = VGreen, trackColor = VSoft) } } }
    }
}

@Composable private fun HeaderCard(xp: Int, solved: Int, streak: Int) { Box(Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(VDeep, VDeep2, Color(0xFF11634A)),), RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)).padding(21.dp)) { Column { Row(verticalAlignment = Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Text("Yurdunu Bil", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black); Text("KPSS Önlisans • Türkiye Coğrafyası", color = VMint, fontSize = 11.sp) }; Text("Lv.${1 + xp / 500}", color = Color.White, fontWeight = FontWeight.Bold) }; Spacer(Modifier.height(17.dp)); Text("Bugün Türkiye'yi biraz daha çöz. ✨", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold); Spacer(Modifier.height(13.dp)); Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { Stat("$solved", "SORU"); Stat("$xp", "XP"); Stat("$streak", "SERİ") } } }

@Composable private fun Stat(v: String, label: String) { Column(Modifier.clip(RoundedCornerShape(12.dp)).background(Color.White.copy(.09f)).padding(horizontal = 11.dp, vertical = 7.dp)) { Text(v, color = Color.White, fontWeight = FontWeight.Black); Text(label, color = Color.White.copy(.55f), fontSize = 7.sp) } }

@Composable private fun ActionCard(icon: String, eyebrow: String, title: String, subtitle: String, accent: Color, action: () -> Unit, dark: Boolean = false) { Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp).clickable(onClick = action), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(if (dark) VDeep else Color.White)) { Column(Modifier.padding(17.dp)) { Row(verticalAlignment = Alignment.CenterVertically) { Text(icon, fontSize = 30.sp); Spacer(Modifier.width(11.dp)); Column(Modifier.weight(1f)) { Text(eyebrow, color = accent, fontSize = 10.sp, fontWeight = FontWeight.Black); Text(title, color = if (dark) Color.White else VDeep, fontSize = 19.sp, fontWeight = FontWeight.Black); Text(subtitle, color = if (dark) Color.White.copy(.65f) else VMuted, fontSize = 11.sp) } }; Spacer(Modifier.height(11.dp)); Button(action, Modifier.fillMaxWidth().height(46.dp), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = accent, contentColor = VDeep)) { Text("Başla", fontWeight = FontWeight.Bold) } } } }

@Composable private fun SmallAction(icon: String, title: String, action: () -> Unit) { Card(Modifier.weight(1f).clickable(onClick = action), RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(Color.White)) { Column(Modifier.padding(15.dp)) { Text(icon, fontSize = 25.sp); Text(title, color = VDeep, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 6.dp)); Text("hemen çöz", color = VGreen, fontSize = 10.sp) } } }

@Composable private fun LibraryScreen(onStudy: (Topic) -> Unit) { var search by remember { mutableStateOf("") }; val topics = GeographyData.topics.filter { it.title.contains(search, true) || it.subtitle.contains(search, true) }; LazyColumn(contentPadding = PaddingValues(16.dp, 10.dp, 16.dp, 28.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) { item { Text("Kütüphane", color = VDeep, fontSize = 29.sp, fontWeight = FontWeight.Black); Text("Önce oku → anla → sonra test et.", color = VGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp); Spacer(Modifier.height(8.dp)); OutlinedTextField(search, { search = it }, Modifier.fillMaxWidth(), singleLine = true, label = { Text("Konu ara") }, leadingIcon = { Icon(Icons.Default.Search, null) }) }; item { Card(Modifier.fillMaxWidth(), RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(VDeep)) { Column(Modifier.padding(17.dp)) { Text("ÇALIŞMA KÜTÜPHANESİ", color = VMint, fontSize = 10.sp, fontWeight = FontWeight.Black); Text("12 konu • kısa ve sınav odaklı", color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Black); Text("Her konuyu soruya geçmeden önce anlayabileceğin özetler.", color = Color.White.copy(.68f), fontSize = 11.sp) } } }; item { Text("Konu Bankası", color = VDeep, fontSize = 19.sp, fontWeight = FontWeight.Black) }; items(topics, key = { it.title }) { topic -> TopicRow(topic) { onStudy(topic) } } }
}

@Composable private fun TopicRow(topic: Topic, onClick: () -> Unit) { Card(Modifier.fillMaxWidth().clickable(onClick = onClick), RoundedCornerShape(17.dp), colors = CardDefaults.cardColors(Color.White)) { Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) { Text(topic.icon, fontSize = 25.sp); Spacer(Modifier.width(11.dp)); Column(Modifier.weight(1f)) { Text(topic.title, color = VDeep, fontWeight = FontWeight.ExtraBold); Text(topic.subtitle, color = VMuted, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis); Text("Çalışmayı aç →", color = VGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold) }; Icon(Icons.Default.ChevronRight, null, tint = VGreen) } } }

@Composable private fun EventsScreen(onLaunch: (SharedGameMode) -> Unit) { val modes = SharedGameModes.games + SharedGameModes.arenaModes; LazyColumn(contentPadding = PaddingValues(16.dp, 10.dp, 16.dp, 28.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) { item { Text("Etkinlikler", color = VDeep, fontSize = 29.sp, fontWeight = FontWeight.Black); Text("Öğren, yarış, tekrar et.", color = VGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold) }; items(modes, key = { it.id }) { mode -> Card(Modifier.fillMaxWidth().clickable { onLaunch(mode) }, RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(Color.White)) { Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) { Text(mode.icon, fontSize = 27.sp); Spacer(Modifier.width(11.dp)); Column(Modifier.weight(1f)) { Text(mode.title, color = VDeep, fontWeight = FontWeight.ExtraBold); Text(mode.subtitle, color = VMuted, fontSize = 10.sp); Text("${mode.questions} soru • +${mode.rewardXp} XP", color = if (mode.arena) VGold else VGreen, fontSize = 9.sp, fontWeight = FontWeight.Black) }; Icon(Icons.Default.PlayArrow, null, tint = VGreen) } } } } }

@Composable private fun SettingsScreen(p: SharedPreferences) { LazyColumn(contentPadding = PaddingValues(16.dp, 10.dp, 16.dp, 28.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) { item { Text("Ayarlar", color = VDeep, fontSize = 29.sp, fontWeight = FontWeight.Black); Text("İlerleme bilgilerin cihazında tutulur.", color = VMuted, fontSize = 12.sp) }; item { Setting("Çözülen soru", p.getInt("solved", 0).toString(), Icons.Default.MenuBook) }; item { Setting("Kazanılan XP", p.getInt("xp", 0).toString(), Icons.Default.Star) }; item { Setting("Seri", p.getInt("streak", 0).toString(), Icons.Default.LocalFireDepartment) } } }

@Composable private fun Setting(title: String, value: String, icon: ImageVector) { Card(Modifier.fillMaxWidth(), RoundedCornerShape(17.dp), colors = CardDefaults.cardColors(Color.White)) { Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) { Icon(icon, null, tint = VGreen); Spacer(Modifier.width(11.dp)); Text(title, color = VDeep, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f)); Text(value, color = VGreen, fontWeight = FontWeight.Black) } } }

@Composable private fun V2QuizScreen(title: String, questions: List<Question>, prefs: SharedPreferences, onFinish: () -> Unit) { var index by remember { mutableIntStateOf(0) }; var selected by remember { mutableIntStateOf(-1) }; var correct by remember { mutableIntStateOf(0) }; var wrong by remember { mutableIntStateOf(0) }; var finished by remember { mutableStateOf(false) }; if (finished) { ResultScreen(title, correct, wrong, questions.size - correct - wrong, onFinish); return }; val q = questions[index]; Box(Modifier.fillMaxSize().background(VBg).statusBarsPadding().navigationBarsPadding()) { LazyColumn(contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 28.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) { item { Row(verticalAlignment = Alignment.CenterVertically) { IconButton({ onFinish() }) { Icon(Icons.Default.Close, "Kapat") }; Text(title, color = VDeep, fontSize = 19.sp, fontWeight = FontWeight.Black, modifier = Modifier.weight(1f)); Text("${index + 1}/${questions.size}", color = VGreen, fontWeight = FontWeight.Bold) } }; item { LinearProgressIndicator({ (index + 1f) / questions.size }, Modifier.fillMaxWidth().height(7.dp), color = VGreen, trackColor = VSoft) }; item { Card(Modifier.fillMaxWidth(), RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color.White)) { Column(Modifier.padding(17.dp)) { Text(q.topic, color = VGreen, fontSize = 10.sp, fontWeight = FontWeight.Black); Spacer(Modifier.height(7.dp)); Text(q.text, color = VDeep, fontSize = 19.sp, lineHeight = 26.sp, fontWeight = FontWeight.ExtraBold) } } }; items(q.options.indices.toList()) { i -> val revealed = selected >= 0; val correctAnswer = i == q.correctIndex; val chosen = i == selected; val color = when { !revealed -> VDeep; correctAnswer -> VGreen; chosen -> Color(0xFFE65353); else -> VMuted }; Card(Modifier.fillMaxWidth().clickable(enabled = !revealed) { selected = i; if (correctAnswer) correct++ else wrong++ }, RoundedCornerShape(17.dp), colors = CardDefaults.cardColors(if (revealed && correctAnswer) VSoft else Color.White)) { Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) { Text("${('A'.code + i).toChar()}", color = color, fontWeight = FontWeight.Black); Spacer(Modifier.width(12.dp)); Text(q.options[i], color = VDeep, modifier = Modifier.weight(1f)); if (revealed && correctAnswer) Icon(Icons.Default.CheckCircle, null, tint = VGreen) } } }; item { if (selected >= 0) Button({ if (index == questions.lastIndex) finished = true else { index++; selected = -1 } }, Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = VDeep)) { Text(if (index == questions.lastIndex) "Sonucu Gör" else "Sonraki Soru") } } } } }

@Composable private fun ResultScreen(title: String, correct: Int, wrong: Int, blank: Int, onFinish: () -> Unit) { Box(Modifier.fillMaxSize().background(VBg).statusBarsPadding().navigationBarsPadding(), contentAlignment = Alignment.Center) { Card(Modifier.fillMaxWidth().padding(24.dp), RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(Color.White)) { Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text("TEST TAMAMLANDI", color = VGreen, fontWeight = FontWeight.Black); Text(title, color = VDeep, fontSize = 23.sp, fontWeight = FontWeight.Black); Spacer(Modifier.height(15.dp)); Text("$correct doğru", color = VGreen, fontSize = 21.sp, fontWeight = FontWeight.Bold); Text("$wrong yanlış • $blank boş", color = VMuted); Spacer(Modifier.height(20.dp)); Button(onFinish, Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(VDeep)) { Text("Ana Sayfaya Dön") } } } } }
