package tr.yurdunubil.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private val YDeep = Color(0xFF06271F)
private val YGreen = Color(0xFF16B87A)
private val YMint = Color(0xFFDDF9EA)
private val YGold = Color(0xFFFFC857)
private val YRed = Color(0xFFE45454)
private val YBg = Color(0xFFF3F8F5)
private val YSoft = Color(0xFFEAF7F0)

@Composable
fun MajorUpgradeApp() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val store = remember { AppProgressStore(context) }
    val state by store.snapshot.collectAsStateWithLifecycle(AppProgressStore.Snapshot())
    var tab by rememberSaveable { mutableIntStateOf(0) }
    var quiz by remember { mutableStateOf<List<Question>?>(null) }
    var quizTitle by rememberSaveable { mutableStateOf("") }
    var reviewMode by rememberSaveable { mutableStateOf(false) }

    val start: (String, List<Question>, Boolean) -> Unit = { title, questions, review ->
        val safe = questions.filter { it.options.size >= 2 && it.correctIndex in it.options.indices }.distinctBy { it.id }
        if (safe.isNotEmpty()) { quizTitle = title; quiz = safe; reviewMode = review }
    }

    if (quiz != null) {
        AdaptiveQuiz(quizTitle, quiz!!, store, reviewMode) { quiz = null; reviewMode = false }
        return
    }

    Scaffold(
        containerColor = YBg,
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                listOf("Ana Sayfa", "Kütüphane", "Harita", "Arena", "Profil").forEachIndexed { i, label ->
                    val icon = listOf(Icons.Default.Home, Icons.Default.MenuBook, Icons.Default.Map, Icons.Default.SportsEsports, Icons.Default.Person)[i]
                    NavigationBarItem(
                        selected = tab == i,
                        onClick = { tab = i },
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label, fontSize = 10.sp) }
                    )
                }
            }
        }
    ) { pad ->
        Box(Modifier.fillMaxSize().padding(pad)) {
            when (tab) {
                0 -> HomeScreen(state, start)
                1 -> LibraryScreen(start)
                2 -> AtlasScreen(start)
                3 -> ArenaScreen(start)
                else -> ProfileScreen(state, start)
            }
        }
    }
}

@Composable
private fun HomeScreen(s: AppProgressStore.Snapshot, start: (String, List<Question>, Boolean) -> Unit) {
    val accuracy = if (s.solved == 0) 0 else (s.correct * 100f / s.solved).roundToInt()
    val wrong = SharedQuestionPool.all.filter { it.id in s.wrongIds }
    val smart = smartQuestions(s)
    val weak = weakestTopic(s)
    LazyColumn(contentPadding = PaddingValues(bottom = 28.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)).background(Brush.verticalGradient(listOf(YDeep, Color(0xFF0E503C)))).padding(22.dp)) {
                Column {
                    Text("YURDUNU BİL", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Black)
                    Text("KPSS Önlisans • Türkiye Coğrafyası", color = YMint)
                    Spacer(Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatPill("${s.solved}", "Soru")
                        StatPill("${s.correct}", "Doğru")
                        StatPill("${s.xp}", "XP")
                        StatPill("${s.streak}", "Seri")
                    }
                }
            }
        }
        item { SectionTitle("Bugün ne çalışmalısın?", "Uygulama performansına göre hızlı seçim") }
        item {
            Row(Modifier.padding(horizontal = 18.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ActionCard("⚡", "Hızlı 10", "Karışık KPSS", YGreen, Modifier.weight(1f)) { start("Hızlı 10", SharedQuestionPool.pick(SharedGameModes.quick), false) }
                ActionCard("🧠", "Akıllı Tekrar", "${smart.size} hedef soru", YGold, Modifier.weight(1f)) { if (smart.isNotEmpty()) start("Akıllı Tekrar", smart, true) }
            }
        }
        item { ActionCard("🔁", "Yanlışlarım", "${wrong.size} soru bekliyor", YRed, Modifier.fillMaxWidth().padding(horizontal = 18.dp)) { if (wrong.isNotEmpty()) start("Yanlışlarım", wrong.shuffled().take(15), true) } }
        item { BigCard(Brush.horizontalGradient(listOf(Color.White, YMint))) { Text("📉 ZAYIF KONUN", color = YGreen, fontWeight = FontWeight.Black); Text(weak, color = YDeep, fontSize = 22.sp, fontWeight = FontWeight.Black); Text("Bu başlıkta hata yaptıysan önce burayı güçlendir.", color = Color.Gray) } }
        item { BigCard(YDeep) { Text("🔥 ${s.streak} günlük seri", color = YGold, fontWeight = FontWeight.Black); Text("Bugün birkaç soru çöz, ritmi koru.", color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold); Spacer(Modifier.height(8.dp)); Text("Genel doğruluk: %$accuracy", color = YMint) } }
        item { BigCard(Color.White) { Text("📌 HARİTA MANTIĞI", color = YGreen, fontWeight = FontWeight.Black); Text("İl → bölge → iklim → ürün → maden bağlantısını kur.", color = YDeep, fontSize = 16.sp, fontWeight = FontWeight.Bold); Text("Ezber yerine coğrafi ilişkiyi hatırla.", color = Color.Gray, fontSize = 12.sp) } }
    }
}

@Composable private fun LibraryScreen(start: (String, List<Question>, Boolean) -> Unit) {
    var query by rememberSaveable { mutableStateOf("") }
    val topics = GeographyData.topics.filter { it.title.contains(query, true) || it.subtitle.contains(query, true) }
    LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("Kütüphane", color = YDeep, fontSize = 31.sp, fontWeight = FontWeight.Black); Text("Oku • bağlantı kur • test et", color = YGreen, fontWeight = FontWeight.Bold); Spacer(Modifier.height(8.dp)); OutlinedTextField(query, { query = it }, Modifier.fillMaxWidth(), singleLine = true, leadingIcon = { Icon(Icons.Default.Search, null) }, placeholder = { Text("Dağ, ova, maden, nüfus…") }) }
        item { BigCard(YDeep) { Text("🇹🇷 TÜRKİYE COĞRAFYA ATLASI", color = YGold, fontWeight = FontWeight.Black); Text("12 ana konu + 81 il keşfi", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black); Text("Konuya gir, mini testi başlat ve öğrendiğini pekiştir.", color = YMint) } }
        items(topics, key = { it.title }) { t ->
            BigCard(Color.White, onClick = { start(t.title, SharedQuestionPool.pick(SharedGameMode("topic-${t.title}", t.title, t.subtitle, t.icon, 10, 180, 100, SharedQuestionPool.topicForLibrary(t.title))), false) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(t.icon, fontSize = 28.sp); Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(t.title, color = YDeep, fontSize = 18.sp, fontWeight = FontWeight.Black); Text(t.subtitle, color = Color.Gray, fontSize = 12.sp); LinearProgressIndicator({ t.progress.coerceIn(0, 100) / 100f }, Modifier.fillMaxWidth().padding(top = 6.dp), color = YGreen, trackColor = YMint) }; Icon(Icons.Default.ArrowForward, null, tint = YGreen)
                }
            }
        }
        item { BigCard(YSoft) { Text("📚 Kapsam", color = YGreen, fontWeight = FontWeight.Black); Text("Yer şekilleri, su varlığı, iklim-bitki, nüfus, tarım, maden-enerji, sanayi-ulaşım, turizm, bölgeler, doğal afetler ve harita bilgisi tek akışta.", color = YDeep, fontSize = 13.sp) } }
    }
}

@Composable private fun AtlasScreen(start: (String, List<Question>, Boolean) -> Unit) {
    val regions = listOf("Tümü", "Marmara", "Ege", "Akdeniz", "İç Anadolu", "Karadeniz", "Doğu Anadolu", "Güneydoğu Anadolu")
    var region by rememberSaveable { mutableStateOf("Tümü") }
    var query by rememberSaveable { mutableStateOf("") }
    val provinces = TurkeyAtlasData.filter { (region == "Tümü" || it.region == region) && it.name.contains(query, true) }
    LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("81 İl Atlası", color = YDeep, fontSize = 31.sp, fontWeight = FontWeight.Black); Text("İl • bölge • sınav ipucu", color = YGreen, fontWeight = FontWeight.Bold); Spacer(Modifier.height(8.dp)); OutlinedTextField(query, { query = it }, Modifier.fillMaxWidth(), singleLine = true, leadingIcon = { Icon(Icons.Default.Search, null) }, placeholder = { Text("İl ara…") }) }
        item { BigCard(Brush.horizontalGradient(listOf(YDeep, Color(0xFF0C5E46)))) { Text("🗺️ ATLAS", color = YGold, fontWeight = FontWeight.Black); Text("${provinces.size} il", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Black); Text("Bölge filtresini kullan, sonra il kartından mini teste geç.", color = YMint) } }
        item { Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) { regions.forEach { r -> FilterChip(region == r, { region = r }, label = { Text(r) }) } } }
        items(provinces.chunked(2), key = { it.joinToString("-") }) { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(9.dp), modifier = Modifier.fillMaxWidth()) {
                row.forEach { p -> ProvinceCard(p, Modifier.weight(1f)) { start("${p.name} Mini Test", provinceQuestions(p), false) } }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable private fun ProvinceCard(p: AtlasProvince, modifier: Modifier, onClick: () -> Unit) {
    Card(modifier.clickable(onClick = onClick).semantics { contentDescription = "${p.name}, ${p.region}. Mini testi aç." }, colors = CardDefaults.cardColors(Color.White), shape = RoundedCornerShape(18.dp)) {
        Column(Modifier.padding(14.dp)) { Text(p.name, color = YDeep, fontWeight = FontWeight.Black); Text(p.region, color = YGreen, fontSize = 11.sp); Text(p.clue, color = Color.Gray, fontSize = 11.sp, maxLines = 3); Text("Mini test →", color = YGreen, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.padding(top = 7.dp)) }
    }
}

@Composable private fun ArenaScreen(start: (String, List<Question>, Boolean) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("Arena", color = YDeep, fontSize = 31.sp, fontWeight = FontWeight.Black); Text("Hız, bilgi ve seri", color = YGreen, fontWeight = FontWeight.Bold) }
        item { BigCard(Brush.horizontalGradient(listOf(Color(0xFF2C1D00), Color(0xFF8C6500)))) { Text("🏆 TÜRKİYE USTASI", color = YGold, fontWeight = FontWeight.Black); Text("Bugünkü skorunu geliştir.", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black); Text("Önce tek oyunculu arena; çevrim içi eşleşme altyapısı ayrıca eklenebilir.", color = Color.White.copy(.8f)) } }
        SharedGameModes.games.forEach { m -> item { GameModeCard(m, start) } }
        item { Text("Arena modları şu aşamada cihaz içi antrenman olarak çalışır; gerçek PvP/lig için sunucu taraflı maç doğrulaması gerekir.", color = Color.Gray, fontSize = 11.sp) }
    }
}

@Composable private fun GameModeCard(m: SharedGameMode, start: (String, List<Question>, Boolean) -> Unit) {
    BigCard(if (m.arena) YGold else Color.White, onClick = { start(m.title, SharedQuestionPool.pick(m), false) }) {
        Row(verticalAlignment = Alignment.CenterVertically) { Text(m.icon, fontSize = 29.sp); Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(m.title, color = YDeep, fontWeight = FontWeight.Black, fontSize = 17.sp); Text(m.subtitle, color = Color.Gray, fontSize = 11.sp); Text("${m.questions} soru • ${m.seconds} sn • +${m.rewardXp} XP", color = YGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold) }; Icon(Icons.Default.PlayArrow, null, tint = YGreen) }
    }
}

@Composable private fun ProfileScreen(s: AppProgressStore.Snapshot, start: (String, List<Question>, Boolean) -> Unit) {
    val accuracy = if (s.solved == 0) 0 else (s.correct * 100f / s.solved).roundToInt()
    val wrong = SharedQuestionPool.all.filter { it.id in s.wrongIds }
    val level = 1 + s.xp / 500
    LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("Profil & Gelişim", color = YDeep, fontSize = 31.sp, fontWeight = FontWeight.Black); Text("Seviye $level • %$accuracy doğruluk", color = YGreen, fontWeight = FontWeight.Bold) }
        item { BigCard(YDeep) { Text("SEVİYE $level", color = YGold, fontWeight = FontWeight.Black); Text("${s.xp} XP", color = Color.White, fontSize = 29.sp, fontWeight = FontWeight.Black); Text("${s.solved} soru • ${s.correct} doğru • ${s.wrong} yanlış", color = YMint) } }
        item { BigCard(Color.White) { Text("📈 KONU ANALİZİ", color = YGreen, fontWeight = FontWeight.Black); s.topicWrong.entries.sortedByDescending { it.value }.take(5).forEach { Text("${it.key}: ${it.value} yanlış", color = YDeep, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 6.dp)) } } }
        item { BigCard(YRed, onClick = { if (wrong.isNotEmpty()) start("Yanlışlarım", wrong.shuffled().take(15), true) }) { Text("🔁 YANLIŞLARIM", color = Color.White, fontWeight = FontWeight.Black); Text("${wrong.size} soru", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black); Text("Yanlış yaptıklarını tekrar çöz.", color = Color.White.copy(.85f)) } }
        item { BigCard(YGold) { Text("🏅 BAŞARIMLAR", color = Color(0xFF765500), fontWeight = FontWeight.Black); Achievement("İlk 10 soru", s.solved >= 10); Achievement("%70 doğruluk", accuracy >= 70); Achievement("50 soru", s.solved >= 50); Achievement("500 XP", s.xp >= 500); Achievement("7 günlük seri", s.streak >= 7) } }
    }
}

@Composable private fun AdaptiveQuiz(title: String, source: List<Question>, store: AppProgressStore, reviewOnly: Boolean, finish: () -> Unit) {
    val scope = rememberCoroutineScope()
    var index by rememberSaveable { mutableIntStateOf(0) }
    var selected by rememberSaveable { mutableIntStateOf(-1) }
    var correct by rememberSaveable { mutableIntStateOf(0) }
    var wrong by rememberSaveable { mutableIntStateOf(0) }
    var done by rememberSaveable { mutableStateOf(false) }
    var seconds by rememberSaveable { mutableIntStateOf((source.size * 18).coerceIn(60, 300)) }
    var persisted by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(done) {
        while (!done && seconds > 0) { kotlinx.coroutines.delay(1000); seconds-- }
        if (!done && seconds <= 0) done = true
    }
    if (done) {
        LaunchedEffect(Unit) { if (!persisted) { persisted = true; store.updateStreak() } }
        Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text("🏆", fontSize = 58.sp)
            Text("Tur tamamlandı", color = YDeep, fontSize = 28.sp, fontWeight = FontWeight.Black)
            Text("$correct / ${source.size} doğru", color = YGreen, fontSize = 23.sp, fontWeight = FontWeight.Bold)
            Text("$wrong yanlış • +${correct * 10} XP", color = Color.Gray)
            Spacer(Modifier.height(18.dp))
            Button(onClick = finish, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(YGreen)) { Text("Devam Et") }
        }
        return
    }
    val q = source[index.coerceIn(0, source.lastIndex)]
    val answered = selected >= 0
    val timerColor = if (seconds <= 20) YRed else YGreen
    LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Row(verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = finish) { Icon(Icons.Default.Close, "Çık") }; Column(Modifier.weight(1f)) { Text(title, color = YDeep, fontWeight = FontWeight.Black); Text("${index + 1} / ${source.size}", color = YGreen, fontSize = 11.sp) }; Text("⏱ ${seconds}s", color = timerColor, fontWeight = FontWeight.Black) } }
        item { LinearProgressIndicator({ (index + 1f) / source.size }, Modifier.fillMaxWidth(), color = YGreen, trackColor = YMint) }
        item { BigCard(Color.White) { Text(q.topic, color = YGreen, fontWeight = FontWeight.Black, fontSize = 11.sp); Text(q.text, color = YDeep, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold) } }
        items(q.options.indices.toList(), key = { q.id * 10 + it }) { i ->
            val isCorrect = i == q.correctIndex
            val selectedOption = i == selected
            val border by animateColorAsState(if (!answered) Color(0xFFD6E4DD) else if (isCorrect) YGreen else if (selectedOption) YRed else Color(0xFFD6E4DD), label = "answer")
            val bg = when { !answered -> Color.White; isCorrect -> YSoft; selectedOption -> Color(0xFFFFE8E8); else -> Color.White }
            Card(Modifier.fillMaxWidth().border(2.dp, border, RoundedCornerShape(16.dp)).clickable(enabled = !answered) {
                selected = i
                val ok = isCorrect
                if (ok) correct++ else wrong++
                scope.launch { store.record(q, ok) }
            }, colors = CardDefaults.cardColors(bg)) {
                Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) { Text(('A'.code + i).toChar().toString(), color = border, fontWeight = FontWeight.Black); Spacer(Modifier.width(10.dp)); Text(q.options[i], color = YDeep, fontWeight = if (selectedOption || (answered && isCorrect)) FontWeight.Bold else FontWeight.Normal) }
            }
        }
        if (answered) {
            item { AnimatedVisibility(true) { BigCard(YGold) { Text("💡 DİKKAT KÖŞESİ", color = Color(0xFF846100), fontWeight = FontWeight.Black); Text(q.explanation, color = YDeep, fontSize = 13.sp) } } }
            item { Button(onClick = { if (index < source.lastIndex) { index++; selected = -1 } else done = true }, Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(YGreen)) { Text(if (index < source.lastIndex) "Sonraki Soru →" else "Sonucu Gör") } }
        }
    }
}

private fun smartQuestions(s: AppProgressStore.Snapshot): List<Question> {
    val all = SharedQuestionPool.all
    val wrong = all.filter { it.id in s.wrongIds }
    val weakTopics = s.topicWrong.entries.sortedByDescending { it.value }.map { it.key }.take(3)
    val targeted = all.filter { it.topic in weakTopics && it.id !in s.masteredIds }
    return (wrong.shuffled() + targeted.shuffled() + all.filter { it.id !in s.seenIds }.shuffled()).distinctBy { it.id }.take(10)
}

private fun weakestTopic(s: AppProgressStore.Snapshot): String = s.topicWrong.maxByOrNull { it.value }?.key ?: "Genel Coğrafya"

private fun provinceQuestions(p: AtlasProvince): List<Question> {
    val direct = SharedQuestionPool.all.filter { it.text.contains(p.name, true) || it.explanation.contains(p.name, true) || it.topic.contains(p.region, true) }
    return (direct + SharedQuestionPool.all.filter { it.topic == "Bölgeler" || it.topic == "Coğrafi Konum" }).distinctBy { it.id }.shuffled().take(5)
}

@Composable private fun SectionTitle(title: String, subtitle: String) { Column(Modifier.padding(horizontal = 18.dp)) { Text(title, color = YDeep, fontSize = 18.sp, fontWeight = FontWeight.Black); Text(subtitle, color = Color.Gray, fontSize = 12.sp) } }
@Composable private fun StatPill(value: String, label: String) { Surface(color = Color.White.copy(.12f), shape = RoundedCornerShape(14.dp)) { Column(Modifier.padding(horizontal = 10.dp, vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text(value, color = Color.White, fontWeight = FontWeight.Black); Text(label, color = YMint, fontSize = 9.sp) } } }
@Composable private fun ActionCard(icon: String, title: String, subtitle: String, accent: Color, modifier: Modifier, onClick: () -> Unit) { Card(modifier.clickable(onClick = onClick), colors = CardDefaults.cardColors(Color.White), shape = RoundedCornerShape(18.dp)) { Column(Modifier.padding(15.dp)) { Text(icon, fontSize = 25.sp); Text(title, color = YDeep, fontWeight = FontWeight.Black); Text(subtitle, color = accent, fontSize = 11.sp, fontWeight = FontWeight.Bold) } } }
@Composable private fun BigCard(container: Any, onClick: (() -> Unit)? = null, content: @Composable ColumnScope.() -> Unit) { val brush = when (container) { is Brush -> container; is Color -> Brush.linearGradient(listOf(container, container)); else -> Brush.linearGradient(listOf(Color.White, Color.White)) }; Card(Modifier.fillMaxWidth().then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier), shape = RoundedCornerShape(21.dp), colors = CardDefaults.cardColors(Color.Transparent)) { Column(Modifier.background(brush).padding(18.dp), content = content) } }
@Composable private fun Achievement(title: String, unlocked: Boolean) { Row(Modifier.padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) { Text(if (unlocked) "✓" else "🔒", color = if (unlocked) YGreen else Color.Gray, fontWeight = FontWeight.Black); Spacer(Modifier.width(8.dp)); Text(title, color = if (unlocked) YDeep else Color.Gray) } }

private data class AtlasProvince(val name: String, val region: String, val clue: String)

private val TurkeyAtlasData = listOf(
    AtlasProvince("Adana","Akdeniz","Çukurova; pamuk, tarım ve Seyhan-Ceyhan sistemiyle ilişkilendir."), AtlasProvince("Adıyaman","Güneydoğu Anadolu","Nemrut Dağı ve Fırat havzası çevresiyle anılır."), AtlasProvince("Afyonkarahisar","Ege","İç Batı Anadolu geçişinde; termal ve ulaşım bağlantısı güçlü."), AtlasProvince("Ağrı","Doğu Anadolu","Ağrı Dağı ile öne çıkar; yüksek ve sert karasal koşullar."), AtlasProvince("Amasya","Karadeniz","Yeşilırmak vadisi; tarihî yerleşim alanlarıyla bilinir."), AtlasProvince("Ankara","İç Anadolu","İç Anadolu'nun merkezî konumu ve başkent olmasıyla öne çıkar."), AtlasProvince("Antalya","Akdeniz","Kıyı turizmi; Toroslar ve tarım alanları birlikte görülür."), AtlasProvince("Artvin","Karadeniz","Dağlık relief, yoğun yağış ve Çoruh vadisiyle dikkat çeker."), AtlasProvince("Aydın","Ege","Büyük Menderes çevresi; zeytin ve incir tarımıyla ilişkilidir."), AtlasProvince("Balıkesir","Marmara","Marmara ile Ege arasında; zeytin, tarım ve kıyı turizmi."), AtlasProvince("Bilecik","Marmara","Marmara-İç Anadolu geçişinde; yükselti ve ulaşım bağlantıları önemlidir."), AtlasProvince("Bingöl","Doğu Anadolu","Dağlık yapı; karasal iklim ve çığ/heyelan riski görülebilir."), AtlasProvince("Bitlis","Doğu Anadolu","Van Gölü çevresi ve yüksek dağlık relief."), AtlasProvince("Bolu","Karadeniz","Ormanlar ve göller; Batı Karadeniz'in geçiş alanı."), AtlasProvince("Burdur","Akdeniz","Göller Yöresi; kapalı havza özellikleri ve karstik şekiller."), AtlasProvince("Bursa","Marmara","Marmara'nın güneyi; sanayi, tarım ve Uludağ ile öne çıkar."), AtlasProvince("Çanakkale","Marmara","Boğazlar ve Ege-Marmara geçişi; tarihî ve stratejik konum."), AtlasProvince("Çankırı","İç Anadolu","İç Anadolu'nun kuzeyinde geçiş alanı; karasal iklim etkisi."), AtlasProvince("Çorum","Karadeniz","İç Karadeniz; tarım ve karasal koşulların etkisi."), AtlasProvince("Denizli","Ege","Büyük Menderes havzası; Pamukkale ve tekstil sanayisiyle bilinir."), AtlasProvince("Diyarbakır","Güneydoğu Anadolu","Geniş platolar ve karasal koşullar; tarım alanları önemlidir."), AtlasProvince("Edirne","Marmara","Trakya'nın batısı; sınır ve tarım merkezi."), AtlasProvince("Elazığ","Doğu Anadolu","Keban çevresi ve Fırat havzasıyla bağlantılı."), AtlasProvince("Erzincan","Doğu Anadolu","Fay kuşakları üzerinde; deprem riski ve yüksek vadiler."), AtlasProvince("Erzurum","Doğu Anadolu","Yüksek plato; sert karasal iklim ve hayvancılık."), AtlasProvince("Eskişehir","İç Anadolu","İç Anadolu'nun batısı; Kırka bor yataklarıyla bilinir."), AtlasProvince("Gaziantep","Güneydoğu Anadolu","Sanayi, ticaret ve Antep fıstığı ile öne çıkar."), AtlasProvince("Giresun","Karadeniz","Nemli Doğu Karadeniz; fındık üretimiyle güçlü ilişki."), AtlasProvince("Gümüşhane","Karadeniz","Dağlık iç Karadeniz; maden ve yüksek vadiler dikkat çeker."), AtlasProvince("Hakkari","Doğu Anadolu","Çok engebeli yüksek dağlık yapı; sınır kuşağı."), AtlasProvince("Hatay","Akdeniz","Doğu Akdeniz kıyısı; tarım, liman ve sınır konumu."), AtlasProvince("Isparta","Akdeniz","Göller Yöresi; gül ve elma üretimiyle bilinir."), AtlasProvince("Mersin","Akdeniz","Akdeniz kıyısı; narenciye, liman ve tarım ekonomisi."), AtlasProvince("İstanbul","Marmara","Boğazlar; nüfus, ulaşım, sanayi ve ticaret açısından çok güçlü."), AtlasProvince("İzmir","Ege","Ege kıyısı; liman, sanayi, ticaret ve kıyı turizmi."), AtlasProvince("Kars","Doğu Anadolu","Yüksek plato; sert karasal iklim ve hayvancılık."), AtlasProvince("Kastamonu","Karadeniz","Kıyı dağları ve ormanlar; yüksek yağışlı alanlar bulunur."), AtlasProvince("Kayseri","İç Anadolu","Erciyes Dağı ve sanayi-tarım etkileşimiyle öne çıkar."), AtlasProvince("Kırklareli","Marmara","Trakya; ayçiçeği ve tarım alanlarıyla bilinir."), AtlasProvince("Kırşehir","İç Anadolu","Bozkır karakteri; karasal iklim ve tarım."), AtlasProvince("Kocaeli","Marmara","İstanbul'a yakın sanayi ve ulaşım merkezi."), AtlasProvince("Konya","İç Anadolu","Geniş düzlükler; tahıl tarımı ve geniş yüzölçümü."), AtlasProvince("Kütahya","Ege","İç Batı Anadolu; maden, seramik ve termal kaynaklarla ilişkilidir."), AtlasProvince("Malatya","Doğu Anadolu","Kayısı üretimi; Fırat havzasıyla bağlantılı."), AtlasProvince("Manisa","Ege","Gediz Havzası; üzüm ve sanayi faaliyetleri."), AtlasProvince("Kahramanmaraş","Akdeniz","Akdeniz ile Güneydoğu geçişi; sanayi ve tarım."), AtlasProvince("Mardin","Güneydoğu Anadolu","Plato ve sıcak-kurak koşullar; tarihî yerleşimler."), AtlasProvince("Muğla","Ege","Koylar ve kıyı turizmi; dağlık kıyı yapısı."), AtlasProvince("Muş","Doğu Anadolu","Muş Ovası; tarım ve karasal iklim."), AtlasProvince("Nevşehir","İç Anadolu","Kapadokya; volkanik tüf ve aşınım şekilleri."), AtlasProvince("Niğde","İç Anadolu","Aladağlar ve volkanik relief; tarım ve meyvecilik."), AtlasProvince("Ordu","Karadeniz","Fındık ve nemli Doğu/Orta Karadeniz kıyı kuşağı."), AtlasProvince("Rize","Karadeniz","Çay tarımının en tipik alanlarından; çok yağışlı kıyı."), AtlasProvince("Sakarya","Marmara","Marmara'nın doğusu; tarım, sanayi ve ulaşım."), AtlasProvince("Samsun","Karadeniz","Kızılırmak ve Yeşilırmak deltaları; tarım ve ulaşım merkezi."), AtlasProvince("Siirt","Güneydoğu Anadolu","Güneydoğu'nun doğu kesimi; tarım ve geçiş iklimi."), AtlasProvince("Sinop","Karadeniz","Türkiye'nin kuzeydeki uç noktası; kıyı ve ormanlar."), AtlasProvince("Sivas","İç Anadolu","Geniş plato ve yüksek alanlar; karasal iklim."), AtlasProvince("Şanlıurfa","Güneydoğu Anadolu","Harran Ovası; sulama ve pamuk tarımıyla ilişkilidir."), AtlasProvince("Şırnak","Güneydoğu Anadolu","Dağlık sınır kuşağı; yüksek ve engebeli alanlar."), AtlasProvince("Tekirdağ","Marmara","Trakya; tarım, liman ve sanayi bağlantısı."), AtlasProvince("Tokat","Karadeniz","Yeşilırmak vadileri; tarım ve iç kesim iklim etkileri."), AtlasProvince("Trabzon","Karadeniz","Doğu Karadeniz kıyısı; yağışlı iklim, fındık/çay çevresi."), AtlasProvince("Tunceli","Doğu Anadolu","Dağlık ve engebeli; düşük nüfus yoğunluğu ile dikkat çeker."), AtlasProvince("Uşak","Ege","İç Batı Anadolu; halıcılık ve sanayi ile bilinir."), AtlasProvince("Van","Doğu Anadolu","Van Gölü; yüksek plato ve karasal iklim."), AtlasProvince("Yalova","Marmara","Marmara kıyısı; termal turizm ve geçiş konumu."), AtlasProvince("Yozgat","İç Anadolu","Bozok Platosu; karasal iklim ve tarım."), AtlasProvince("Zonguldak","Karadeniz","Taş kömürü ve maden-enerji geçmişiyle öne çıkar."), AtlasProvince("Aksaray","İç Anadolu","Tuz Gölü çevresi etkisi; kurak/yarı kurak koşullar."), AtlasProvince("Bayburt","Karadeniz","İç kesimde yüksek plato ve sert karasal koşullar."), AtlasProvince("Karaman","İç Anadolu","Kuraklık ve bozkır; tahıl ve tarım alanları."), AtlasProvince("Kırıkkale","İç Anadolu","Sanayi ve ulaşım bağlantıları; İç Anadolu geçiş alanı."), AtlasProvince("Batman","Güneydoğu Anadolu","Petrol üretimi ve rafineri faaliyetleriyle ilişkilidir."), AtlasProvince("Bolu","Karadeniz","Batı Karadeniz; orman ve göl alanları."), AtlasProvince("Ardahan","Doğu Anadolu","Çok yüksek plato; sert karasal koşullar ve hayvancılık."), AtlasProvince("Iğdır","Doğu Anadolu","Doğu Anadolu'nun daha alçak ve nispeten ılıman alanlarından."), AtlasProvince("Bartın","Karadeniz","Kıyı, orman ve kısa akarsular; Batı Karadeniz."), AtlasProvince("Karabük","Karadeniz","Demir-çelik sanayisi ve ormanlık çevreyle bilinir."), AtlasProvince("Kilis","Güneydoğu Anadolu","Sınır kuşağı; zeytin ve tarım faaliyetleri."), AtlasProvince("Osmaniye","Akdeniz","Çukurova'nın doğu çevresi; tarım ve yer fıstığı."), AtlasProvince("Düzce","Karadeniz","Batı Karadeniz kıyı kuşağı; nemli iklim ve tarım."), AtlasProvince("Batman","Güneydoğu Anadolu","Petrol ve enerji kaynaklarıyla öne çıkan il."), AtlasProvince("Bingöl","Doğu Anadolu","Dağlık arazi, karasal iklim ve çığ riski."), AtlasProvince("Mardin","Güneydoğu Anadolu","Karasal-kurak koşullar ve plato yüzeyleri."), AtlasProvince("Şanlıurfa","Güneydoğu Anadolu","GAP ve sulama sayesinde tarım çeşitliliği.")
)
