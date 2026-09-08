package tr.yurdunubil.app

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private val Deep = Color(0xFF06271F)
private val Green = Color(0xFF16B87A)
private val Mint = Color(0xFFDDF9EA)
private val Gold = Color(0xFFFFC857)
private val Red = Color(0xFFE45454)
private val Bg = Color(0xFFF3F8F5)

@Composable
fun NextGenerationApp() {
    val context = LocalContext.current
    val store = remember { AppProgressStore(context) }
    val state by store.snapshot.collectAsStateWithLifecycle(AppProgressStore.Snapshot())
    var tab by remember { mutableIntStateOf(0) }
    var quiz by remember { mutableStateOf<List<Question>?>(null) }
    var quizTitle by remember { mutableStateOf("") }
    var plannerOpen by remember { mutableStateOf(false) }
    var settingsOpen by remember { mutableStateOf(false) }

    fun start(title: String, questions: List<Question>) {
        val safe = questions.filter { it.text.isNotBlank() && it.options.size >= 2 && it.correctIndex in it.options.indices }
            .distinctBy { it.id }
        if (safe.isNotEmpty()) { quizTitle = title; quiz = safe }
    }

    BackHandler(enabled = quiz != null || plannerOpen || settingsOpen || tab != 0) {
        when {
            quiz != null -> quiz = null
            plannerOpen -> plannerOpen = false
            settingsOpen -> settingsOpen = false
            tab != 0 -> tab = 0
        }
    }

    if (quiz != null) {
        QuizScreen(quizTitle, quiz!!, store) { quiz = null }
        return
    }
    if (plannerOpen) {
        StudyPlannerScreen(state, ::start) { plannerOpen = false }
        return
    }
    if (settingsOpen) {
        SettingsScreen(context) { settingsOpen = false }
        return
    }

    val labels = listOf("Ana Sayfa", "Kütüphane", "Harita", "Arena", "Profil")
    val icons = listOf(Icons.Default.Home, Icons.Default.MenuBook, Icons.Default.Map, Icons.Default.SportsEsports, Icons.Default.Person)
    Scaffold(containerColor = Bg, bottomBar = {
        NavigationBar(containerColor = Color.White) {
            labels.forEachIndexed { index, label ->
                NavigationBarItem(selected = tab == index, onClick = { tab = index }, icon = { Icon(icons[index], contentDescription = label) }, label = { Text(label, fontSize = 10.sp) })
            }
        }
    }) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (tab) {
                0 -> PremiumHomeScreen(state, ::start, onPlanner = { plannerOpen = true })
                1 -> LibraryScreen(::start)
                2 -> AtlasScreen(::start)
                3 -> ArenaScreen(::start)
                else -> ProfileScreen(state, ::start, onSettings = { settingsOpen = true })
            }
        }
    }
}

@Composable
private fun LibraryScreen(start: (String, List<Question>) -> Unit) {
    var query by remember { mutableStateOf("") }
    val topics = GeographyData.topics.filter { it.title.contains(query, true) || it.subtitle.contains(query, true) }
    LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text("Kütüphane", color = Deep, fontSize = 31.sp, fontWeight = FontWeight.Black)
            Text("Oku • bağlantı kur • test et", color = Green, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(query, { query = it }, Modifier.fillMaxWidth(), singleLine = true, leadingIcon = { Icon(Icons.Default.Search, null) }, placeholder = { Text("Dağ, ova, maden, nüfus…") })
        }
        item { Panel(Deep) { Text("🇹🇷 TÜRKİYE COĞRAFYA", color = Gold, fontWeight = FontWeight.Black); Text("${topics.size} konu", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black); Text("Konuya gir, sonra mini testi çöz.", color = Mint) } }
        items(topics, key = { it.title }) { topic ->
            Panel(Color.White, onClick = { start(topic.title, SharedQuestionPool.all.filter { it.topic == topic.title }.shuffled().take(10)) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(topic.icon, fontSize = 27.sp); Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) {
                        Text(topic.title, color = Deep, fontWeight = FontWeight.Black, fontSize = 17.sp)
                        Text(topic.subtitle, color = Color.Gray, fontSize = 12.sp)
                        Text("${topic.lessons.size} alt başlık • %${topic.progress}", color = Green, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }; Icon(Icons.Default.ArrowForward, null, tint = Green)
                }
            }
        }
    }
}

@Composable
private fun AtlasScreen(start: (String, List<Question>) -> Unit) {
    val regions = listOf("Tümü", "Marmara", "Ege", "Akdeniz", "İç Anadolu", "Karadeniz", "Doğu Anadolu", "Güneydoğu Anadolu")
    var region by remember { mutableStateOf("Tümü") }
    val provinces = GeographyData.provinces.filter { region == "Tümü" || it.region == region }
    LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text("Türkiye Atlası", color = Deep, fontSize = 31.sp, fontWeight = FontWeight.Black)
            Text("İl → bölge → özellik → soru", color = Green, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                regions.forEach { item -> FilterChip(selected = region == item, onClick = { region = item }, label = { Text(item) }) }
            }
        }
        item { Panel(Deep) { Text("🗺️ ${provinces.size} il", color = Gold, fontSize = 24.sp, fontWeight = FontWeight.Black); Text("İl kartını açıp mini teste geç.", color = Mint) } }
        items(provinces, key = { it.name }) { province ->
            Panel(Color.White, onClick = { start("${province.name} Mini Test", provinceQuestions(province)) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📍", fontSize = 24.sp); Spacer(Modifier.width(10.dp)); Column(Modifier.weight(1f)) {
                        Text(province.name, color = Deep, fontWeight = FontWeight.Black)
                        Text(province.region, color = Green, fontSize = 11.sp)
                        Text(province.clue, color = Color.Gray, fontSize = 11.sp)
                    }; Icon(Icons.Default.PlayArrow, null, tint = Green)
                }
            }
        }
    }
}

private fun provinceQuestions(p: Province): List<Question> {
    val others = GeographyData.provinces.filter { it.name != p.name }
    fun options(correct: String, values: List<String>): List<String> = (listOf(correct) + values.filter { it != correct }.shuffled().take(4)).shuffled()
    val regionOptions = options(p.region, others.map { it.region }.distinct())
    val clueOptions = options(p.clue, others.map { it.clue }.filter { it.isNotBlank() })
    val facts = p.facts.ifEmpty { listOf("Bölge: ${p.region}") }
    val factOptions = facts.map { fact -> options(fact, others.flatMap { it.facts }) }
    val list = mutableListOf<Question>()
    list += Question(700000 + p.name.hashCode(), "Coğrafya", "İller", "${p.name} hangi coğrafi bölgede yer alır?", regionOptions, regionOptions.indexOf(p.region), "${p.name}, ${p.region} Bölgesi'ndedir.")
    list += Question(700100 + p.name.hashCode(), "Coğrafya", "İller", "Aşağıdakilerden hangisi ${p.name} için verilen temel coğrafi ipucudur?", clueOptions, clueOptions.indexOf(p.clue), "İl kartındaki ipucu: ${p.clue}")
    facts.take(3).forEachIndexed { index, fact ->
        val opts = factOptions[index]
        list += Question(700200 + p.name.hashCode() + index, "Coğrafya", "İller", "${p.name} ile ilgili aşağıdaki bilgilerden hangisi doğrudur?", opts, opts.indexOf(fact), "${p.name}: $fact")
    }
    return list.distinctBy { it.id }.filter { it.options.size >= 2 }
}

@Composable
private fun ArenaScreen(start: (String, List<Question>) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("Arena", color = Deep, fontSize = 31.sp, fontWeight = FontWeight.Black); Text("Hız • bilgi • seri", color = Green, fontWeight = FontWeight.Bold) }
        item { Panel(Deep) { Text("🏆 SEZON 1", color = Gold, fontWeight = FontWeight.Black); Text("Türkiye Ustası", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Black); Text("Çevrim içi rakip sistemi için gerçek sunucu doğrulaması kullanılacak.", color = Mint) } }
        GeographyData.games.forEach { game -> item { Panel(Color.White, onClick = { start(game.title, SharedQuestionPool.all.shuffled().take(10)) }) { Row(verticalAlignment = Alignment.CenterVertically) { Text(game.icon, fontSize = 27.sp); Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(game.title, color = Deep, fontWeight = FontWeight.Black, fontSize = 17.sp); Text(game.subtitle, color = Color.Gray, fontSize = 12.sp) }; Icon(Icons.Default.PlayArrow, null, tint = Green) } } } }
        GeographyData.arena.forEach { mode -> item { Panel(Deep, onClick = { start(mode.title, SharedQuestionPool.all.shuffled().take(10)) }) { Row(verticalAlignment = Alignment.CenterVertically) { Text(mode.icon, fontSize = 27.sp); Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(mode.title, color = Color.White, fontWeight = FontWeight.Black); Text(mode.subtitle, color = Mint, fontSize = 12.sp) }; Text(mode.reward, color = Gold, fontWeight = FontWeight.Black) } } } }
    }
}

@Composable
private fun ProfileScreen(state: AppProgressStore.Snapshot, start: (String, List<Question>) -> Unit, onSettings: () -> Unit) {
    val accuracy = if (state.solved == 0) 0 else (state.correct * 100f / state.solved).roundToInt()
    val wrong = SharedQuestionPool.all.filter { it.id in state.wrongIds }
    val level = 1 + state.xp / 500
    LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Profil & Gelişim", color = Deep, fontSize = 31.sp, fontWeight = FontWeight.Black); Text("Seviye $level • %$accuracy doğruluk", color = Green, fontWeight = FontWeight.Bold) }
        item { Panel(Deep) { Text("${state.xp} XP", color = Color.White, fontSize = 29.sp, fontWeight = FontWeight.Black); Text("🔥 ${state.streak} günlük seri", color = Gold, fontWeight = FontWeight.Bold); Text("Bugün ${state.todaySolved} soru • ${state.solved} toplam soru", color = Mint) } }
        item {
            Panel(Color.White) {
                Text("📈 KONU ANALİZİ", color = Green, fontWeight = FontWeight.Black)
                val insights = buildStudyInsights(state)
                if (insights.isEmpty()) Text("Henüz yeterli veri yok. Birkaç soru çözünce burada hangi konularda güçlendiğini göreceksin.", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(top = 7.dp))
                else insights.take(5).forEach { insight -> Text("${insight.topic}: %${insight.score} • ${insight.detail}", color = Deep, fontSize = 12.sp, modifier = Modifier.padding(top = 7.dp)) }
            }
        }
        item { Panel(Color.White, onClick = { if (wrong.isNotEmpty()) start("Yanlışlarım", wrong.take(20)) }) { Text("🔁 Yanlış soru deposu", color = Deep, fontWeight = FontWeight.Black); Text("${wrong.size} soru", color = if (wrong.isEmpty()) Color.Gray else Red, fontSize = 20.sp, fontWeight = FontWeight.Black); Text(if (wrong.isEmpty()) "Yanlış yaptığın sorular burada birikecek." else "Dokun ve tekrar çöz.", color = Color.Gray, fontSize = 12.sp) } }
        item { Panel(Color.White, onClick = onSettings) { Row(verticalAlignment = Alignment.CenterVertically) { Text("⚙️", fontSize = 24.sp); Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text("Ayarlar", color = Deep, fontWeight = FontWeight.Black, fontSize = 17.sp); Text("Bildirimler, ses ve uygulama tercihleri", color = Color.Gray, fontSize = 12.sp) }; Icon(Icons.Default.ArrowForward, null, tint = Green) } } }
    }
}

@Composable
private fun SettingsScreen(context: android.content.Context, close: () -> Unit) {
    val prefs = remember { context.getSharedPreferences("yurdunu_bil_settings", 0) }
    var notifications by remember { mutableStateOf(prefs.getBoolean("notifications", true)) }
    var sounds by remember { mutableStateOf(prefs.getBoolean("sounds", true)) }
    LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Row(verticalAlignment = Alignment.CenterVertically) { IconButton(close) { Icon(Icons.Default.ArrowBack, "Geri") }; Column { Text("Ayarlar", color = Deep, fontSize = 30.sp, fontWeight = FontWeight.Black); Text("Yurdunu Bil'i kendine göre ayarla", color = Green, fontWeight = FontWeight.Bold) } } }
        item { Panel(Deep) { Text("YURDUNU BİL", color = Gold, fontWeight = FontWeight.Black); Text("KPSS Önlisans • 2026 Coğrafya", color = Color.White); Text("Çalış, yanlışını gör, tekrar et ve ilerle.", color = Mint, fontSize = 12.sp) } }
        item { SettingRow("🔔", "Çalışma hatırlatmaları", "Günün çalışma rutinini destekler", notifications) { notifications = it; prefs.edit().putBoolean("notifications", it).apply() } }
        item { SettingRow("🔊", "Soru sesleri", "Doğru/yanlış geri bildirimleri", sounds) { sounds = it; prefs.edit().putBoolean("sounds", it).apply() } }
        item { Panel(Color.White) { Text("🧭 Kullanım", color = Green, fontWeight = FontWeight.Black); Text("Sorularda doğru cevap yeşil, seçtiğin yanlış cevap kırmızı gösterilir. Her sorunun altında kısa bir Dikkat Köşesi bulunur.", color = Deep, fontSize = 13.sp) } }
        item { Panel(Color.White, onClick = close) { Text("← Profil'e dön", color = Green, fontWeight = FontWeight.Black) } }
    }
}

@Composable
private fun SettingRow(icon: String, title: String, subtitle: String, checked: Boolean, onChecked: (Boolean) -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(18.dp)) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(icon, fontSize = 24.sp); Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(title, color = Deep, fontWeight = FontWeight.Black); Text(subtitle, color = Color.Gray, fontSize = 11.sp) }; Switch(checked = checked, onCheckedChange = onChecked)
        }
    }
}

@Composable
private fun QuizScreen(title: String, questions: List<Question>, store: AppProgressStore, done: () -> Unit) {
    var index by remember { mutableIntStateOf(0) }
    var selected by remember { mutableIntStateOf(-1) }
    var answered by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val question = questions[index]
    val isCorrect = answered && selected == question.correctIndex
    BackHandler { done() }
    Column(Modifier.fillMaxSize().background(Bg).padding(18.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(done) { Icon(Icons.Default.Close, contentDescription = "Kapat") }
            Column(Modifier.weight(1f)) { Text(title, color = Deep, fontWeight = FontWeight.Black); Text("${index + 1}/${questions.size}", color = Green, fontWeight = FontWeight.Bold) }
        }
        LinearProgressIndicator(progress = { (index + 1f) / questions.size }, modifier = Modifier.fillMaxWidth().height(6.dp), color = Green, trackColor = Mint)
        Text(question.topic, color = Green, fontWeight = FontWeight.Black, modifier = Modifier.padding(top = 14.dp))
        Text(question.text, color = Deep, fontSize = 21.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(vertical = 16.dp))
        question.options.forEachIndexed { optionIndex, option ->
            val optionColor = when { !answered -> Color.White; optionIndex == question.correctIndex -> Mint; optionIndex == selected -> Color(0xFFFFE1E1); else -> Color.White }
            val borderColor = when { !answered -> Color.Transparent; optionIndex == question.correctIndex -> Green; optionIndex == selected -> Red; else -> Color.Transparent }
            Card(colors = CardDefaults.cardColors(containerColor = optionColor), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).border(2.dp, borderColor, RoundedCornerShape(16.dp)).clickable(enabled = !answered) { selected = optionIndex; answered = true; scope.launch { store.record(question, optionIndex == question.correctIndex) } }) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Text("${('A'.code + optionIndex).toChar()}", color = if (answered && optionIndex == selected) borderColor else Deep, fontWeight = FontWeight.Black); Spacer(Modifier.width(12.dp)); Text(option, color = Deep, fontWeight = if (answered && (optionIndex == question.correctIndex || optionIndex == selected)) FontWeight.Bold else FontWeight.Normal) }
            }
        }
        if (answered) {
            Spacer(Modifier.height(12.dp))
            Panel(if (isCorrect) Mint else Color(0xFFFFEEEE)) {
                Text(if (isCorrect) "✓ DOĞRU! +10 XP" else "✕ YANLIŞ", color = if (isCorrect) Green else Red, fontWeight = FontWeight.Black, fontSize = 15.sp)
                Text(if (isCorrect) "Gayet iyi, devam!" else "Doğrusunu birlikte netleştirelim.", color = Deep, fontWeight = FontWeight.Bold)
                Text("Dikkat Köşesi", color = Deep, fontWeight = FontWeight.Black, modifier = Modifier.padding(top = 5.dp))
                Text(question.explanation, color = Color.Gray, fontSize = 13.sp)
            }
            Spacer(Modifier.weight(1f))
            Button(onClick = { if (index + 1 < questions.size) { index++; selected = -1; answered = false } else done() }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Green)) { Text(if (index + 1 < questions.size) "Sonraki Soru" else "Testi Bitir", fontWeight = FontWeight.ExtraBold) }
        }
    }
}

@Composable
private fun HeaderCard(content: @Composable ColumnScope.() -> Unit) { Box(Modifier.fillMaxWidth().background(Deep, RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)).padding(22.dp)) { Column(content = content) } }
@Composable
private fun Panel(color: Color, onClick: (() -> Unit)? = null, content: @Composable ColumnScope.() -> Unit) { val modifier = Modifier.fillMaxWidth().background(color, RoundedCornerShape(18.dp)).then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier).padding(17.dp); Box(modifier) { Column(content = content) } }
@Composable
private fun ActionCard(icon: String, title: String, subtitle: String, accent: Color, modifier: Modifier, onClick: () -> Unit) { Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(20.dp), modifier = modifier.clickable(onClick = onClick)) { Column(Modifier.padding(15.dp)) { Text(icon, fontSize = 25.sp); Text(title, color = Deep, fontWeight = FontWeight.Black); Text(subtitle, color = accent, fontSize = 11.sp, fontWeight = FontWeight.Bold) } } }
@Composable
private fun SectionTitle(title: String, subtitle: String) { Column(Modifier.padding(horizontal = 18.dp)) { Text(title, color = Deep, fontSize = 19.sp, fontWeight = FontWeight.Black); Text(subtitle, color = Color.Gray, fontSize = 12.sp) } }
@Composable
private fun Pill(text: String) { Surface(color = Color.White.copy(alpha = .15f), shape = RoundedCornerShape(30.dp)) { Text(text, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) } }
