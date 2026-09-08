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

private val BG = Color(0xFFF3F8F5)
private val DEEP = Color(0xFF06221B)
private val DEEP2 = Color(0xFF0B342B)
private val GREEN = Color(0xFF18C986)
private val MINT = Color(0xFFC9F8E1)
private val GOLD = Color(0xFFFFC857)
private val MUTED = Color(0xFF70847B)
private val SOFT = Color(0xFFE4F7ED)
private val RED = Color(0xFFE65353)

@Composable
fun YurdunuBilMainV2App() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = remember { context.getSharedPreferences("yurdunu_bil_native", 0) }
    var tab by remember { mutableIntStateOf(0) }
    var study by remember { mutableStateOf<Topic?>(null) }
    var province by remember { mutableStateOf<Province?>(null) }
    var quiz by remember { mutableStateOf<List<Question>?>(null) }
    var quizTitle by remember { mutableStateOf("") }

    fun startQuiz(title: String, mode: SharedGameMode) {
        val picked = SharedQuestionPool.pick(mode)
        if (picked.isNotEmpty()) { quizTitle = title; quiz = picked }
    }

    if (study != null) {
        val selected = study!!
        LibraryStudyScreen(selected, { study = null }) {
            study = null
            startQuiz(selected.title, SharedGameMode("study-${selected.title}", selected.title, selected.subtitle, selected.icon, 10, 180, 100, SharedQuestionPool.topicForLibrary(selected.title)))
        }
        return
    }
    if (province != null) {
        ProvinceDetailScreen(province!!, onBack = { province = null })
        return
    }
    if (quiz != null) {
        V2QuizScreen(quizTitle, quiz!!, prefs) { quiz = null }
        return
    }

    Scaffold(
        containerColor = BG,
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                val nav = listOf(
                    "Ana Sayfa" to Icons.Default.Home,
                    "Kütüphane" to Icons.Default.MenuBook,
                    "Etkinlikler" to Icons.Default.SportsEsports,
                    "Ayarlar" to Icons.Default.Settings
                )
                nav.forEachIndexed { i, item ->
                    NavigationBarItem(
                        selected = tab == i,
                        onClick = { tab = i },
                        icon = { Icon(item.second, contentDescription = item.first) },
                        label = { Text(item.first, fontSize = 10.sp) }
                    )
                }
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (tab) {
                0 -> HomeScreen(prefs, { startQuiz("Hızlı 10", SharedGameModes.quick) }, { tab = 2 })
                1 -> LibraryScreen({ study = it }, { province = it })
                2 -> EventsScreen { startQuiz(it.title, it) }
                else -> SettingsScreen(prefs)
            }
        }
    }
}

@Composable
private fun HomeScreen(prefs: SharedPreferences, quick: () -> Unit, events: () -> Unit) {
    val solved = prefs.getInt("solved", 0)
    val correct = prefs.getInt("correct", 0)
    val xp = prefs.getInt("xp", 0)
    val streak = prefs.getInt("streak", 0)
    val accuracy = if (solved == 0) 0 else (correct * 100f / solved).roundToInt()
    LazyColumn(contentPadding = PaddingValues(bottom = 28.dp), verticalArrangement = Arrangement.spacedBy(13.dp)) {
        item { HeaderCard(xp, solved, streak) }
        item { ActionCard("🎯", "BUGÜNÜN GÖREVİ", SharedQuestionPool.dailyMode().title, "Kısa bir coğrafya turuyla ritmini koru.", GREEN, quick) }
        item {
            Row(Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SmallAction("⚡", "Hızlı 10", quick)
                SmallAction("🧠", "Bilgi Zinciri") { events() }
            }
        }
        item { ActionCard("⚔️", "ARENA", "Bilgini sahaya çıkar.", "1v1 • hız • bölge • Türkiye Ustası", GOLD, events, dark = true) }
        item {
            Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(20.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("GELİŞİMİN", color = GREEN, fontSize = 10.sp, fontWeight = FontWeight.Black)
                    Text("%$accuracy genel doğruluk", color = DEEP, fontSize = 19.sp, fontWeight = FontWeight.Black)
                    Text(if (solved == 0) "İlk testini çöz ve ilerlemeni başlat." else "$correct doğru cevapla devam ediyorsun.", color = MUTED, fontSize = 11.sp)
                    Spacer(Modifier.height(10.dp))
                    LinearProgressIndicator(progress = { accuracy / 100f }, modifier = Modifier.fillMaxWidth().height(7.dp).clip(RoundedCornerShape(8.dp)), color = GREEN, trackColor = SOFT)
                }
            }
        }
    }
}

@Composable
private fun HeaderCard(xp: Int, solved: Int, streak: Int) {
    Box(Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(DEEP, DEEP2, Color(0xFF11634A))), RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)).padding(21.dp)) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Yurdunu Bil", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black)
                    Text("KPSS Önlisans • Türkiye Coğrafyası", color = MINT, fontSize = 11.sp)
                }
                Text("Lv.${1 + xp / 500}", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(17.dp))
            Text("Bugün Türkiye'yi biraz daha çöz. ✨", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(13.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { Stat("$solved", "SORU"); Stat("$xp", "XP"); Stat("$streak", "SERİ") }
        }
    }
}

@Composable private fun Stat(value: String, label: String) {
    Column(Modifier.clip(RoundedCornerShape(12.dp)).background(Color.White.copy(alpha = .09f)).padding(horizontal = 11.dp, vertical = 7.dp)) {
        Text(value, color = Color.White, fontWeight = FontWeight.Black)
        Text(label, color = Color.White.copy(alpha = .55f), fontSize = 7.sp)
    }
}

@Composable
private fun ActionCard(icon: String, eyebrow: String, title: String, subtitle: String, accent: Color, action: () -> Unit, dark: Boolean = false) {
    Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp).clickable(onClick = action), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = if (dark) DEEP else Color.White)) {
        Column(Modifier.padding(17.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(icon, fontSize = 30.sp); Spacer(Modifier.width(11.dp))
                Column(Modifier.weight(1f)) {
                    Text(eyebrow, color = accent, fontSize = 10.sp, fontWeight = FontWeight.Black)
                    Text(title, color = if (dark) Color.White else DEEP, fontSize = 19.sp, fontWeight = FontWeight.Black)
                    Text(subtitle, color = if (dark) Color.White.copy(alpha = .65f) else MUTED, fontSize = 11.sp)
                }
            }
            Spacer(Modifier.height(11.dp))
            Button(onClick = action, modifier = Modifier.fillMaxWidth().height(46.dp), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = accent, contentColor = DEEP)) { Text("Başla", fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
private fun SmallAction(icon: String, title: String, action: () -> Unit) {
    Card(Modifier.weight(1f).clickable(onClick = action), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.padding(15.dp)) { Text(icon, fontSize = 25.sp); Text(title, color = DEEP, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 6.dp)); Text("hemen çöz", color = GREEN, fontSize = 10.sp) }
    }
}

@Composable
private fun LibraryScreen(onStudy: (Topic) -> Unit, onProvince: (Province) -> Unit) {
    var search by remember { mutableStateOf("") }
    val topics = GeographyData.topics.filter { it.title.contains(search, true) || it.subtitle.contains(search, true) }
    LazyColumn(contentPadding = PaddingValues(16.dp, 10.dp, 16.dp, 28.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text("Kütüphane", color = DEEP, fontSize = 29.sp, fontWeight = FontWeight.Black)
            Text("Önce oku → anla → sonra test et.", color = GREEN, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = search, onValueChange = { search = it }, modifier = Modifier.fillMaxWidth(), singleLine = true, label = { Text("Konu ara") }, leadingIcon = { Icon(Icons.Default.Search, null) })
        }
        item {
            Card(Modifier.fillMaxWidth(), RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = DEEP)) {
                Column(Modifier.padding(17.dp)) {
                    Text("ÇALIŞMA KÜTÜPHANESİ", color = MINT, fontSize = 10.sp, fontWeight = FontWeight.Black)
                    Text("12 konu • sınav odaklı çalışma", color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Black)
                    Text("Her konuyu soruya geçmeden önce anlayabileceğin özetler ve KPSS ipuçları.", color = Color.White.copy(alpha = .68f), fontSize = 11.sp)
                }
            }
        }
        item { Text("Konu Bankası", color = DEEP, fontSize = 19.sp, fontWeight = FontWeight.Black) }
        items(topics, key = { it.title }) { topic -> TopicRow(topic) { onStudy(topic) } }
        item {
            Spacer(Modifier.height(8.dp))
            Text("İl Keşfi", color = DEEP, fontSize = 19.sp, fontWeight = FontWeight.Black)
            Text("Detaylandırılmış il kartlarından coğrafi bağlantıları öğren.", color = MUTED, fontSize = 11.sp)
        }
        items(GeographyData.provinces, key = { it.name }) { city -> ProvinceRow(city) { onProvince(city) } }
    }
}

@Composable private fun TopicRow(topic: Topic, onClick: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable(onClick = onClick), RoundedCornerShape(17.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(topic.icon, fontSize = 25.sp); Spacer(Modifier.width(11.dp))
            Column(Modifier.weight(1f)) {
                Text(topic.title, color = DEEP, fontWeight = FontWeight.ExtraBold)
                Text(topic.subtitle, color = MUTED, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("Çalışmayı aç →", color = GREEN, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            Icon(Icons.Default.ChevronRight, null, tint = GREEN)
        }
    }
}

@Composable private fun ProvinceRow(city: Province, onClick: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable(onClick = onClick), RoundedCornerShape(17.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("📍", fontSize = 23.sp); Spacer(Modifier.width(11.dp))
            Column(Modifier.weight(1f)) { Text(city.name, color = DEEP, fontWeight = FontWeight.ExtraBold); Text(city.region, color = GREEN, fontSize = 10.sp, fontWeight = FontWeight.Bold); Text(city.clue, color = MUTED, fontSize = 10.sp) }
            Icon(Icons.Default.ChevronRight, null, tint = GREEN)
        }
    }
}

@Composable
private fun ProvinceDetailScreen(city: Province, onBack: () -> Unit) {
    LazyColumn(contentPadding = PaddingValues(16.dp, 12.dp, 16.dp, 30.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Row(verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Geri") }; Column { Text(city.name, color = DEEP, fontSize = 26.sp, fontWeight = FontWeight.Black); Text(city.region, color = GREEN, fontWeight = FontWeight.Bold) } } }
        item { Card(Modifier.fillMaxWidth(), RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = DEEP)) { Column(Modifier.padding(18.dp)) { Text("COĞRAFYA KARTI", color = MINT, fontSize = 10.sp, fontWeight = FontWeight.Black); Spacer(Modifier.height(6.dp)); Text(city.clue, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black) } } }
        item { Text("Bilmen Gerekenler", color = DEEP, fontSize = 19.sp, fontWeight = FontWeight.Black) }
        items(city.facts) { fact -> Card(Modifier.fillMaxWidth(), RoundedCornerShape(15.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) { Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) { Text("✓", color = GREEN, fontWeight = FontWeight.Black); Spacer(Modifier.width(10.dp)); Text(fact, color = DEEP, fontSize = 13.sp) } } }
    }
}

@Composable
private fun EventsScreen(onLaunch: (SharedGameMode) -> Unit) {
    val modes = SharedGameModes.games + SharedGameModes.arenaModes
    LazyColumn(contentPadding = PaddingValues(16.dp, 10.dp, 16.dp, 28.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("Etkinlikler", color = DEEP, fontSize = 29.sp, fontWeight = FontWeight.Black); Text("Öğren, yarış, tekrar et.", color = GREEN, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
        items(modes, key = { it.id }) { mode ->
            Card(Modifier.fillMaxWidth().clickable { onLaunch(mode) }, RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(mode.icon, fontSize = 27.sp); Spacer(Modifier.width(11.dp))
                    Column(Modifier.weight(1f)) { Text(mode.title, color = DEEP, fontWeight = FontWeight.ExtraBold); Text(mode.subtitle, color = MUTED, fontSize = 10.sp); Text("${mode.questions} soru • +${mode.rewardXp} XP", color = if (mode.arena) GOLD else GREEN, fontSize = 9.sp, fontWeight = FontWeight.Black) }
                    Icon(Icons.Default.PlayArrow, null, tint = GREEN)
                }
            }
        }
    }
}

@Composable
private fun SettingsScreen(prefs: SharedPreferences) {
    LazyColumn(contentPadding = PaddingValues(16.dp, 10.dp, 16.dp, 28.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("Ayarlar", color = DEEP, fontSize = 29.sp, fontWeight = FontWeight.Black); Text("İlerleme bilgilerin bu cihazda tutulur.", color = MUTED, fontSize = 12.sp) }
        item { SettingRow("Çözülen soru", prefs.getInt("solved", 0).toString(), Icons.Default.MenuBook) }
        item { SettingRow("Kazanılan XP", prefs.getInt("xp", 0).toString(), Icons.Default.Star) }
        item { SettingRow("Doğru cevap", prefs.getInt("correct", 0).toString(), Icons.Default.CheckCircle) }
        item { SettingRow("Yanlış cevap", prefs.getInt("wrong", 0).toString(), Icons.Default.Close) }
        item { SettingRow("Seri", prefs.getInt("streak", 0).toString(), Icons.Default.LocalFireDepartment) }
        item { Card(Modifier.fillMaxWidth(), RoundedCornerShape(17.dp), colors = CardDefaults.cardColors(containerColor = SOFT)) { Text("İpucu: Kütüphanede önce konuyu çalış, ardından aynı konunun testini çöz. Böylece kalıcı tekrar döngüsü oluşturursun.", color = DEEP, fontSize = 12.sp, modifier = Modifier.padding(16.dp)) } }
    }
}

@Composable private fun SettingRow(title: String, value: String, icon: ImageVector) {
    Card(Modifier.fillMaxWidth(), RoundedCornerShape(17.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) { Icon(icon, null, tint = GREEN); Spacer(Modifier.width(11.dp)); Text(title, color = DEEP, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f)); Text(value, color = GREEN, fontWeight = FontWeight.Black) }
    }
}

@Composable
private fun V2QuizScreen(title: String, questions: List<Question>, prefs: SharedPreferences, onFinish: () -> Unit) {
    var index by remember { mutableIntStateOf(0) }
    var selected by remember { mutableIntStateOf(-1) }
    var correct by remember { mutableIntStateOf(0) }
    var wrong by remember { mutableIntStateOf(0) }
    var finished by remember { mutableStateOf(false) }

    if (finished) {
        val blank = questions.size - correct - wrong
        ResultScreen(title, correct, wrong, blank, correct * 10, onFinish)
        return
    }

    val q = questions[index]
    val revealed = selected >= 0
    Box(Modifier.fillMaxSize().background(BG)) {
        LazyColumn(contentPadding = PaddingValues(16.dp, 10.dp, 16.dp, 30.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onFinish) { Icon(Icons.Default.Close, "Kapat") }
                    Text(title, color = DEEP, fontSize = 19.sp, fontWeight = FontWeight.Black, modifier = Modifier.weight(1f))
                    Text("${index + 1}/${questions.size}", color = GREEN, fontWeight = FontWeight.Bold)
                }
            }
            item { LinearProgressIndicator(progress = { (index + 1f) / questions.size }, modifier = Modifier.fillMaxWidth().height(7.dp), color = GREEN, trackColor = SOFT) }
            item { Card(Modifier.fillMaxWidth(), RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) { Column(Modifier.padding(17.dp)) { Text(q.topic, color = GREEN, fontSize = 10.sp, fontWeight = FontWeight.Black); Spacer(Modifier.height(7.dp)); Text(q.text, color = DEEP, fontSize = 18.sp, lineHeight = 25.sp, fontWeight = FontWeight.ExtraBold) } } }
            items(q.options.indices.toList()) { optionIndex ->
                val isCorrect = optionIndex == q.correctIndex
                val isChosen = optionIndex == selected
                val optionBg = when { revealed && isCorrect -> SOFT; revealed && isChosen -> Color(0xFFFFE8E8); else -> Color.White }
                val optionTint = when { revealed && isCorrect -> GREEN; revealed && isChosen -> RED; else -> DEEP }
                Card(Modifier.fillMaxWidth().clickable(enabled = !revealed) { selected = optionIndex; if (isCorrect) correct++ else wrong++ }, RoundedCornerShape(17.dp), colors = CardDefaults.cardColors(containerColor = optionBg)) {
                    Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(('A'.code + optionIndex).toChar().toString(), color = optionTint, fontWeight = FontWeight.Black)
                        Spacer(Modifier.width(12.dp)); Text(q.options[optionIndex], color = DEEP, modifier = Modifier.weight(1f))
                        if (revealed && isCorrect) Icon(Icons.Default.CheckCircle, null, tint = GREEN)
                        else if (revealed && isChosen) Icon(Icons.Default.Cancel, null, tint = RED)
                    }
                }
            }
            if (revealed) {
                item { Card(Modifier.fillMaxWidth(), RoundedCornerShape(17.dp), colors = CardDefaults.cardColors(containerColor = SOFT)) { Column(Modifier.padding(15.dp)) { Text("💡 DİKKAT KÖŞESİ", color = GREEN, fontWeight = FontWeight.Black, fontSize = 10.sp); Spacer(Modifier.height(5.dp)); Text(q.explanation, color = DEEP, fontSize = 12.sp, lineHeight = 18.sp) } } }
                item {
                    Button(onClick = { if (index == questions.lastIndex) finished = true else { index++; selected = -1 } }, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = DEEP)) {
                        Text(if (index == questions.lastIndex) "Sonucu Gör" else "Sonraki Soru", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ResultScreen(title: String, correct: Int, wrong: Int, blank: Int, xp: Int, onExit: () -> Unit) {
    val total = correct + wrong + blank
    LaunchedEffect(total) { }
    Column(Modifier.fillMaxSize().background(BG).statusBarsPadding().navigationBarsPadding().padding(22.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text("TEST TAMAMLANDI", color = GREEN, fontSize = 11.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(8.dp)); Text(title, color = DEEP, fontSize = 28.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(22.dp))
        Card(Modifier.fillMaxWidth(), RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = DEEP)) {
            Column(Modifier.padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$correct / $total", color = Color.White, fontSize = 38.sp, fontWeight = FontWeight.Black)
                Text("Doğru cevap", color = MINT, fontSize = 12.sp)
                Spacer(Modifier.height(16.dp)); Text("✓ $correct doğru   •   ✕ $wrong yanlış   •   – $blank boş", color = Color.White.copy(alpha = .75f), fontSize = 11.sp)
                Text("+$xp XP", color = GOLD, fontSize = 18.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(top = 10.dp))
            }
        }
        Spacer(Modifier.height(18.dp)); Button(onClick = onExit, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = GREEN, contentColor = DEEP)) { Text("Ana Ekrana Dön", fontWeight = FontWeight.Black) }
    }
}
