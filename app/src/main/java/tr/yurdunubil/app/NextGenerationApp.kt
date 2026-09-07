package tr.yurdunubil.app

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private val NDeep = Color(0xFF05261E)
private val NGreen = Color(0xFF16B87A)
private val NMint = Color(0xFFDDF9EA)
private val NGold = Color(0xFFFFC857)
private val NRed = Color(0xFFE45454)
private val NBg = Color(0xFFF3F8F5)
private val NCard = Color.White

@Composable
fun NextGenerationApp() {
    val context = LocalContext.current
    val activity = context as? Activity
    val store = remember { AppProgressStore(context) }
    val state by store.snapshot.collectAsStateWithLifecycle(AppProgressStore.Snapshot())
    val scope = rememberCoroutineScope()
    var tab by rememberSaveable { mutableIntStateOf(0) }
    var quiz by remember { mutableStateOf<List<Question>?>(null) }
    var quizTitle by rememberSaveable { mutableStateOf("") }
    var quizReview by rememberSaveable { mutableStateOf(false) }
    var showNotifications by rememberSaveable { mutableStateOf(false) }
    var showEvents by rememberSaveable { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) YurdunuBilNotifications.scheduleDaily(context)
    }
    LaunchedEffect(Unit) {
        YurdunuBilNotifications.ensureChannels(context)
        if (android.os.Build.VERSION.SDK_INT < 33 || context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            YurdunuBilNotifications.scheduleDaily(context)
        } else {
            activity?.let { permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) }
        }
    }

    val launchQuiz: (String, List<Question>, Boolean) -> Unit = { title, list, review ->
        val safe = list.filter { it.options.size >= 2 && it.correctIndex in it.options.indices }.distinctBy { it.id }
        if (safe.isNotEmpty()) { quizTitle = title; quiz = safe; quizReview = review }
    }

    if (quiz != null) {
        NextGenQuiz(quizTitle, quiz!!, store, quizReview) { quiz = null; quizReview = false }
        return
    }

    Scaffold(containerColor = NBg, bottomBar = {
        NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
            val labels = listOf("Ana Sayfa", "Kütüphane", "Harita", "Arena", "Profil")
            val icons = listOf(Icons.Default.Home, Icons.Default.MenuBook, Icons.Default.Map, Icons.Default.SportsEsports, Icons.Default.Person)
            labels.forEachIndexed { i, label ->
                NavigationBarItem(tab == i, { tab = i }, icon = { Icon(icons[i], label) }, label = { Text(label, fontSize = 10.sp) })
            }
        }
    }) { pad ->
        Box(Modifier.fillMaxSize().padding(pad)) {
            AnimatedContent(tab, label = "main-tab") { selected ->
                when (selected) {
                    0 -> NextHome(state, launchQuiz, { showEvents = true }, { showNotifications = true })
                    1 -> NextLibrary(launchQuiz)
                    2 -> NextAtlas(launchQuiz)
                    3 -> NextArena(launchQuiz)
                    else -> NextProfile(state, launchQuiz)
                }
            }
        }
    }

    if (showEvents) EventSheet(onDismiss = { showEvents = false }, onPractice = { showEvents = false; launchQuiz("Etkinlik: Türkiye Coğrafya Sprinti", SharedQuestionPool.all.shuffled().take(10), false) })
    if (showNotifications) NotificationCenter(onDismiss = { showNotifications = false }, state = state)
}

@Composable
private fun NextHome(s: AppProgressStore.Snapshot, start: (String, List<Question>, Boolean) -> Unit, openEvents: () -> Unit, openNotifications: () -> Unit) {
    val accuracy = if (s.solved == 0) 0 else (s.correct * 100f / s.solved).roundToInt()
    val wrong = SharedQuestionPool.all.filter { it.id in s.wrongIds }
    val smart = smartQuestions(s)
    val level = 1 + s.xp / 500
    LazyColumn(contentPadding = PaddingValues(bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(bottomStart = 34.dp, bottomEnd = 34.dp)).background(Brush.verticalGradient(listOf(NDeep, Color(0xFF0A513C)))).padding(22.dp)) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) { Text("YURDUNU BİL", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Black); Text("KPSS Önlisans • Türkiye Coğrafyası", color = NMint, fontSize = 13.sp) }
                        IconButton(onClick = openNotifications) { Icon(Icons.Default.Notifications, "Bildirimler", tint = Color.White) }
                    }
                    Spacer(Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { StatChip("SV ${level}", NGold); StatChip("${s.xp} XP", NMint); StatChip("🔥 ${s.streak}", Color.White.copy(.16f)) }
                }
            }
        }
        item { ActionStrip(start, smart, wrong) }
        item {
            Card(colors = CardDefaults.cardColors(NCard), shape = RoundedCornerShape(22.dp), modifier = Modifier.padding(horizontal = 18.dp)) {
                Column(Modifier.padding(17.dp)) {
                    Text("🎯 BUGÜNÜN PLANI", color = NGreen, fontWeight = FontWeight.Black)
                    Text("10 soru • %70 hedef", color = NDeep, fontWeight = FontWeight.ExtraBold, fontSize = 19.sp)
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator({ (s.solved % 10) / 10f }, Modifier.fillMaxWidth(), color = NGreen, trackColor = NMint)
                    Text("Bugün ${s.solved % 10}/10 soru tamamlandı", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(top = 7.dp))
                }
            }
        }
        item { InsightCard(s, smart) }
        item { EventBanner(openEvents) }
        item { FactBlock() }
        item { Text("Genel doğruluk: %$accuracy • ${s.correct} doğru / ${s.wrong} yanlış", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 18.dp)) }
    }
}

@Composable
private fun ActionStrip(start: (String, List<Question>, Boolean) -> Unit, smart: List<Question>, wrong: List<Question>) {
    Row(Modifier.padding(horizontal = 18.dp), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
        QuickCard("⚡", "Hızlı 10", "Karışık", NGreen, Modifier.weight(1f)) { start("Hızlı 10", SharedQuestionPool.all.shuffled().take(10), false) }
        QuickCard("🧠", "Akıllı", "${smart.size} hedef", NGold, Modifier.weight(1f)) { if (smart.isNotEmpty()) start("Akıllı Tekrar", smart, true) }
        QuickCard("🔁", "Yanlış", "${wrong.size}", NRed, Modifier.weight(1f)) { if (wrong.isNotEmpty()) start("Yanlışlarım", wrong.shuffled().take(15), true) }
    }
}

@Composable private fun InsightCard(s: AppProgressStore.Snapshot, smart: List<Question>) {
    val weak = weakestTopic(s)
    Card(colors = CardDefaults.cardColors(NCard), shape = RoundedCornerShape(22.dp), modifier = Modifier.padding(horizontal = 18.dp)) {
        Row(Modifier.padding(17.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(45.dp).clip(CircleShape).background(NMint), contentAlignment = Alignment.Center) { Text("🧠", fontSize = 23.sp) }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) { Text("ZAYIF NOKTA", color = NGreen, fontSize = 11.sp, fontWeight = FontWeight.Black); Text(weak, color = NDeep, fontWeight = FontWeight.Black, fontSize = 18.sp); Text(if (smart.isNotEmpty()) "Akıllı tekrar hazır." else "Daha fazla soru çözerek analizini güçlendir.", color = Color.Gray, fontSize = 12.sp) }
            Text("→", color = NGreen, fontSize = 23.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable private fun EventBanner(openEvents: () -> Unit) {
    Card(colors = CardDefaults.cardColors(NDeep), shape = RoundedCornerShape(22.dp), modifier = Modifier.padding(horizontal = 18.dp).clickable(onClick = openEvents)) {
        Row(Modifier.padding(17.dp), verticalAlignment = Alignment.CenterVertically) { Text("🏆", fontSize = 34.sp); Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text("CANLI ETKİNLİK", color = NGold, fontWeight = FontWeight.Black, fontSize = 11.sp); Text("Türkiye Coğrafya Sprinti", color = Color.White, fontWeight = FontWeight.Black, fontSize = 19.sp); Text("Süreli 10 soru • ekstra XP", color = NMint, fontSize = 12.sp) }; Text("Aç →", color = NGold, fontWeight = FontWeight.Bold) }
    }
}

@Composable private fun NextLibrary(start: (String, List<Question>, Boolean) -> Unit) {
    var query by rememberSaveable { mutableStateOf("") }
    val topics = GeographyData.topics.filter { it.title.contains(query, true) || it.subtitle.contains(query, true) }
    LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("Kütüphane", color = NDeep, fontSize = 31.sp, fontWeight = FontWeight.Black); Text("Öğren → bağlantı kur → test et", color = NGreen, fontWeight = FontWeight.Bold); Spacer(Modifier.height(8.dp)); OutlinedTextField(query, { query = it }, Modifier.fillMaxWidth(), singleLine = true, leadingIcon = { Icon(Icons.Default.Search, null) }, placeholder = { Text("Dağ, ova, akarsu, maden…") }) }
        item { FeatureCard("🇹🇷", "Türkiye Coğrafya Atlası", "12 ana başlık + soru havuzu", NDeep) { start("Türkiye Geneli", SharedQuestionPool.all.shuffled().take(15), false) } }
        items(topics, key = { it.title }) { t ->
            FeatureCard(t.icon, t.title, t.subtitle, NGreen) { start(t.title, SharedQuestionPool.pick(SharedGameMode("topic", t.title, t.subtitle, t.icon, 10, 180, 100, SharedQuestionPool.topicForLibrary(t.title))), false) }
        }
    }
}

@Composable private fun NextAtlas(start: (String, List<Question>, Boolean) -> Unit) {
    val regions = listOf("Tümü", "Marmara", "Ege", "Akdeniz", "İç Anadolu", "Karadeniz", "Doğu Anadolu", "Güneydoğu Anadolu")
    var region by rememberSaveable { mutableStateOf("Tümü") }
    var query by rememberSaveable { mutableStateOf("") }
    var selected by remember { mutableStateOf<AtlasProvince?>(null) }
    val provinces = TurkeyAtlasData.filter { (region == "Tümü" || it.region == region) && it.name.contains(query, true) }
    LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("Türkiye Haritası", color = NDeep, fontSize = 31.sp, fontWeight = FontWeight.Black); Text("81 ili keşfet • konum bağlantısı kur", color = NGreen, fontWeight = FontWeight.Bold); Spacer(Modifier.height(8.dp)); OutlinedTextField(query, { query = it }, Modifier.fillMaxWidth(), singleLine = true, leadingIcon = { Icon(Icons.Default.Search, null) }, placeholder = { Text("İl ara…") }) }
        item { LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) { regions.forEach { r -> FilterChip(region == r, { region = r }, label = { Text(r) }) } } }
        item { Card(colors = CardDefaults.cardColors(NDeep), shape = RoundedCornerShape(22.dp)) { Column(Modifier.padding(17.dp)) { Text("🗺️ ${provinces.size} İL", color = NGold, fontWeight = FontWeight.Black); Text("İl seç, ipucunu gör, sonra mini teste geç.", color = Color.White, fontWeight = FontWeight.Bold) } } }
        items(provinces, key = { it.name }) { p ->
            Card(colors = CardDefaults.cardColors(NCard), shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth().clickable { selected = p }) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(42.dp).clip(CircleShape).background(NMint), contentAlignment = Alignment.Center) { Text("📍") }; Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(p.name, color = NDeep, fontWeight = FontWeight.Black); Text(p.region, color = NGreen, fontSize = 11.sp); Text(p.clue, color = Color.Gray, fontSize = 11.sp, maxLines = 2) }; Icon(Icons.Default.ChevronRight, null, tint = NGreen) }
            }
        }
    }
    selected?.let { p ->
        AlertDialog(onDismissRequest = { selected = null }, title = { Text(p.name, fontWeight = FontWeight.Black) }, text = { Column { Text(p.region, color = NGreen, fontWeight = FontWeight.Bold); Spacer(Modifier.height(8.dp)); p.facts.take(5).forEach { Text("• $it", fontSize = 13.sp, modifier = Modifier.padding(vertical = 2.dp)) } } }, confirmButton = { Button(onClick = { selected = null; start("${p.name} Mini Test", provinceQuestions(p), false) }, colors = ButtonDefaults.buttonColors(NGreen)) { Text("Mini Test") } }, dismissButton = { TextButton(onClick = { selected = null }) { Text("Kapat") } })
    }
}

@Composable private fun NextArena(start: (String, List<Question>, Boolean) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(11.dp)) {
        item { Text("Arena", color = NDeep, fontSize = 31.sp, fontWeight = FontWeight.Black); Text("Tek oyunculu rekabet + online altyapıya hazır", color = NGreen, fontWeight = FontWeight.Bold) }
        item { Card(colors = CardDefaults.cardColors(BrushCard(NDeep)), shape = RoundedCornerShape(24.dp)) { Column(Modifier.padding(20.dp)) { Text("🏆 SEZON 1", color = NGold, fontWeight = FontWeight.Black); Text("Türkiye Ustası", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Black); Text("Hızını artır, doğru cevapları seri hâline getir.", color = NMint); Spacer(Modifier.height(10.dp)); Text("Online PvP ve lig sistemi için güvenli Supabase maç katmanı hazırlandı; istemci tarafı skor hilesine güvenmez.", color = Color.White.copy(.75f), fontSize = 11.sp) } } }
        SharedGameModes.games.forEach { m -> item { FeatureCard(m.icon, m.title, "${m.subtitle} • ${m.questions} soru • ${m.seconds} sn", if (m.arena) NGold else NCard) { start(m.title, SharedQuestionPool.pick(m), false) } } }
        item { FeatureCard("⚔️", "Online Düello", "Eşleşme kuyruğu • rating • maç odası", NGold) { start("Online Düello Ön Isınma", SharedQuestionPool.pick(SharedGameModes.arenaModes.first()), false) } }
    }
}

@Composable private fun NextProfile(s: AppProgressStore.Snapshot, start: (String, List<Question>, Boolean) -> Unit) {
    val accuracy = if (s.solved == 0) 0 else (s.correct * 100f / s.solved).roundToInt()
    val level = 1 + s.xp / 500
    val wrong = SharedQuestionPool.all.filter { it.id in s.wrongIds }
    val weakEntries = s.topicWrong.entries.sortedByDescending { it.value }.take(6)
    LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("Profil & Gelişim", color = NDeep, fontSize = 31.sp, fontWeight = FontWeight.Black); Text("Seviye $level • %$accuracy doğruluk", color = NGreen, fontWeight = FontWeight.Bold) }
        item { Card(colors = CardDefaults.cardColors(NDeep), shape = RoundedCornerShape(24.dp)) { Column(Modifier.padding(20.dp)) { Text("SEVİYE $level", color = NGold, fontWeight = FontWeight.Black); Text("${s.xp} XP", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Black); Text("${s.solved} soru • ${s.correct} doğru • ${s.wrong} yanlış", color = NMint); Spacer(Modifier.height(10.dp)); val progress = (s.xp % 500) / 500f; LinearProgressIndicator({ progress }, Modifier.fillMaxWidth(), color = NGold, trackColor = Color.White.copy(.12f)); Text("Sonraki seviyeye ${500 - (s.xp % 500)} XP", color = Color.White.copy(.7f), fontSize = 11.sp, modifier = Modifier.padding(top = 6.dp)) } } }
        item { Card(colors = CardDefaults.cardColors(NCard), shape = RoundedCornerShape(22.dp)) { Column(Modifier.padding(17.dp)) { Text("📊 KONU ANALİZİ", color = NGreen, fontWeight = FontWeight.Black); if (weakEntries.isEmpty()) Text("Henüz yeterli veri yok.", color = Color.Gray) else weakEntries.forEach { Text("${it.key}: ${it.value} yanlış", color = NDeep, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 7.dp)) } } } }
        item { FeatureCard("🔁", "Yanlışlarım", "${wrong.size} soru • tekrar et ve temizle", NRed) { if (wrong.isNotEmpty()) start("Yanlışlarım", wrong.shuffled().take(15), true) } }
        item { Text("🏅 Başarımlar", color = NDeep, fontSize = 18.sp, fontWeight = FontWeight.Black) }
        item { AchievementGrid(s, accuracy) }
    }
}

@Composable private fun AchievementGrid(s: AppProgressStore.Snapshot, accuracy: Int) {
    val items = listOf("İlk 10 soru" to (s.solved >= 10), "%70 doğruluk" to (accuracy >= 70), "50 soru" to (s.solved >= 50), "500 XP" to (s.xp >= 500), "7 günlük seri" to (s.streak >= 7), "100 soru" to (s.solved >= 100))
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { items.chunked(2).forEach { row -> Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { row.forEach { (title, earned) -> Card(Modifier.weight(1f), colors = CardDefaults.cardColors(if (earned) NMint else Color.White), shape = RoundedCornerShape(16.dp)) { Row(Modifier.padding(13.dp), verticalAlignment = Alignment.CenterVertically) { Text(if (earned) "🏆" else "🔒", fontSize = 20.sp); Spacer(Modifier.width(8.dp)); Text(title, color = if (earned) NDeep else Color.Gray, fontWeight = FontWeight.Bold, fontSize = 12.sp) } } }; if (row.size == 1) Spacer(Modifier.weight(1f)) } } }
}

@Composable private fun FeatureCard(icon: String, title: String, subtitle: String, accent: Color, onClick: () -> Unit) {
    Card(colors = CardDefaults.cardColors(if (accent == NCard) NCard else accent.copy(alpha = .10f)), shape = RoundedCornerShape(19.dp), modifier = Modifier.fillMaxWidth().animateContentSize().clickable(onClick = onClick)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Text(icon, fontSize = 29.sp); Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(title, color = NDeep, fontSize = 17.sp, fontWeight = FontWeight.Black); Text(subtitle, color = Color.Gray, fontSize = 12.sp) }; Icon(Icons.Default.ChevronRight, null, tint = NGreen) }
    }
}

@Composable private fun QuickCard(icon: String, title: String, sub: String, accent: Color, modifier: Modifier, onClick: () -> Unit) {
    val scale by animateFloatAsState(1f, label = title)
    Card(modifier.scale(scale).then(modifier).clickable(onClick = onClick), colors = CardDefaults.cardColors(accent.copy(alpha = .12f)), shape = RoundedCornerShape(18.dp)) { Column(Modifier.padding(12.dp)) { Text(icon, fontSize = 24.sp); Text(title, color = NDeep, fontWeight = FontWeight.Black, fontSize = 13.sp); Text(sub, color = Color.Gray, fontSize = 10.sp) } }
}

@Composable private fun StatChip(text: String, color: Color) { Surface(color = color.copy(alpha = .16f), shape = RoundedCornerShape(14.dp)) { Text(text, color = Color.White, fontWeight = FontWeight.Black, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp)) } }

@Composable private fun FactBlock() { val fact = CurrentFactFeed.facts.firstOrNull(); Card(colors = CardDefaults.cardColors(NCard), shape = RoundedCornerShape(20.dp), modifier = Modifier.padding(horizontal = 18.dp)) { Column(Modifier.padding(17.dp)) { Text("📌 GÜNÜN BİLGİSİ", color = NGreen, fontWeight = FontWeight.Black); Text(fact?.title ?: "Türkiye coğrafyasında bağlantı kurarak öğren.", color = NDeep, fontWeight = FontWeight.Black, fontSize = 17.sp); Text(fact?.body ?: "Harita mantığıyla çalış.", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(top = 6.dp)) } } }

@Composable private fun EventSheet(onDismiss: () -> Unit, onPractice: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss) { Column(Modifier.fillMaxWidth().padding(20.dp)) { Text("🏆 Etkinlik Merkezi", color = NDeep, fontSize = 25.sp, fontWeight = FontWeight.Black); Text("Bugünün etkinlikleri ve ödüller", color = NGreen, fontWeight = FontWeight.Bold); Spacer(Modifier.height(16.dp)); FeatureCard("⚡", "Türkiye Coğrafya Sprinti", "10 soru • süreli • bonus XP", NGold, onPractice); FeatureCard("🧭", "Harita Avı", "İl-bölge bağlantılarını hızla bul", NGreen, onPractice); FeatureCard("🔥", "Seri Gecesi", "Arka arkaya doğru cevap bonusu", NRed, onPractice); Spacer(Modifier.height(14.dp)) } }
}

@Composable private fun NotificationCenter(onDismiss: () -> Unit, state: AppProgressStore.Snapshot) {
    ModalBottomSheet(onDismissRequest = onDismiss) { Column(Modifier.fillMaxWidth().padding(20.dp)) { Text("🔔 Bildirimler", color = NDeep, fontSize = 25.sp, fontWeight = FontWeight.Black); Text("Çalışma ritmini koruyan hatırlatmalar", color = NGreen, fontWeight = FontWeight.Bold); Spacer(Modifier.height(12.dp)); NotificationRow("🔥", "Serini koru", if (state.streak > 0) "${state.streak} günlük serin devam ediyor." else "Bugün ilk adımı at."); NotificationRow("🎯", "Günün hedefi", "10 soru tamamla ve XP kazan."); NotificationRow("🏆", "Arena", "Yeni bir arena antrenmanı seni bekliyor."); NotificationRow("🧠", "Akıllı tekrar", "Zayıf konularına göre test hazırlanıyor.") } }
}

@Composable private fun NotificationRow(icon: String, title: String, body: String) { Row(Modifier.fillMaxWidth().padding(vertical = 9.dp), verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(40.dp).clip(CircleShape).background(NMint), contentAlignment = Alignment.Center) { Text(icon) }; Spacer(Modifier.width(11.dp)); Column { Text(title, color = NDeep, fontWeight = FontWeight.Bold); Text(body, color = Color.Gray, fontSize = 12.sp) } } }

@Composable private fun NextGenQuiz(title: String, source: List<Question>, store: AppProgressStore, reviewOnly: Boolean, finish: () -> Unit) {
    val scope = rememberCoroutineScope()
    var index by rememberSaveable { mutableIntStateOf(0) }
    var selected by rememberSaveable { mutableIntStateOf(-1) }
    var correct by rememberSaveable { mutableIntStateOf(0) }
    var wrong by rememberSaveable { mutableIntStateOf(0) }
    var done by rememberSaveable { mutableStateOf(false) }
    var seconds by rememberSaveable { mutableIntStateOf((source.size * 18).coerceIn(60, 300)) }
    LaunchedEffect(done) { while (!done && seconds > 0) { kotlinx.coroutines.delay(1000); seconds-- }; if (!done && seconds <= 0) done = true }
    if (done) { LaunchedEffect(Unit) { store.updateStreak() }; Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) { Text("🏆", fontSize = 64.sp); Text("Tur tamamlandı", color = NDeep, fontSize = 28.sp, fontWeight = FontWeight.Black); Text("$correct / ${source.size} doğru", color = NGreen, fontSize = 23.sp, fontWeight = FontWeight.Bold); Text("$wrong yanlış • +${correct * 10} XP", color = Color.Gray); Spacer(Modifier.height(18.dp)); Button(onClick = finish, Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(NGreen)) { Text("Ana ekrana dön") } }; return }
    val q = source[index.coerceIn(0, source.lastIndex)]
    val answered = selected >= 0
    LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Row(verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = finish) { Icon(Icons.Default.Close, "Çık") }; Column(Modifier.weight(1f)) { Text(title, color = NDeep, fontWeight = FontWeight.Black); Text("${index + 1}/${source.size}", color = NGreen, fontSize = 11.sp) }; Text("⏱ ${seconds}s", color = if (seconds <= 20) NRed else NGreen, fontWeight = FontWeight.Black) } }
        item { LinearProgressIndicator({ (index + 1f) / source.size }, Modifier.fillMaxWidth(), color = NGreen, trackColor = NMint) }
        item { Card(colors = CardDefaults.cardColors(NCard), shape = RoundedCornerShape(21.dp)) { Column(Modifier.padding(18.dp)) { Text(q.topic, color = NGreen, fontWeight = FontWeight.Black, fontSize = 11.sp); Text(q.text, color = NDeep, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 7.dp)) } } }
        items(q.options.indices.toList(), key = { q.id * 10 + it }) { i ->
            val isRight = i == q.correctIndex
            val chosen = i == selected
            val border = when { !answered -> Color(0xFFD7E6DE); isRight -> NGreen; chosen -> NRed; else -> Color(0xFFD7E6DE) }
            Card(Modifier.fillMaxWidth().border(2.dp, border, RoundedCornerShape(16.dp)).clickable(enabled = !answered) { selected = i; if (isRight) correct++ else wrong++; scope.launch { store.record(q, isRight) } }, colors = CardDefaults.cardColors(when { !answered -> NCard; isRight -> NMint; chosen -> Color(0xFFFFEBEB); else -> NCard })) { Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(32.dp).clip(CircleShape).background(border.copy(alpha = .14f)), contentAlignment = Alignment.Center) { Text(('A'.code + i).toChar().toString(), color = border, fontWeight = FontWeight.Black) }; Spacer(Modifier.width(10.dp)); Text(q.options[i], color = NDeep, fontWeight = if (chosen || (answered && isRight)) FontWeight.Bold else FontWeight.Normal) } }
        }
        if (answered) { item { Card(colors = CardDefaults.cardColors(NGold.copy(alpha = .25f)), shape = RoundedCornerShape(18.dp)) { Column(Modifier.padding(15.dp)) { Text("💡 DİKKAT KÖŞESİ", color = Color(0xFF7A5A00), fontWeight = FontWeight.Black); Text(q.explanation, color = NDeep, fontSize = 13.sp, modifier = Modifier.padding(top = 5.dp)); if (reviewOnly) Text("↻ Tekrar modu: Bu soru zayıf/yanlış geçmişinden seçildi.", color = NGreen, fontSize = 11.sp, modifier = Modifier.padding(top = 7.dp)) } } }; item { Button(onClick = { if (index < source.lastIndex) { index++; selected = -1 } else done = true }, Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(NGreen)) { Text(if (index < source.lastIndex) "Sonraki Soru →" else "Sonucu Gör") } }
    }
}

private fun smartQuestions(s: AppProgressStore.Snapshot): List<Question> {
    val all = SharedQuestionPool.all
    val wrong = all.filter { it.id in s.wrongIds }
    val weak = s.topicWrong.entries.sortedByDescending { it.value }.map { it.key }.take(3)
    val targeted = all.filter { it.topic in weak && it.id !in s.masteredIds }
    val unseen = all.filter { it.id !in s.seenIds }
    return (wrong.shuffled() + targeted.shuffled() + unseen.shuffled()).distinctBy { it.id }.take(10)
}

private fun weakestTopic(s: AppProgressStore.Snapshot): String = s.topicWrong.maxByOrNull { it.value }?.key ?: "Genel Coğrafya"

private fun provinceQuestions(p: AtlasProvince): List<Question> = (SharedQuestionPool.all.filter { it.text.contains(p.name, true) || it.explanation.contains(p.name, true) } + SharedQuestionPool.all.filter { it.topic == "Bölgeler" || it.topic == "Coğrafi Konum" }).distinctBy { it.id }.shuffled().take(5)

@Composable private fun BrushCard(color: Color): Color = color
