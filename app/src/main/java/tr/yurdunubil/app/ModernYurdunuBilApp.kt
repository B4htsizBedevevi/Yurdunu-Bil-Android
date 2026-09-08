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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private val ModernDeep = Color(0xFF06271F)
private val ModernGreen = Color(0xFF15B77A)
private val ModernMint = Color(0xFFE3F8ED)
private val ModernGold = Color(0xFFFFC857)
private val ModernRed = Color(0xFFE45454)
private val ModernBg = Color(0xFFF4F8F6)
private val ModernInk = Color(0xFF17332C)

@Composable
fun ModernYurdunuBilApp() {
    val store = remember { AppProgressStore(androidx.compose.ui.platform.LocalContext.current) }
    val state by store.snapshot.collectAsStateWithLifecycle(AppProgressStore.Snapshot())
    var tab by remember { mutableIntStateOf(0) }
    var quiz by remember { mutableStateOf<List<Question>?>(null) }
    var quizTitle by remember { mutableStateOf("") }

    fun start(title: String, mode: SharedGameMode) {
        val questions = SharedQuestionPool.pick(mode).filter { it.text.isNotBlank() && it.options.size >= 2 && it.correctIndex in it.options.indices }.distinctBy { it.id }
        if (questions.isNotEmpty()) { quizTitle = title; quiz = questions }
    }

    fun startQuestions(title: String, questions: List<Question>) {
        val safe = questions.filter { it.text.isNotBlank() && it.options.size >= 2 && it.correctIndex in it.options.indices }.distinctBy { it.id }
        if (safe.isNotEmpty()) { quizTitle = title; quiz = safe }
    }

    if (quiz != null) {
        ModernQuizScreen(quizTitle, quiz!!, store) { quiz = null }
        return
    }

    Scaffold(
        containerColor = ModernBg,
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                val labels = listOf("Ana Sayfa", "Kütüphane", "Atlas", "Arena", "Profil")
                val icons = listOf(Icons.Default.Home, Icons.Default.MenuBook, Icons.Default.Map, Icons.Default.SportsEsports, Icons.Default.Person)
                labels.forEachIndexed { i, label ->
                    NavigationBarItem(
                        selected = tab == i,
                        onClick = { tab = i },
                        icon = { Icon(icons[i], label) },
                        label = { Text(label, fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = ModernDeep, selectedTextColor = ModernDeep, indicatorColor = ModernMint)
                    )
                }
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (tab) {
                0 -> ModernHome(state, start, startQuestions)
                1 -> ModernLibrary(startQuestions)
                2 -> ModernAtlas(start)
                3 -> ModernArena(start)
                else -> ModernProfile(state, startQuestions)
            }
        }
    }
}

@Composable
private fun ModernHome(state: AppProgressStore.Snapshot, start: (String, SharedGameMode) -> Unit, startQuestions: (String, List<Question>) -> Unit) {
    val accuracy = if (state.solved == 0) 0 else (state.correct * 100f / state.solved).roundToInt()
    val bank = SharedQuestionPool.all
    val recommended = recommendedQuestions(state, 10).ifEmpty { bank.shuffled().take(10) }
    val daily = SharedQuestionPool.dailyMode()
    val event = SharedGameModes.eventForToday()
    LazyColumn(contentPadding = PaddingValues(bottom = 28.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { ModernHero(state, accuracy, bank.size) }
        item {
            ModernSection("Bugünün rotası", "Sana özel değil, gerçekten işe yarayan kısa tur")
            ModernActionCard("🎯", daily.title, daily.subtitle, ModernGreen, "${daily.questions} soru • ${daily.rewardXp} XP") { start("Bugünün Rotası", daily) }
        }
        item {
            ModernSection("Bugünün etkinliği", "Her gün ortak soru havuzundan değişir")
            ModernActionCard(event.icon, event.title, event.subtitle, ModernGold, "ETKİNLİK") { start("Günün Etkinliği • ${event.title}", event) }
        }
        item {
            ModernSection("Hızlı başlangıç", "İstersen 5 dakikada bile çalışabilirsin")
            Row(Modifier.padding(horizontal = 18.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ModernMiniCard("⚡", "Hızlı 10", "10 soru", ModernGreen, Modifier.weight(1f)) { start("Hızlı 10", SharedGameModes.quick) }
                ModernMiniCard("🧠", "Akıllı Tekrar", "${recommended.size} hedef", ModernGold, Modifier.weight(1f)) { startQuestions("Akıllı Tekrar", recommended) }
            }
        }
        item {
            ModernActionCard("🔁", "Yanlışlarım", "Yanlış yaptığın soruları yeniden çöz", ModernRed, "${state.wrongIds.size} soru") {
                val wrong = bank.filter { it.id in state.wrongIds }.take(20)
                if (wrong.isNotEmpty()) startQuestions("Yanlışlarım", wrong)
            }
        }
        item {
            ModernPanel {
                Text("📚 TEK SORU HAVUZU", color = ModernGreen, fontWeight = FontWeight.Black, fontSize = 12.sp)
                Text("${bank.size} soru", color = ModernDeep, fontSize = 27.sp, fontWeight = FontWeight.Black)
                Text("Kütüphane, oyunlar, günlük etkinlikler ve Arena aynı ortak havuzdan besleniyor.", color = Color.Gray, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun ModernHero(state: AppProgressStore.Snapshot, accuracy: Int, bankSize: Int) {
    Box(Modifier.fillMaxWidth().background(Brush.verticalGradient(listOf(ModernDeep, Color(0xFF0B4A39))), RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp))) {
        Column(Modifier.padding(start = 20.dp, end = 20.dp, top = 22.dp, bottom = 24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("YURDUNU BİL", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black)
                    Text("Türkiye coğrafyasını gerçekten öğren.", color = ModernMint, fontSize = 13.sp)
                }
                Surface(shape = RoundedCornerShape(16.dp), color = Color.White.copy(alpha = .12f)) {
                    Text("2026", color = ModernGold, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp))
                }
            }
            Spacer(Modifier.height(18.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ModernStat("XP", state.xp.toString())
                ModernStat("SERİ", "🔥 ${state.streak}")
                ModernStat("DOĞRULUK", "%$accuracy")
            }
            Spacer(Modifier.height(14.dp))
            Text("Ortak havuz: $bankSize soru", color = Color.White.copy(alpha = .78f), fontSize = 11.sp)
        }
    }
}

@Composable
private fun ModernStat(label: String, value: String) {
    Surface(color = Color.White.copy(alpha = .10f), shape = RoundedCornerShape(14.dp)) {
        Column(Modifier.padding(horizontal = 12.dp, vertical = 9.dp)) {
            Text(label, color = ModernMint.copy(alpha = .75f), fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Text(value, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun ModernLibrary(startQuestions: (String, List<Question>) -> Unit) {
    var query by remember { mutableStateOf("") }
    val notes = GeographyLibraryContent.notes.filter { it.title.contains(query, true) || it.summary.contains(query, true) || it.keyPoints.any { p -> p.contains(query, true) } }
    LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("Kütüphane", color = ModernDeep, fontSize = 31.sp, fontWeight = FontWeight.Black)
            Text("Oku → bağlantı kur → hemen test et", color = ModernGreen, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(18.dp), leadingIcon = { Icon(Icons.Default.Search, null) }, placeholder = { Text("Dağ, ova, maden, iklim…") })
        }
        item {
            ModernPanel(ModernDeep) {
                Text("📖 SINAV ODAKLI KÜTÜPHANE", color = ModernGold, fontWeight = FontWeight.Black, fontSize = 12.sp)
                Text("${notes.size} konu kartı", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Black)
                Text("Kaynaklardan çıkarılan başlıklar kısa, bağlantılı ve soru çözmeye hazır tutuluyor.", color = ModernMint, fontSize = 12.sp)
            }
        }
        items(notes, key = { it.title }) { note ->
            val topics = SharedQuestionPool.topicForLibrary(note.title)
            val questions = SharedQuestionPool.all.filter { it.topic in topics }
            ModernLibraryCard(note.title, note.summary, note.keyPoints, questions.size, note.examTip) {
                if (questions.isNotEmpty()) startQuestions("${note.title} Testi", questions.shuffled().take(10))
            }
        }
    }
}

@Composable
private fun ModernLibraryCard(title: String, summary: String, points: List<String>, count: Int, tip: String, onTest: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(22.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(color = ModernMint, shape = RoundedCornerShape(14.dp)) { Text("📚", fontSize = 23.sp, modifier = Modifier.padding(10.dp)) }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) { Text(title, color = ModernDeep, fontSize = 18.sp, fontWeight = FontWeight.Black); Text("$count soru • kaynak odaklı", color = ModernGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            }
            Spacer(Modifier.height(8.dp))
            Text(summary, color = ModernInk, fontSize = 13.sp)
            Spacer(Modifier.height(8.dp))
            points.take(5).forEach { Text("• $it", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(vertical = 2.dp)) }
            Spacer(Modifier.height(8.dp))
            Surface(color = Color(0xFFFFF8E5), shape = RoundedCornerShape(13.dp)) { Text("💡 $tip", color = ModernInk, fontSize = 11.sp, modifier = Modifier.padding(10.dp)) }
            Spacer(Modifier.height(10.dp))
            Button(onClick = onTest, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(15.dp), colors = ButtonDefaults.buttonColors(containerColor = ModernDeep)) { Icon(Icons.Default.PlayArrow, null); Spacer(Modifier.width(6.dp)); Text("Bu konuyu test et", fontWeight = FontWeight.ExtraBold) }
        }
    }
}

@Composable
private fun ModernAtlas(start: (String, SharedGameMode) -> Unit) {
    val regions = listOf("Tümü", "Marmara", "Ege", "Akdeniz", "İç Anadolu", "Karadeniz", "Doğu Anadolu", "Güneydoğu Anadolu")
    var selected by remember { mutableStateOf("Tümü") }
    val provinces = GeographyData.provinces.filter { selected == "Tümü" || it.region == selected }
    LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("Türkiye Atlası", color = ModernDeep, fontSize = 31.sp, fontWeight = FontWeight.Black)
            Text("Harita bilgisi + ortak soru havuzu", color = ModernGreen, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) { regions.forEach { r -> FilterChip(selected = selected == r, onClick = { selected = r }, label = { Text(r) }) } }
        }
        item { ModernActionCard("🗺️", "Atlas Sprint", "Harita ve mekân bilgisini hızla tekrar et", ModernGreen, "15 soru") { start("Atlas Sprint", SharedGameModes.atlas) } }
        item { ModernPanel(ModernDeep) { Text("📍 ${provinces.size} il", color = ModernGold, fontSize = 24.sp, fontWeight = FontWeight.Black); Text("Bir ili seçtiğinde coğrafi ipucunu gör; testi Atlas Sprint ile ortak havuzdan çöz.", color = ModernMint, fontSize = 12.sp) } }
        items(provinces, key = { it.name }) { province ->
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("📍", fontSize = 24.sp); Spacer(Modifier.width(10.dp)); Column(Modifier.weight(1f)) { Text(province.name, color = ModernDeep, fontWeight = FontWeight.Black); Text(province.region, color = ModernGreen, fontSize = 11.sp); Text(province.clue, color = Color.Gray, fontSize = 11.sp) }
                    Icon(Icons.Default.ArrowForward, null, tint = ModernGreen)
                }
            }
        }
    }
}

@Composable
private fun ModernArena(start: (String, SharedGameMode) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("Arena", color = ModernDeep, fontSize = 31.sp, fontWeight = FontWeight.Black)
            Text("Rekabet ayrı, soru kaynağı tek.", color = ModernGreen, fontWeight = FontWeight.Bold)
        }
        item {
            ModernPanel(ModernDeep) {
                Text("🏆 SEZON", color = ModernGold, fontWeight = FontWeight.Black)
                Text("Tek havuz. Aynı standart.", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black)
                Text("Tüm Arena modları SharedQuestionPool üzerinden çalışır. Çevrim içi eşleşmede de sunucu tarafı aynı soru kimliklerini doğrulayacak.", color = ModernMint, fontSize = 12.sp)
            }
        }
        items(SharedGameModes.arenaModes, key = { it.id }) { mode ->
            ModernArenaCard(mode) { start(mode.title, mode) }
        }
        item { ModernSection("Tek oyunculu etkinlikler", "Arena dışındaki oyunlar da aynı bankayı kullanır") }
        items(SharedGameModes.games, key = { it.id }) { mode -> ModernArenaCard(mode) { start(mode.title, mode) } }
    }
}

@Composable
private fun ModernArenaCard(mode: SharedGameMode, onClick: () -> Unit) {
    val accent = if (mode.arena) ModernDeep else Color.White
    val titleColor = if (mode.arena) Color.White else ModernDeep
    val subColor = if (mode.arena) ModernMint else Color.Gray
    Card(colors = CardDefaults.cardColors(containerColor = accent), shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(defaultElevation = if (mode.arena) 5.dp else 2.dp), modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(color = if (mode.arena) Color.White.copy(alpha = .12f) else ModernMint, shape = RoundedCornerShape(15.dp)) { Text(mode.icon, fontSize = 25.sp, modifier = Modifier.padding(10.dp)) }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) { Text(mode.title, color = titleColor, fontWeight = FontWeight.Black, fontSize = 17.sp); Text(mode.subtitle, color = subColor, fontSize = 12.sp); Text("${mode.questions} soru • ${mode.seconds}s • +${mode.rewardXp} XP", color = if (mode.arena) ModernGold else ModernGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold) }
            Icon(Icons.Default.PlayArrow, null, tint = if (mode.arena) ModernGold else ModernGreen)
        }
    }
}

@Composable
private fun ModernProfile(state: AppProgressStore.Snapshot, startQuestions: (String, List<Question>) -> Unit) {
    val accuracy = if (state.solved == 0) 0 else (state.correct * 100f / state.solved).roundToInt()
    val wrong = SharedQuestionPool.all.filter { it.id in state.wrongIds }
    LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Profil & Gelişim", color = ModernDeep, fontSize = 31.sp, fontWeight = FontWeight.Black); Text("Her soru seni biraz daha Türkiye'ye yaklaştırıyor.", color = ModernGreen, fontWeight = FontWeight.Bold) }
        item { ModernPanel(ModernDeep) { Text("SEVİYE ${1 + state.xp / 500}", color = ModernGold, fontWeight = FontWeight.Black); Text("${state.xp} XP", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Black); Text("🔥 ${state.streak} günlük seri • %$accuracy doğruluk", color = ModernMint) } }
        item { ModernStatGrid(state) }
        item { ModernActionCard("🔁", "Yanlışları tekrar et", "${wrong.size} kayıtlı soru", ModernRed, "TEKRAR") { if (wrong.isNotEmpty()) startQuestions("Yanlışlarım", wrong.take(20)) } }
        item { ModernPanel { Text("🎯 BUGÜN", color = ModernGreen, fontWeight = FontWeight.Black); Text("${state.todaySolved} soru çözdün", color = ModernDeep, fontSize = 22.sp, fontWeight = FontWeight.Black); Text("Düzenli tekrar, tek seferlik maratondan daha değerli.", color = Color.Gray, fontSize = 12.sp) } }
    }
}

@Composable
private fun ModernStatGrid(state: AppProgressStore.Snapshot) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        ModernMiniStat("ÇÖZÜLEN", state.solved.toString(), Modifier.weight(1f))
        ModernMiniStat("DOĞRU", state.correct.toString(), Modifier.weight(1f))
        ModernMiniStat("YANLIŞ", state.wrong.toString(), Modifier.weight(1f))
    }
}

@Composable
private fun ModernMiniStat(label: String, value: String, modifier: Modifier) {
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(17.dp), modifier = modifier) { Column(Modifier.padding(12.dp)) { Text(label, color = Color.Gray, fontSize = 9.sp, fontWeight = FontWeight.Bold); Text(value, color = ModernDeep, fontSize = 20.sp, fontWeight = FontWeight.Black) } }
}

@Composable
private fun ModernSection(title: String, subtitle: String) {
    Column(Modifier.padding(horizontal = 18.dp)) { Text(title, color = ModernDeep, fontSize = 18.sp, fontWeight = FontWeight.Black); Text(subtitle, color = Color.Gray, fontSize = 11.sp) }
}

@Composable
private fun ModernActionCard(icon: String, title: String, subtitle: String, accent: Color, badge: String, onClick: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(22.dp), elevation = CardDefaults.cardElevation(defaultElevation = 3.dp), modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp).clickable(onClick = onClick)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(color = accent.copy(alpha = .13f), shape = RoundedCornerShape(16.dp)) { Text(icon, fontSize = 25.sp, modifier = Modifier.padding(11.dp)) }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) { Text(title, color = ModernDeep, fontSize = 17.sp, fontWeight = FontWeight.Black); Text(subtitle, color = Color.Gray, fontSize = 12.sp); Spacer(Modifier.height(4.dp)); Text(badge, color = accent, fontSize = 10.sp, fontWeight = FontWeight.Black) }
            Icon(Icons.Default.ArrowForward, null, tint = accent)
        }
    }
}

@Composable
private fun ModernMiniCard(icon: String, title: String, subtitle: String, accent: Color, modifier: Modifier, onClick: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), modifier = modifier.clickable(onClick = onClick)) {
        Column(Modifier.padding(14.dp)) { Surface(color = accent.copy(alpha = .13f), shape = RoundedCornerShape(13.dp)) { Text(icon, fontSize = 22.sp, modifier = Modifier.padding(8.dp)) }; Spacer(Modifier.height(9.dp)); Text(title, color = ModernDeep, fontWeight = FontWeight.Black); Text(subtitle, color = Color.Gray, fontSize = 11.sp) }
    }
}

@Composable
private fun ModernPanel(color: Color = Color.White, content: @Composable ColumnScope.() -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = color), shape = RoundedCornerShape(22.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp)) { Column(Modifier.padding(17.dp), content = content) }
}

@Composable
private fun ModernQuizScreen(title: String, questions: List<Question>, store: AppProgressStore, onExit: () -> Unit) {
    var index by remember { mutableIntStateOf(0) }
    var selected by remember { mutableIntStateOf(-1) }
    var score by remember { mutableIntStateOf(0) }
    var finished by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    if (finished) {
        Column(Modifier.fillMaxSize().background(ModernBg).padding(22.dp), verticalArrangement = Arrangement.Center) {
            Text("🏆 TUR TAMAM", color = ModernGreen, fontSize = 14.sp, fontWeight = FontWeight.Black)
            Text(title, color = ModernDeep, fontSize = 29.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(12.dp))
            Text("$score / ${questions.size}", color = ModernDeep, fontSize = 46.sp, fontWeight = FontWeight.Black)
            Text("Sorular ortak havuzdan geldi; sonuçların da profilindeki tekrar sistemine işlendi.", color = Color.Gray)
            Spacer(Modifier.height(22.dp))
            Button(onClick = onExit, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(17.dp), colors = ButtonDefaults.buttonColors(containerColor = ModernDeep)) { Text("Devam edelim", fontWeight = FontWeight.ExtraBold) }
        }
        return
    }
    val q = questions[index]
    Column(Modifier.fillMaxSize().background(ModernBg)) {
        Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onExit) { Icon(Icons.Default.Close, "Çık") }
            Column(Modifier.weight(1f)) { Text(title, color = ModernDeep, fontWeight = FontWeight.Black); Text("Soru ${index + 1} / ${questions.size}", color = ModernGreen, fontSize = 11.sp) }
        }
        LinearProgressIndicator(progress = { (index + 1f) / questions.size }, modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp).height(7.dp), color = ModernGreen, trackColor = Color.White)
        LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item { Text(q.topic.uppercase(), color = ModernGreen, fontSize = 11.sp, fontWeight = FontWeight.Black); Text(q.text, color = ModernDeep, fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp)) }
            items(q.options.indices.toList()) { optionIndex ->
                val chosen = selected == optionIndex
                val correct = selected != -1 && optionIndex == q.correctIndex
                val wrongChosen = selected == optionIndex && selected != q.correctIndex
                val bg = when { correct -> ModernMint; wrongChosen -> Color(0xFFFFE6E6); chosen -> Color(0xFFEAF2EF); else -> Color.White }
                val border = when { correct -> ModernGreen; wrongChosen -> ModernRed; else -> Color.Transparent }
                Card(colors = CardDefaults.cardColors(containerColor = bg), shape = RoundedCornerShape(17.dp), modifier = Modifier.fillMaxWidth().clickable(enabled = selected == -1) {
                    selected = optionIndex
                    if (optionIndex == q.correctIndex) score++
                    scope.launch { store.record(q, optionIndex == q.correctIndex) }
                }) { Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) { Surface(color = border.takeIf { it != Color.Transparent } ?: ModernBg, shape = RoundedCornerShape(11.dp)) { Text(('A'.code + optionIndex).toChar().toString(), color = if (border != Color.Transparent) ModernDeep else Color.Gray, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)) }; Spacer(Modifier.width(10.dp)); Text(q.options[optionIndex], color = ModernInk, fontSize = 14.sp, fontWeight = if (chosen || correct) FontWeight.Bold else FontWeight.Normal) } }
            }
            if (selected != -1) {
                item { ModernPanel(if (selected == q.correctIndex) ModernMint else Color(0xFFFFE6E6)) { Text(if (selected == q.correctIndex) "✓ Doğru!" else "✕ Bu kez olmadı.", color = if (selected == q.correctIndex) ModernGreen else ModernRed, fontWeight = FontWeight.Black); Spacer(Modifier.height(4.dp)); Text(q.explanation, color = ModernInk, fontSize = 13.sp); Spacer(Modifier.height(5.dp)); Text("💡 Dikkat köşesi: Bu sorunun ipucunu bir sonraki tekrarında hatırla.", color = ModernDeep, fontSize = 11.sp, fontWeight = FontWeight.Bold) } }
                item { Button(onClick = { if (index + 1 < questions.size) { index++; selected = -1 } else finished = true }, modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp), shape = RoundedCornerShape(17.dp), colors = ButtonDefaults.buttonColors(containerColor = ModernDeep)) { Text(if (index + 1 < questions.size) "Sonraki soru" else "Sonucu gör", fontWeight = FontWeight.ExtraBold) } }
            }
        }
    }
}
