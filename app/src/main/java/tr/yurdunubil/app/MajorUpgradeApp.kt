package tr.yurdunubil.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private val UDeep = Color(0xFF05261E)
private val UGreen = Color(0xFF16B87A)
private val UMint = Color(0xFFDDF9EA)
private val UGold = Color(0xFFFFC857)
private val URed = Color(0xFFE45454)
private val UBg = Color(0xFFF3F8F5)

@Composable
fun MajorUpgradeApp() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val store = remember { AppProgressStore(context) }
    val state by store.snapshot.collectAsStateWithLifecycle(AppProgressStore.Snapshot())
    val scope = rememberCoroutineScope()
    var tab by remember { mutableIntStateOf(0) }
    var quiz by remember { mutableStateOf<List<Question>?>(null) }
    var quizTitle by remember { mutableStateOf("") }
    var reviewOnly by remember { mutableStateOf(false) }

    fun start(title: String, questions: List<Question>, review: Boolean = false) {
        if (questions.isNotEmpty()) { quizTitle = title; quiz = questions; reviewOnly = review }
    }

    if (quiz != null) {
        AdaptiveQuiz(quizTitle, quiz!!, store, reviewOnly) { quiz = null; reviewOnly = false }
        return
    }

    Scaffold(containerColor = UBg, bottomBar = {
        NavigationBar(containerColor = Color.White) {
            val labels = listOf("Ana Sayfa", "Kütüphane", "Harita", "Arena", "Profil")
            val icons = listOf(Icons.Default.Home, Icons.Default.MenuBook, Icons.Default.Map, Icons.Default.SportsEsports, Icons.Default.Person)
            labels.forEachIndexed { i, label -> NavigationBarItem(tab == i, { tab = i }, icon = { Icon(icons[i], null) }, label = { Text(label, fontSize = 10.sp) }) }
        }
    }) { pad ->
        Box(Modifier.fillMaxSize().padding(pad)) {
            when (tab) {
                0 -> UpgradeHome(state, start)
                1 -> UpgradeLibrary(start)
                2 -> UpgradeMap(start)
                3 -> UpgradeArena(start)
                4 -> UpgradeProfile(state, start)
            }
        }
    }
}

@Composable
private fun UpgradeHome(s: AppProgressStore.Snapshot, start: (String, List<Question>, Boolean) -> Unit) {
    val accuracy = if (s.solved == 0) 0 else (s.correct * 100f / s.solved).roundToInt()
    val weak = weakestTopic(s)
    val wrongQuestions = SharedQuestionPool.all.filter { it.id in s.wrongIds }
    LazyColumn(contentPadding = PaddingValues(bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { HeroHeader(s) }
        item { SectionTitle("Bugün ne yapalım?", "Kısa ama etkili bir çalışma") }
        item { Row(Modifier.padding(horizontal = 18.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ActionCard("⚡", "Hızlı 10", "10 soru", UGreen, Modifier.weight(1f)) { start("Hızlı 10", SharedQuestionPool.pick(SharedGameModes.quick), false) }
            ActionCard("🔁", "Yanlışlarım", "${wrongQuestions.size} soru", URed, Modifier.weight(1f)) { if (wrongQuestions.isNotEmpty()) start("Yanlışlarım", wrongQuestions.shuffled().take(10), true) }
        } }
        item { InsightCard(weak, s) }
        item { SectionTitle("Günün hedefi", "Serini koru ve XP kazan") }
        item { BigCard(UDeep) { Text("🔥 ${s.streak} günlük seri", color = UGold, fontWeight = FontWeight.Black); Text("Bugün 10 soru çözersen serin devam eder.", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold); Spacer(Modifier.height(12.dp)); Text("Mevcut doğruluk: %$accuracy", color = UMint) } }
        item { SectionTitle("Günün bilgisi", "Sınavda karşına çıkabilecek bağlantı") }
        item { FactCard() }
    }
}

@Composable private fun HeroHeader(s: AppProgressStore.Snapshot) {
    Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)).background(Brush.verticalGradient(listOf(UDeep, Color(0xFF0B4A38)))).padding(22.dp)) {
        Column { Text("YURDUNU BİL", color = Color.White, fontSize = 29.sp, fontWeight = FontWeight.Black); Text("KPSS Önlisans • Türkiye Coğrafyası", color = UMint); Spacer(Modifier.height(18.dp)); Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { StatPill("${s.solved}", "Soru"); StatPill("${s.xp}", "XP"); StatPill("${s.streak}", "Seri") } }
    }
}

@Composable private fun InsightCard(topic: String, s: AppProgressStore.Snapshot) {
    val errors = s.topicWrong[topic] ?: 0
    BigCard(Brush.horizontalGradient(listOf(Color.White, UMint))) { Text("🧠 ZAYIF NOKTAN", color = UGreen, fontWeight = FontWeight.Black); Text(topic, color = UDeep, fontSize = 22.sp, fontWeight = FontWeight.Black); Text(if (errors > 0) "$errors yanlışın var. Bu konuyu tekrar ederek netini yükselt." else "Henüz yeterli veri yok. Karışık testlerle profilini oluşturalım.", color = Color.Gray) }
}

@Composable private fun UpgradeLibrary(start: (String, List<Question>, Boolean) -> Unit) {
    var search by remember { mutableStateOf("") }
    val topics = GeographyData.topics.filter { it.title.contains(search, true) || it.subtitle.contains(search, true) }
    LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("Kütüphane", color = UDeep, fontSize = 31.sp, fontWeight = FontWeight.Black); Text("Oku • bağlantı kur • soru çöz", color = UGreen, fontWeight = FontWeight.Bold); Spacer(Modifier.height(8.dp)); OutlinedTextField(search, { search = it }, Modifier.fillMaxWidth(), singleLine = true, leadingIcon = { Icon(Icons.Default.Search, null) }, placeholder = { Text("Dağ, maden, tarım, nüfus…") }) }
        item { BigCard(UDeep) { Text("🇹🇷 TÜRKİYE COĞRAFYA ATLASI", color = UGold, fontWeight = FontWeight.Black); Text("12 ana başlık + il/bölge keşfi", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold); Text("Konuya gir, ardından doğrudan test başlat.", color = UMint) } }
        items(topics) { t -> BigCard(UGreen, onClick = { start(t.title, SharedQuestionPool.pick(SharedGameMode("topic", t.title, t.subtitle, t.icon, 10, 180, 100, SharedQuestionPool.topicForLibrary(t.title))), false) }) { Row(verticalAlignment = Alignment.CenterVertically) { Text(t.icon, fontSize = 30.sp); Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(t.title, color = UDeep, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold); Text(t.subtitle, color = Color.Gray, fontSize = 12.sp); LinearProgressIndicator({ t.progress / 100f }, Modifier.fillMaxWidth().padding(top = 7.dp), color = UGreen, trackColor = UMint) }; Icon(Icons.Default.ArrowForward, null, tint = UGreen) } } }
    }
}

@Composable private fun UpgradeMap(start: (String, List<Question>, Boolean) -> Unit) {
    val regions = listOf("Marmara", "Ege", "Akdeniz", "İç Anadolu", "Karadeniz", "Doğu Anadolu", "Güneydoğu Anadolu")
    var selectedRegion by remember { mutableStateOf<String?>(null) }
    var query by remember { mutableStateOf("") }
    val provinces = all81Stable().filter { (selectedRegion == null || it.region == selectedRegion) && it.name.contains(query, true) }
    LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("81 İl Atlası", color = UDeep, fontSize = 31.sp, fontWeight = FontWeight.Black); Text("Bölgeyi seç, illeri keşfet", color = UGreen, fontWeight = FontWeight.Bold); Spacer(Modifier.height(8.dp)); OutlinedTextField(query, { query = it }, Modifier.fillMaxWidth(), singleLine = true, leadingIcon = { Icon(Icons.Default.Search, null) }, placeholder = { Text("İl ara…") }) }
        item { BigCard(UDeep) { Text("🗺️ HARİTA KEŞİF", color = UGold, fontWeight = FontWeight.Black); Text("${provinces.size} il gösteriliyor", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black); Text("İl kartına dokunarak sınav ipucunu ve bilgileri aç.", color = UMint) } }
        item { LazyRow(horizontalArrangement = Arrangement.spacedBy(7.dp)) { item { FilterChip(selectedRegion == null, { selectedRegion = null }, label = { Text("Tümü") }) }; regions.forEach { r -> item { FilterChip(selectedRegion == r, { selectedRegion = r }, label = { Text(r) }) } } } }
        items(provinces.chunked(2)) { row -> Row(horizontalArrangement = Arrangement.spacedBy(9.dp), modifier = Modifier.fillMaxWidth()) { row.forEach { p -> ProvinceCard(p, Modifier.weight(1f)) { start("${p.name} • Mini Test", SharedQuestionPool.pick(SharedGameMode("province", p.name, p.region, "📍", 5, 90, 50)), false) } }; if (row.size == 1) Spacer(Modifier.weight(1f)) } }
    }
}

@Composable private fun ProvinceCard(p: Province, modifier: Modifier, onClick: () -> Unit) { Card(modifier.clickable(onClick = onClick), colors = CardDefaults.cardColors(Color.White), shape = RoundedCornerShape(18.dp)) { Column(Modifier.padding(14.dp)) { Text(p.name, color = UDeep, fontWeight = FontWeight.Black); Text(p.region, color = UGreen, fontSize = 11.sp); Text(p.clue, color = Color.Gray, fontSize = 11.sp, maxLines = 2); Text("Test →", color = UGreen, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.padding(top = 8.dp)) } } }

@Composable private fun UpgradeArena(start: (String, List<Question>, Boolean) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(11.dp)) {
        item { Text("Arena", color = UDeep, fontSize = 31.sp, fontWeight = FontWeight.Black); Text("Kendinle yarış • skorunu geliştir", color = UGreen, fontWeight = FontWeight.Bold) }
        item { BigCard(Brush.horizontalGradient(listOf(Color(0xFF332100), Color(0xFF8C6500)))) { Text("🏆 SEZON HEDEFİ", color = UGold, fontWeight = FontWeight.Black); Text("Türkiye Ustası", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Black); Text("Zor sorular + süre baskısı + yüksek XP", color = Color.White.copy(.8f)) } }
        SharedGameModes.arenaModes.forEach { m -> item { BigCard(UGold, onClick = { start(m.title, SharedQuestionPool.pick(m), false) }) { Row(verticalAlignment = Alignment.CenterVertically) { Text(m.icon, fontSize = 31.sp); Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(m.title, color = UDeep, fontSize = 18.sp, fontWeight = FontWeight.Black); Text(m.subtitle, color = Color.Gray); Text("${m.questions} soru • ${m.seconds} sn • +${m.rewardXp} XP", color = Color(0xFF856000), fontWeight = FontWeight.Bold, fontSize = 11.sp) }; Icon(Icons.Default.PlayArrow, null, tint = UDeep) } } } }
        item { Text("Not: Bu sürümde Arena modları cihaz içi skor/alıştırma olarak çalışır. Gerçek zamanlı rakip eşleştirme ve lig için güvenli Supabase maç/RPC katmanı ayrıca gerekir.", color = Color.Gray, fontSize = 11.sp, modifier = Modifier.padding(5.dp)) }
    }
}

@Composable private fun UpgradeProfile(s: AppProgressStore.Snapshot, start: (String, List<Question>, Boolean) -> Unit) {
    val wrong = SharedQuestionPool.all.filter { it.id in s.wrongIds }
    val weak = weakestTopic(s)
    val accuracy = if (s.solved == 0) 0 else (s.correct * 100f / s.solved).roundToInt()
    LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("Profil & Gelişim", color = UDeep, fontSize = 31.sp, fontWeight = FontWeight.Black); Text("Hangi konularda güçlenmen gerektiğini gör.", color = UGreen, fontWeight = FontWeight.Bold) }
        item { BigCard(UDeep) { Text("SEVİYE ${1 + s.xp / 500}", color = UGold, fontWeight = FontWeight.Black); Text("${s.xp} XP", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black); Text("%$accuracy doğruluk • ${s.solved} soru", color = UMint) } }
        item { BigCard(Color.White) { Text("📉 ZAYIF KONU", color = URed, fontWeight = FontWeight.Black); Text(weak, color = UDeep, fontSize = 21.sp, fontWeight = FontWeight.Black); Text("Yanlışların bu başlıkta yoğunlaşıyorsa ilk tekrarın burada olsun.", color = Color.Gray) } }
        item { BigCard(URed, onClick = { if (wrong.isNotEmpty()) start("Yanlışlarım", wrong.shuffled().take(15), true) }) { Text("🔁 YANLIŞLARIM", color = Color.White, fontWeight = FontWeight.Black); Text("${wrong.size} soru tekrar bekliyor", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black); Text("Yanlış yaptığın soruları tekrar çöz.", color = Color.White.copy(.8f)) } }
        item { BigCard(UGold) { Text("🏅 BAŞARIMLAR", color = Color(0xFF765500), fontWeight = FontWeight.Black); Achievement("İlk 10 soru", s.solved >= 10); Achievement("%70 doğruluk", accuracy >= 70); Achievement("50 soru", s.solved >= 50); Achievement("500 XP", s.xp >= 500); Achievement("7 günlük seri", s.streak >= 7) } }
    }
}

@Composable private fun AdaptiveQuiz(title: String, source: List<Question>, store: AppProgressStore, reviewOnly: Boolean, finish: () -> Unit) {
    val scope = rememberCoroutineScope()
    var index by remember { mutableIntStateOf(0) }
    var selected by remember { mutableIntStateOf(-1) }
    var correct by remember { mutableIntStateOf(0) }
    var wrong by remember { mutableIntStateOf(0) }
    var done by remember { mutableStateOf(false) }
    var secondsLeft by remember { mutableIntStateOf((source.size * 18).coerceIn(60, 300)) }
    LaunchedEffect(index, selected, done) { if (!done && selected == -1 && secondsLeft > 0) { kotlinx.coroutines.delay(1000); secondsLeft-- } }
    if (done || secondsLeft == 0) {
        Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(if (secondsLeft == 0) "⏱️" else "🏆", fontSize = 60.sp); Text("Tur tamamlandı", color = UDeep, fontSize = 29.sp, fontWeight = FontWeight.Black); Text("$correct / ${source.size} doğru", color = UGreen, fontSize = 22.sp, fontWeight = FontWeight.Black); Text("$wrong yanlış • ${correct * 10} XP", color = Color.Gray); Spacer(Modifier.height(16.dp)); Button({ finish() }, Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(UGreen)) { Text("Devam Et") }
        }
        return
    }
    val q = source[index]
    LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Row(verticalAlignment = Alignment.CenterVertically) { IconButton(finish) { Icon(Icons.Default.Close, "Çık") }; Column(Modifier.weight(1f)) { Text(title, color = UDeep, fontWeight = FontWeight.Black); Text("${index + 1}/${source.size} • ${secondsLeft}s", color = UGreen, fontSize = 11.sp) } } }
        item { LinearProgressIndicator({ (index + 1f) / source.size }, Modifier.fillMaxWidth(), color = UGreen, trackColor = UMint) }
        item { BigCard(Color.White) { Text(q.topic.uppercase(), color = UGreen, fontSize = 10.sp, fontWeight = FontWeight.Black); Text(q.text, color = UDeep, fontSize = 20.sp, fontWeight = FontWeight.Black) } }
        items(q.options.size) { i ->
            val isCorrect = i == q.correctIndex; val isSelected = i == selected
            val border by animateColorAsState(if (selected == -1) Color(0xFFDDE8E2) else if (isCorrect) UGreen else if (isSelected) URed else Color(0xFFDDE8E2), label = "answerBorder")
            Card(Modifier.fillMaxWidth().border(2.dp, border, RoundedCornerShape(17.dp)).clickable(enabled = selected == -1) { selected = i; if (isCorrect) correct++ else wrong++; scope.launch { store.record(q, isCorrect); store.updateStreak() } }, colors = CardDefaults.cardColors(if (selected != -1 && isCorrect) UMint else if (selected != -1 && isSelected) Color(0xFFFFE7E7) else Color.White)) { Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Text(('A'.code + i).toChar().toString(), color = border, fontWeight = FontWeight.Black); Spacer(Modifier.width(11.dp)); Text(q.options[i], color = UDeep, fontWeight = if (isSelected || (selected != -1 && isCorrect)) FontWeight.Bold else FontWeight.Normal) } }
        }
        item { AnimatedVisibility(selected != -1) { BigCard(UGold) { Text("💡 DİKKAT KÖŞESİ", color = Color(0xFF7A5900), fontWeight = FontWeight.Black); Text(q.explanation, color = UDeep, fontSize = 13.sp); Spacer(Modifier.height(8.dp)); Text(if (selected == q.correctIndex) "✓ Bu bağlantıyı koru." else "↻ Bu soruyu yanlışlar listesine ekledik.", color = UDeep, fontWeight = FontWeight.Bold) } } }
        item { if (selected != -1) Button({ if (index == source.lastIndex) done = true else { index++; selected = -1 } }, Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(UGreen)) { Text(if (index == source.lastIndex) "Sonucu Gör" else "Sonraki Soru") } }
    }
}

@Composable private fun BigCard(container: Any, onClick: (() -> Unit)? = null, content: @Composable ColumnScope.() -> Unit) { val bg = when (container) { is Color -> container; is Brush -> Color.Transparent; else -> Color.White }; val brush = container as? Brush; Card(Modifier.fillMaxWidth().then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier), shape = RoundedCornerShape(21.dp), colors = CardDefaults.cardColors(bg)) { if (brush != null) Box(Modifier.fillMaxWidth().background(brush).padding(18.dp)) { Column(content = content) } else Column(Modifier.padding(18.dp), content = content) } }
@Composable private fun ActionCard(icon: String, title: String, subtitle: String, color: Color, modifier: Modifier, onClick: () -> Unit) { Card(modifier.clickable(onClick = onClick), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color.White)) { Column(Modifier.padding(15.dp)) { Text(icon, fontSize = 27.sp); Text(title, color = UDeep, fontWeight = FontWeight.Black); Text(subtitle, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold) } } }
@Composable private fun SectionTitle(title: String, subtitle: String) { Column(Modifier.padding(horizontal = 18.dp)) { Text(title, color = UDeep, fontSize = 20.sp, fontWeight = FontWeight.Black); Text(subtitle, color = Color.Gray, fontSize = 11.sp) } }
@Composable private fun StatPill(value: String, label: String) { Surface(shape = RoundedCornerShape(14.dp), color = Color.White.copy(.12f)) { Column(Modifier.padding(horizontal = 13.dp, vertical = 8.dp)) { Text(value, color = Color.White, fontWeight = FontWeight.Black); Text(label, color = UMint, fontSize = 10.sp) } } }
@Composable private fun Achievement(name: String, ok: Boolean) { Text(if (ok) "✓ $name" else "○ $name", color = if (ok) UDeep else Color.Gray, fontWeight = if (ok) FontWeight.Bold else FontWeight.Normal, modifier = Modifier.padding(top = 6.dp)) }
@Composable private fun FactCard() { BigCard(UGreen) { Text("📌 HARİTA MANTIĞI", color = UDeep, fontWeight = FontWeight.Black); Text("Türkiye'de dağların uzanışı; iklim, ulaşım ve kıyı-iç kesim ilişkilerini birlikte düşünmeyi sağlar.", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold); Text("Soru çözerken tek bilgiyi değil, bilgilerin birbirine etkisini düşün.", color = UMint, fontSize = 12.sp) } }
private fun weakestTopic(s: AppProgressStore.Snapshot): String = (s.topicWrong.entries.maxByOrNull { it.value }?.key ?: GeographyData.topics.firstOrNull()?.title ?: "Genel Coğrafya")
