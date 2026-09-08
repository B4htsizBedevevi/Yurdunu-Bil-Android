package tr.yurdunubil.app

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

    fun start(title: String, questions: List<Question>) {
        val safe = questions.filter { it.text.isNotBlank() && it.options.size >= 2 && it.correctIndex in it.options.indices }
            .distinctBy { it.id }
        if (safe.isNotEmpty()) {
            quizTitle = title
            quiz = safe
        }
    }

    if (quiz != null) {
        QuizScreen(title = quizTitle, questions = quiz!!, store = store) { quiz = null }
        return
    }
    if (plannerOpen) {
        StudyPlannerScreen(state, ::start) { plannerOpen = false }
        return
    }

    val labels = listOf("Ana Sayfa", "Kütüphane", "Harita", "Arena", "Profil")
    val icons = listOf(Icons.Default.Home, Icons.Default.MenuBook, Icons.Default.Map, Icons.Default.SportsEsports, Icons.Default.Person)

    Scaffold(
        containerColor = Bg,
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                labels.forEachIndexed { index, label ->
                    NavigationBarItem(selected = tab == index, onClick = { tab = index }, icon = { Icon(icons[index], contentDescription = label) }, label = { Text(label, fontSize = 10.sp) })
                }
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (tab) {
                0 -> HomeScreen(state, ::start, onPlanner = { plannerOpen = true })
                1 -> LibraryScreen(::start)
                2 -> AtlasScreen(::start)
                3 -> ArenaScreen(::start)
                else -> ProfileScreen(state, ::start)
            }
        }
    }
}

@Composable
private fun HomeScreen(state: AppProgressStore.Snapshot, start: (String, List<Question>) -> Unit, onPlanner: () -> Unit) {
    val accuracy = if (state.solved == 0) 0 else (state.correct * 100f / state.solved).roundToInt()
    val wrong = SharedQuestionPool.all.filter { it.id in state.wrongIds }
    val recommended = recommendedQuestions(state, 10)
    val weakTopic = buildStudyInsights(state).firstOrNull()?.topic ?: "Henüz belirlenmedi"
    val dailyGoal = 10
    val today = state.todaySolved.coerceAtMost(dailyGoal)
    val goalDone = state.todaySolved >= dailyGoal

    LazyColumn(contentPadding = PaddingValues(bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            HeaderCard {
                Text("YURDUNU BİL", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Black)
                Text("KPSS Önlisans • Türkiye Coğrafyası", color = Mint)
                Spacer(Modifier.height(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Pill("${state.xp} XP")
                    Pill("🔥 ${state.streak} gün")
                    Pill("%$accuracy")
                }
            }
        }
        item {
            DailyMissionCard(today, dailyGoal, goalDone, state.streak) {
                start("Bugünün 10 Sorusu", recommended.ifEmpty { SharedQuestionPool.all.shuffled().take(10) })
            }
        }
        item {
            ActionCard("🧭", "Bana bugün ne çalışacağımı söyle", "Kişisel çalışma rotanı hazırla", Gold, Modifier.fillMaxWidth().padding(horizontal = 18.dp), onPlanner)
        }
        item { SectionTitle("Bugünün hedefi", if (goalDone) "Bugünlük görev tamam! İstersen devam edelim." else "Az ama düzenli: bugün ${dailyGoal - today} soru daha yeter.") }
        item {
            Row(Modifier.padding(horizontal = 18.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ActionCard("⚡", "Hızlı 10", "Karışık soru", Green, Modifier.weight(1f)) { start("Hızlı 10", SharedQuestionPool.all.shuffled().take(10)) }
                ActionCard("🧠", "Akıllı Tekrar", "${recommended.size} hedef", Gold, Modifier.weight(1f)) { start("Akıllı Tekrar", recommended) }
            }
        }
        item {
            ActionCard("🔁", "Yanlışlarım", "${wrong.size} soru", Red, Modifier.fillMaxWidth().padding(horizontal = 18.dp)) {
                if (wrong.isNotEmpty()) start("Yanlışlarım", wrong.take(20))
            }
        }
        item {
            Panel(Mint) {
                Text("📊 PERFORMANS", color = Green, fontWeight = FontWeight.Black)
                Text("%$accuracy doğruluk", color = Deep, fontSize = 23.sp, fontWeight = FontWeight.Black)
                Text("${state.solved} soru • ${state.correct} doğru • ${state.wrong} yanlış", color = Color.Gray)
            }
        }
        item {
            Panel(Deep) {
                Text("🧠 ZAYIF NOKTA", color = Gold, fontWeight = FontWeight.Black)
                Text(weakTopic, color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.Black)
                Text("Akıllı Tekrar bu alanı önceliklendiriyor.", color = Mint)
            }
        }
    }
}

@Composable
private fun DailyMissionCard(today: Int, goal: Int, done: Boolean, streak: Int, onStart: () -> Unit) {
    val progress = (today.toFloat() / goal).coerceIn(0f, 1f)
    Card(colors = CardDefaults.cardColors(containerColor = Deep), shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp)) {
        Column(Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(if (done) "🎉 BUGÜNÜN GÖREVİ TAMAM" else "🎯 BUGÜNÜN MİSYONU", color = Gold, fontSize = 12.sp, fontWeight = FontWeight.Black)
                    Spacer(Modifier.height(4.dp))
                    Text(if (done) "Helal! Seriyi koruduk." else "10 soru çöz, günün boş geçmesin.", color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Black)
                    Text(if (done) "İstersen biraz daha pratik yapabiliriz." else "🔥 ${streak} günlük serin devam ediyor.", color = Mint, fontSize = 12.sp)
                }
                Text("$today/$goal", color = Green, fontSize = 22.sp, fontWeight = FontWeight.Black)
            }
            Spacer(Modifier.height(12.dp))
            LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth().height(7.dp), color = Green, trackColor = Color.White.copy(alpha = .12f))
            Spacer(Modifier.height(12.dp))
            Button(onClick = onStart, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Green, contentColor = Deep)) {
                Text(if (done) "Biraz daha çözelim" else "Bugünün sorularına başla", fontWeight = FontWeight.ExtraBold)
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
            OutlinedTextField(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth(), singleLine = true, leadingIcon = { Icon(Icons.Default.Search, null) }, placeholder = { Text("Dağ, ova, maden, nüfus…") })
        }
        item {
            Panel(Deep) {
                Text("🇹🇷 TÜRKİYE COĞRAFYA", color = Gold, fontWeight = FontWeight.Black)
                Text("${topics.size} konu", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black)
                Text("Konuya gir, sonra mini testi çöz.", color = Mint)
            }
        }
        items(topics, key = { it.title }) { topic ->
            Panel(Color.White, onClick = { start(topic.title, SharedQuestionPool.all.filter { it.topic == topic.title }.shuffled().take(10)) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(topic.icon, fontSize = 27.sp)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(topic.title, color = Deep, fontWeight = FontWeight.Black, fontSize = 17.sp)
                        Text(topic.subtitle, color = Color.Gray, fontSize = 12.sp)
                        Text("${topic.lessons.size} alt başlık • %${topic.progress}", color = Green, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Icon(Icons.Default.ArrowForward, null, tint = Green)
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
        item {
            Panel(Deep) {
                Text("🗺️ ${provinces.size} il", color = Gold, fontSize = 24.sp, fontWeight = FontWeight.Black)
                Text("İl kartını açıp mini teste geç.", color = Mint)
            }
        }
        items(provinces, key = { it.name }) { province ->
            Panel(Color.White, onClick = { start("${province.name} Mini Test", SharedQuestionPool.all.shuffled().take(5)) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📍", fontSize = 24.sp)
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(province.name, color = Deep, fontWeight = FontWeight.Black)
                        Text(province.region, color = Green, fontSize = 11.sp)
                        Text(province.clue, color = Color.Gray, fontSize = 11.sp)
                    }
                    Icon(Icons.Default.PlayArrow, null, tint = Green)
                }
            }
        }
    }
}

@Composable
private fun ArenaScreen(start: (String, List<Question>) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text("Arena", color = Deep, fontSize = 31.sp, fontWeight = FontWeight.Black)
            Text("Hız • bilgi • seri", color = Green, fontWeight = FontWeight.Bold)
        }
        item {
            Panel(Deep) {
                Text("🏆 SEZON 1", color = Gold, fontWeight = FontWeight.Black)
                Text("Türkiye Ustası", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Black)
                Text("Çevrim içi rakip sistemi için gerçek sunucu doğrulaması kullanılacak.", color = Mint)
            }
        }
        GeographyData.games.forEach { game ->
            item {
                Panel(Color.White, onClick = { start(game.title, SharedQuestionPool.all.shuffled().take(10)) }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(game.icon, fontSize = 27.sp)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(game.title, color = Deep, fontWeight = FontWeight.Black, fontSize = 17.sp)
                            Text(game.subtitle, color = Color.Gray, fontSize = 12.sp)
                        }
                        Icon(Icons.Default.PlayArrow, null, tint = Green)
                    }
                }
            }
        }
        GeographyData.arena.forEach { mode ->
            item {
                Panel(Deep, onClick = { start(mode.title, SharedQuestionPool.all.shuffled().take(10)) }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(mode.icon, fontSize = 27.sp)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(mode.title, color = Color.White, fontWeight = FontWeight.Black)
                            Text(mode.subtitle, color = Mint, fontSize = 12.sp)
                        }
                        Text(mode.reward, color = Gold, fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileScreen(state: AppProgressStore.Snapshot, start: (String, List<Question>) -> Unit) {
    val accuracy = if (state.solved == 0) 0 else (state.correct * 100f / state.solved).roundToInt()
    val wrong = SharedQuestionPool.all.filter { it.id in state.wrongIds }
    val level = 1 + state.xp / 500
    LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text("Profil & Gelişim", color = Deep, fontSize = 31.sp, fontWeight = FontWeight.Black)
            Text("Seviye $level • %$accuracy doğruluk", color = Green, fontWeight = FontWeight.Bold)
        }
        item {
            Panel(Deep) {
                Text("${state.xp} XP", color = Color.White, fontSize = 29.sp, fontWeight = FontWeight.Black)
                Text("🔥 ${state.streak} günlük seri", color = Gold, fontWeight = FontWeight.Bold)
                Text("Bugün ${state.todaySolved} soru • ${state.solved} toplam soru", color = Mint)
            }
        }
        item {
            Panel(Color.White) {
                Text("📈 KONU ANALİZİ", color = Green, fontWeight = FontWeight.Black)
                buildStudyInsights(state).take(7).forEach { insight ->
                    Text("${insight.topic}: %${insight.score} • ${insight.detail}", color = Deep, fontSize = 12.sp, modifier = Modifier.padding(top = 7.dp))
                }
            }
        }
        item {
            Panel(Color.White, onClick = { if (wrong.isNotEmpty()) start("Yanlışlarım", wrong.take(20)) }) {
                Text("🔁 Yanlış soru deposu", color = Deep, fontWeight = FontWeight.Black)
                Text("${wrong.size} soru", color = Red, fontSize = 20.sp, fontWeight = FontWeight.Black)
            }
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

    Column(Modifier.fillMaxSize().background(Bg).padding(18.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = done) { Icon(Icons.Default.Close, contentDescription = "Kapat") }
            Column(Modifier.weight(1f)) {
                Text(title, color = Deep, fontWeight = FontWeight.Black)
                Text("${index + 1}/${questions.size}", color = Green, fontWeight = FontWeight.Bold)
            }
        }
        Text(question.topic, color = Green, fontWeight = FontWeight.Black)
        Text(question.text, color = Deep, fontSize = 21.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(vertical = 16.dp))

        question.options.forEachIndexed { optionIndex, option ->
            val optionColor = when {
                !answered -> Color.White
                optionIndex == question.correctIndex -> Mint
                optionIndex == selected -> Color(0xFFFFE1E1)
                else -> Color.White
            }
            Card(colors = CardDefaults.cardColors(containerColor = optionColor), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable(enabled = !answered) {
                selected = optionIndex
                answered = true
                scope.launch { store.record(question, optionIndex == question.correctIndex) }
            }) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("${('A'.code + optionIndex).toChar()}", color = Deep, fontWeight = FontWeight.Black)
                    Spacer(Modifier.width(12.dp))
                    Text(option, color = Deep, fontWeight = if (answered && (optionIndex == question.correctIndex || optionIndex == selected)) FontWeight.Bold else FontWeight.Normal)
                }
            }
        }

        if (answered) {
            Spacer(Modifier.height(12.dp))
            Panel(if (isCorrect) Mint else Color(0xFFFFEEEE)) {
                Text(if (isCorrect) "✓ BİLDİN! +10 XP" else "✕ BU SEFER OLMADI", color = if (isCorrect) Green else Red, fontWeight = FontWeight.Black)
                Text("Dikkat Köşesi", color = Deep, fontWeight = FontWeight.Black)
                Text(question.explanation, color = Color.Gray, fontSize = 13.sp)
            }
            Spacer(Modifier.weight(1f))
            Button(onClick = {
                if (index + 1 < questions.size) {
                    index++
                    selected = -1
                    answered = false
                } else {
                    done()
                }
            }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Green)) {
                Text(if (index + 1 < questions.size) "Sonraki Soru" else "Tamamla")
            }
        }
    }
}

@Composable
private fun HeaderCard(content: @Composable ColumnScope.() -> Unit) {
    Box(Modifier.fillMaxWidth().background(Deep, RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)).padding(22.dp)) { Column(content = content) }
}

@Composable
private fun Panel(color: Color, onClick: (() -> Unit)? = null, content: @Composable ColumnScope.() -> Unit) {
    val modifier = Modifier.fillMaxWidth().background(color, RoundedCornerShape(18.dp)).then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier).padding(17.dp)
    Box(modifier) { Column(content = content) }
}

@Composable
private fun ActionCard(icon: String, title: String, subtitle: String, accent: Color, modifier: Modifier, onClick: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(20.dp), modifier = modifier.clickable(onClick = onClick)) {
        Column(Modifier.padding(15.dp)) {
            Text(icon, fontSize = 25.sp)
            Text(title, color = Deep, fontWeight = FontWeight.Black)
            Text(subtitle, color = accent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SectionTitle(title: String, subtitle: String) {
    Column(Modifier.padding(horizontal = 18.dp)) {
        Text(title, color = Deep, fontSize = 19.sp, fontWeight = FontWeight.Black)
        Text(subtitle, color = Color.Gray, fontSize = 12.sp)
    }
}

@Composable
private fun Pill(text: String) {
    Surface(color = Color.White.copy(alpha = .15f), shape = RoundedCornerShape(30.dp)) {
        Text(text, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
    }
}
