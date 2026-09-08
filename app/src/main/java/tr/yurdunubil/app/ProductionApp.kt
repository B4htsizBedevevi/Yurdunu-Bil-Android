package tr.yurdunubil.app

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private val ProdDeep = Color(0xFF06271F)
private val ProdGreen = Color(0xFF15B77A)
private val ProdMint = Color(0xFFE3F8ED)
private val ProdGold = Color(0xFFFFC857)
private val ProdRed = Color(0xFFE45454)
private val ProdBg = Color(0xFFF4F8F6)
private val ProdInk = Color(0xFF17332C)

@Composable
fun ProductionYurdunuBilApp() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val store = remember { AppProgressStore(context) }
    val state by store.snapshot.collectAsStateWithLifecycle(AppProgressStore.Snapshot())
    var tab by rememberSaveable { mutableIntStateOf(0) }
    var quiz by remember { mutableStateOf<List<Question>?>(null) }
    var title by rememberSaveable { mutableStateOf("") }

    fun openQuiz(newTitle: String, raw: List<Question>) {
        val safe = raw.filter { it.text.isNotBlank() && it.options.size >= 4 && it.correctIndex in it.options.indices }
            .distinctBy { it.id }
        if (safe.isNotEmpty()) {
            title = newTitle
            quiz = safe
        }
    }

    if (quiz != null) {
        ProductionQuiz(title, quiz!!, store) { quiz = null }
        return
    }

    Scaffold(
        containerColor = ProdBg,
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                val labels = listOf("Ana Sayfa", "Kütüphane", "Atlas", "Arena", "Profil")
                val icons = listOf(Icons.Default.Home, Icons.Default.MenuBook, Icons.Default.Map, Icons.Default.SportsEsports, Icons.Default.Person)
                labels.forEachIndexed { index, label ->
                    NavigationBarItem(
                        selected = tab == index,
                        onClick = { tab = index },
                        icon = { Icon(icons[index], contentDescription = label) },
                        label = { Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ProdDeep,
                            selectedTextColor = ProdDeep,
                            indicatorColor = ProdMint
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (tab) {
                0 -> ProductionHome(state, ::openQuiz)
                1 -> ProductionLibrary(::openQuiz)
                2 -> ProductionAtlas(::openQuiz)
                3 -> ProductionArena(::openQuiz)
                else -> ProductionProfile(state, ::openQuiz)
            }
        }
    }
}

@Composable
private fun ProductionHome(state: AppProgressStore.Snapshot, openQuiz: (String, List<Question>) -> Unit) {
    val accuracy = if (state.solved == 0) 0 else (state.correct * 100f / state.solved).roundToInt()
    val questions = SharedQuestionPool.all
    val recommended = recommendedQuestions(state, 10).ifEmpty { questions.shuffled().take(10) }
    val daily = SharedQuestionPool.dailyMode()
    val wrong = questions.filter { it.id in state.wrongIds }
    val goal = 10
    val today = state.todaySolved.coerceAtMost(goal)
    val done = state.todaySolved >= goal

    LazyColumn(contentPadding = PaddingValues(bottom = 22.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Box(Modifier.fillMaxWidth().background(Brush.verticalGradient(listOf(ProdDeep, Color(0xFF0B4A39))), RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp))) {
                Column(Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 24.dp)) {
                    Text("YURDUNU BİL", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Black)
                    Text("KPSS Önlisans • Türkiye Coğrafyası", color = ProdMint, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatPill("${state.xp} XP")
                        StatPill("🔥 ${state.streak}")
                        StatPill("%$accuracy")
                    }
                }
            }
        }
        item {
            ActionPanel(ProdDeep) {
                Text(if (done) "🎉 BUGÜNÜN ROTASI TAMAM" else "🎯 BUGÜNÜN ROTASI", color = ProdGold, fontWeight = FontWeight.Black, fontSize = 12.sp)
                Spacer(Modifier.height(5.dp))
                Text(if (done) "İstersen seriyi büyütelim." else "${goal - today} soru daha. Sonra mola!", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
                Text("${daily.icon} ${daily.title} • ${daily.questions} soru", color = ProdMint, fontSize = 13.sp)
                Spacer(Modifier.height(11.dp))
                LinearProgressIndicator(progress = { (today.toFloat() / goal).coerceIn(0f, 1f) }, modifier = Modifier.fillMaxWidth().height(7.dp), color = ProdGreen, trackColor = Color.White.copy(alpha = .14f))
                Spacer(Modifier.height(12.dp))
                Button(onClick = { openQuiz("${daily.title} • Bugünün Rotası", dailyQuestions(daily)) }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = ProdGreen, contentColor = ProdDeep)) {
                    Text(if (done) "Bir tur daha" else "Başlayalım", fontWeight = FontWeight.Black)
                }
            }
        }
        item {
            Row(Modifier.padding(horizontal = 18.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                QuickCard("⚡", "Hızlı 10", "${questions.size} soruluk havuz", ProdGreen, Modifier.weight(1f)) { openQuiz("Hızlı 10", questions.shuffled().take(10)) }
                QuickCard("🧠", "Akıllı Tekrar", "${recommended.size} hedef soru", ProdGold, Modifier.weight(1f)) { openQuiz("Akıllı Tekrar", recommended) }
            }
        }
        item {
            ActionPanel(Color.White) {
                Text("🔁 YANLIŞLARIM", color = ProdRed, fontWeight = FontWeight.Black, fontSize = 12.sp)
                Text("${wrong.size} soru bekliyor", color = ProdInk, fontSize = 20.sp, fontWeight = FontWeight.Black)
                Text("Yanlış yaptıklarını tekrar çözerek kalıcı hale getir.", color = Color.Gray, fontSize = 12.sp)
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = { if (wrong.isNotEmpty()) openQuiz("Yanlışlarım", wrong.take(20)) }, modifier = Modifier.fillMaxWidth()) { Text("Yanlışlara dön") }
            }
        }
        item {
            ActionPanel(ProdMint) {
                Text("📚 TEK SORU HAVUZU", color = ProdGreen, fontWeight = FontWeight.Black, fontSize = 12.sp)
                Text("${questions.size} aktif soru", color = ProdDeep, fontSize = 27.sp, fontWeight = FontWeight.Black)
                Text("Testler, oyunlar ve Arena aynı doğrulanmış havuzu kullanıyor.", color = ProdInk, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun ProductionLibrary(openQuiz: (String, List<Question>) -> Unit) {
    var query by rememberSaveable { mutableStateOf("") }
    var selected by rememberSaveable { mutableStateOf<String?>(null) }
    val topics = GeographyData.topics.filter { it.title.contains(query, true) || it.subtitle.contains(query, true) }
    val selectedNote = GeographyLibraryContent.notes.firstOrNull { it.title == selected }

    if (selectedNote != null) {
        LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                IconButton(onClick = { selected = null }) { Icon(Icons.Default.ArrowBack, contentDescription = "Geri") }
                Text(selectedNote.title, color = ProdDeep, fontSize = 30.sp, fontWeight = FontWeight.Black)
                Text(selectedNote.summary, color = ProdGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            item { NoteCard("🧠 Bilmen gerekenler", selectedNote.keyPoints, Color.White) }
            item {
                ActionPanel(ProdDeep) {
                    Text("💡 SINAV TAKTİĞİ", color = ProdGold, fontWeight = FontWeight.Black)
                    Spacer(Modifier.height(5.dp))
                    Text(selectedNote.examTip, color = Color.White, fontSize = 14.sp, lineHeight = 20.sp)
                }
            }
            item {
                val mapped = SharedQuestionPool.topicForLibrary(selectedNote.title)
                val bank = SharedQuestionPool.all.filter { it.topic in mapped }
                ActionPanel(ProdMint) {
                    Text("🎯 BU KONUDAN TEST", color = ProdGreen, fontWeight = FontWeight.Black)
                    Text("${bank.size} soru bulundu", color = ProdDeep, fontSize = 19.sp, fontWeight = FontWeight.Black)
                    Button(onClick = { openQuiz("${selectedNote.title} • Mini Test", bank.shuffled().take(10)) }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = ProdDeep)) { Text("10 soruluk mini test", color = Color.White, fontWeight = FontWeight.Black) }
                }
            }
        }
        return
    }

    LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text("Kütüphane", color = ProdDeep, fontSize = 31.sp, fontWeight = FontWeight.Black)
            Text("Oku • bağlantı kur • hemen test et", color = ProdGreen, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth(), singleLine = true, leadingIcon = { Icon(Icons.Default.Search, null) }, placeholder = { Text("Dağ, maden, nüfus, tarım…") })
        }
        item {
            ActionPanel(ProdDeep) {
                Text("🇹🇷 TÜRKİYE COĞRAFYA KÜTÜPHANESİ", color = ProdGold, fontWeight = FontWeight.Black, fontSize = 12.sp)
                Text("${GeographyLibraryContent.notes.size} anlatım kartı", color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.Black)
                Text("Her kartta anahtar bilgiler, sınav taktiği ve mini test var.", color = ProdMint, fontSize = 12.sp)
            }
        }
        items(topics, key = { it.title }) { topic ->
            val note = GeographyLibraryContent.notes.firstOrNull { it.title == topic.title }
            ActionPanel(Color.White, onClick = { selected = note?.title }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(topic.icon, fontSize = 28.sp)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(topic.title, color = ProdDeep, fontWeight = FontWeight.Black, fontSize = 17.sp)
                        Text(topic.subtitle, color = Color.Gray, fontSize = 12.sp)
                        Text(if (note == null) "${topic.lessons.size} alt başlık" else "${note.keyPoints.size} kilit bilgi • sınav taktiği", color = ProdGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Icon(Icons.Default.ArrowForward, null, tint = ProdGreen)
                }
            }
        }
    }
}

@Composable
private fun ProductionAtlas(openQuiz: (String, List<Question>) -> Unit) {
    val filters = listOf("Tümü", "Marmara", "Ege", "Akdeniz", "İç Anadolu", "Karadeniz", "Doğu Anadolu", "Güneydoğu Anadolu")
    var filter by rememberSaveable { mutableStateOf("Tümü") }
    val provinces = GeographyData.provinces.filter { filter == "Tümü" || it.region == filter }
    LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text("Türkiye Atlası", color = ProdDeep, fontSize = 31.sp, fontWeight = FontWeight.Black)
            Text("İl → bölge → özellik → soru", color = ProdGreen, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) { filters.forEach { FilterChip(selected = filter == it, onClick = { filter = it }, label = { Text(it) }) } }
        }
        item { ActionPanel(ProdMint) { Text("🗺️ ${provinces.size} şehir kartı", color = ProdDeep, fontSize = 22.sp, fontWeight = FontWeight.Black); Text("Kartı seç, ipucunu gör ve mini teste geç.", color = ProdInk, fontSize = 12.sp) } }
        items(provinces, key = { it.name }) { province ->
            ActionPanel(Color.White, onClick = { openQuiz("${province.name} • Mini Test", SharedQuestionPool.all.shuffled().take(5)) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📍", fontSize = 24.sp)
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(province.name, color = ProdDeep, fontWeight = FontWeight.Black, fontSize = 16.sp)
                        Text(province.region, color = ProdGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(province.clue, color = Color.Gray, fontSize = 11.sp)
                        Text(province.facts.joinToString(" • "), color = ProdInk, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
                    }
                    Icon(Icons.Default.PlayArrow, null, tint = ProdGreen)
                }
            }
        }
    }
}

@Composable
private fun ProductionArena(openQuiz: (String, List<Question>) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("Arena", color = ProdDeep, fontSize = 31.sp, fontWeight = FontWeight.Black); Text("Hız • bilgi • seri • rekabet", color = ProdGreen, fontWeight = FontWeight.Bold) }
        item {
            ActionPanel(Brush.verticalGradient(listOf(ProdDeep, Color(0xFF0B4A39)))) {
                Text("🏆 SEZON 1", color = ProdGold, fontWeight = FontWeight.Black)
                Text("Türkiye Ustası", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Black)
                Text("Yerel testlerde aynı ortak havuz kullanılıyor. Çevrim içi PvP sunucu doğrulamasıyla ayrı çalışır.", color = ProdMint, fontSize = 12.sp, lineHeight = 18.sp)
            }
        }
        SharedGameModes.arenaModes.forEach { mode ->
            item {
                ActionPanel(Color.White, onClick = { openQuiz(mode.title, SharedQuestionPool.pick(mode)) }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(mode.icon, fontSize = 28.sp)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) { Text(mode.title, color = ProdDeep, fontWeight = FontWeight.Black, fontSize = 17.sp); Text(mode.subtitle, color = Color.Gray, fontSize = 12.sp); Text("${mode.questions} soru • ${mode.rewardXp} XP", color = ProdGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        Icon(Icons.Default.PlayArrow, null, tint = ProdGreen)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductionProfile(state: AppProgressStore.Snapshot, openQuiz: (String, List<Question>) -> Unit) {
    val accuracy = if (state.solved == 0) 0 else (state.correct * 100f / state.solved).roundToInt()
    val level = 1 + state.xp / 500
    val wrong = SharedQuestionPool.all.filter { it.id in state.wrongIds }
    LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("Profil & Gelişim", color = ProdDeep, fontSize = 31.sp, fontWeight = FontWeight.Black); Text("Seviye $level • %$accuracy doğruluk", color = ProdGreen, fontWeight = FontWeight.Bold) }
        item { ActionPanel(ProdDeep) { Text("$level. SEVİYE", color = ProdGold, fontWeight = FontWeight.Black); Text("${state.xp} XP", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Black); Text("🔥 ${state.streak} günlük seri • bugün ${state.todaySolved} soru", color = ProdMint) } }
        item {
            ActionPanel(Color.White) {
                Text("📈 KONU ANALİZİ", color = ProdGreen, fontWeight = FontWeight.Black)
                buildStudyInsights(state).take(8).forEach { insight -> Text("${insight.topic}  •  %${insight.score}  •  ${insight.detail}", color = ProdInk, fontSize = 12.sp, modifier = Modifier.padding(top = 7.dp)) }
            }
        }
        item { ActionPanel(ProdMint, onClick = { if (wrong.isNotEmpty()) openQuiz("Yanlışlarım", wrong.take(20)) }) { Text("🔁 YANLIŞ DEPOSU", color = ProdRed, fontWeight = FontWeight.Black); Text("${wrong.size} soru", color = ProdDeep, fontSize = 21.sp, fontWeight = FontWeight.Black); Text("Tekrar çözerek ustalaş.", color = ProdInk, fontSize = 12.sp) } }
    }
}

@Composable
private fun ProductionQuiz(title: String, questions: List<Question>, store: AppProgressStore, done: () -> Unit) {
    var index by rememberSaveable { mutableIntStateOf(0) }
    var selected by rememberSaveable { mutableIntStateOf(-1) }
    var answered by rememberSaveable { mutableStateOf(false) }
    var localCorrect by rememberSaveable { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()
    val question = questions[index]
    val correct = selected == question.correctIndex

    Column(Modifier.fillMaxSize().background(ProdBg).padding(18.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = done) { Icon(Icons.Default.Close, contentDescription = "Kapat") }
            Column(Modifier.weight(1f)) { Text(title, color = ProdDeep, fontWeight = FontWeight.Black, maxLines = 1); Text("${index + 1}/${questions.size}", color = ProdGreen, fontWeight = FontWeight.Bold) }
        }
        LinearProgressIndicator(progress = { (index + 1).toFloat() / questions.size }, modifier = Modifier.fillMaxWidth().height(6.dp), color = ProdGreen, trackColor = Color.White)
        Spacer(Modifier.height(14.dp))
        Text(question.topic, color = ProdGreen, fontWeight = FontWeight.Black, fontSize = 12.sp)
        Spacer(Modifier.height(7.dp))
        Text(question.text, color = ProdDeep, fontSize = 20.sp, fontWeight = FontWeight.Black, lineHeight = 27.sp)
        Spacer(Modifier.height(14.dp))

        question.options.forEachIndexed { optionIndex, option ->
            val isChosen = selected == optionIndex
            val isAnswer = answered && optionIndex == question.correctIndex
            val isWrongChoice = answered && isChosen && !correct
            val bg = when {
                isAnswer -> ProdGreen.copy(alpha = .20f)
                isWrongChoice -> ProdRed.copy(alpha = .18f)
                isChosen -> ProdMint
                else -> Color.White
            }
            val border = when {
                isAnswer -> ProdGreen
                isWrongChoice -> ProdRed
                isChosen -> ProdGreen
                else -> Color.Transparent
            }
            Card(colors = CardDefaults.cardColors(containerColor = bg), border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(border, border))), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                TextButton(onClick = { if (!answered) selected = optionIndex }, modifier = Modifier.fillMaxWidth().padding(6.dp)) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = RoundedCornerShape(10.dp), color = Color.White.copy(alpha = .78f)) { Text("${('A'.code + optionIndex).toChar()}", color = ProdDeep, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) }
                        Spacer(Modifier.width(10.dp))
                        Text(option, color = ProdInk, fontSize = 14.sp, modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        if (answered) {
            Card(colors = CardDefaults.cardColors(containerColor = if (correct) ProdMint else ProdRed.copy(alpha = .10f)), shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(14.dp)) {
                    Text(if (correct) "✅ Doğru!" else "❌ Bu sefer olmadı.", color = if (correct) ProdGreen else ProdRed, fontWeight = FontWeight.Black)
                    Text(question.explanation, color = ProdInk, fontSize = 12.sp, lineHeight = 18.sp, modifier = Modifier.padding(top = 6.dp))
                }
            }
            Spacer(Modifier.height(10.dp))
        }

        Spacer(Modifier.weight(1f))
        Button(
            onClick = {
                if (!answered) {
                    if (selected == -1) return@Button
                    answered = true
                    if (correct) localCorrect += 1
                    scope.launch { store.record(question, correct) }
                } else if (index < questions.lastIndex) {
                    index += 1
                    selected = -1
                    answered = false
                } else {
                    done()
                }
            },
            enabled = answered || selected >= 0,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = ProdDeep, disabledContainerColor = Color.LightGray)
        ) {
            Text(if (!answered) "Cevabımı kontrol et" else if (index < questions.lastIndex) "Sonraki soru" else "Testi bitir • $localCorrect doğru", color = Color.White, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun NoteCard(title: String, items: List<String>, background: Color) {
    ActionPanel(background) {
        Text(title, color = ProdGreen, fontWeight = FontWeight.Black)
        items.forEach { item -> Text("• $item", color = ProdInk, fontSize = 13.sp, modifier = Modifier.padding(top = 7.dp)) }
    }
}

@Composable
private fun ActionPanel(background: Color, onClick: (() -> Unit)? = null, content: @Composable ColumnScope.() -> Unit) {
    val modifier = Modifier.fillMaxWidth().padding(horizontal = if (background == ProdDeep) 0.dp else 0.dp)
    Card(colors = CardDefaults.cardColors(containerColor = background), shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), onClick = onClick ?: {}) { Column(modifier.padding(16.dp), content = content) }
}

@Composable
private fun StatPill(text: String) {
    Surface(color = Color.White.copy(alpha = .12f), shape = RoundedCornerShape(30.dp)) { Text(text, color = Color.White, fontWeight = FontWeight.Black, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)) }
}

@Composable
private fun QuickCard(icon: String, title: String, subtitle: String, accent: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(onClick = onClick, colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(18.dp), modifier = modifier) {
        Column(Modifier.padding(14.dp)) { Text(icon, fontSize = 24.sp); Text(title, color = ProdDeep, fontWeight = FontWeight.Black); Text(subtitle, color = Color.Gray, fontSize = 11.sp, modifier = Modifier.padding(top = 3.dp)); Text("Başla →", color = accent, fontSize = 11.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(top = 9.dp)) }
    }
}

private fun dailyQuestions(mode: SharedGameMode): List<Question> = SharedQuestionPool.pick(mode, System.currentTimeMillis() / 86_400_000L)
