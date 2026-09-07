package tr.yurdunubil.app

import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import kotlin.math.roundToInt

private val Deep = Color(0xFF06221B)
private val Green = Color(0xFF18B77A)
private val Mint = Color(0xFFC8F7DF)
private val Gold = Color(0xFFFFC857)
private val Red = Color(0xFFE65353)
private val Bg = Color(0xFFF4F8F6)
private val Soft = Color(0xFFE6F7EE)

@Composable
fun GeographyNativeStableApp() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("yurdunu_bil_native", 0) }
    var page by remember { mutableIntStateOf(0) }
    var quiz by remember { mutableStateOf<List<Question>?>(null) }
    var title by remember { mutableStateOf("") }
    val launch: (String, List<Question>) -> Unit = { t, q -> if (q.isNotEmpty()) { title = t; quiz = q } }
    if (quiz != null) QuizStable(title, quiz!!, prefs) { quiz = null }
    else Scaffold(bottomBar = {
        NavigationBar(containerColor = Color.White) {
            val labels = listOf("Ana Sayfa", "Kütüphane", "Harita", "Arena", "Profil")
            val icons = listOf(Icons.Default.Home, Icons.Default.MenuBook, Icons.Default.Map, Icons.Default.SportsEsports, Icons.Default.Person)
            labels.forEachIndexed { i, label -> NavigationBarItem(selected = page == i, onClick = { page = i }, icon = { Icon(icons[i], label) }, label = { Text(label, fontSize = 10.sp) }) }
        }
    }) { pad ->
        Box(Modifier.fillMaxSize().padding(pad).background(Bg)) {
            when (page) {
                0 -> HomeStable(prefs, launch)
                1 -> LibraryStable(launch)
                2 -> MapStable()
                3 -> ArenaStable(launch)
                else -> ProfileStable(prefs)
            }
        }
    }
}

@Composable
private fun HomeStable(p: SharedPreferences, launch: (String, List<Question>) -> Unit) {
    val solved = p.getInt("solved", 0); val correct = p.getInt("correct", 0); val xp = p.getInt("xp", 0); val streak = p.getInt("streak", 0)
    val accuracy = if (solved == 0) 0 else (correct * 100f / solved).roundToInt()
    val daily = SharedQuestionPool.dailyMode()
    LazyColumn(contentPadding = PaddingValues(bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Column(Modifier.fillMaxWidth().background(Deep, RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)).padding(22.dp)) { Text("Yurdunu Bil", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Black); Text("KPSS Önlisans • Türkiye Coğrafyası", color = Mint); Spacer(Modifier.height(12.dp)); Text("Her gün biraz daha Türkiye.", color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.ExtraBold); Spacer(Modifier.height(14.dp)); Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { Pill("$solved", "Soru"); Pill("$xp", "XP"); Pill("$streak", "Seri") } } }
        item { CardStable(Green) { Text("🎯 BUGÜNÜN GÖREVİ", color = Green, fontWeight = FontWeight.Black); Text(daily.title, color = Deep, fontSize = 21.sp, fontWeight = FontWeight.Black); Text("${daily.questions} soru • +${daily.rewardXp} XP", color = Color.Gray); Button(onClick = { launch(daily.title, SharedQuestionPool.pick(daily)) }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(Green)) { Text("Göreve Başla") } } }
        item { Row(Modifier.padding(horizontal = 18.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) { Mini("⚡", "Hızlı 10", Modifier.weight(1f)) { launch("Hızlı 10", SharedQuestionPool.pick(SharedGameModes.quick)) }; Mini("🔥", "Bilgi Zinciri", Modifier.weight(1f)) { launch("Bilgi Zinciri", SharedQuestionPool.pick(SharedGameModes.chain)) } } }
        item { CardStable(Gold, true) { Text("⚔️ ARENA", color = Gold, fontWeight = FontWeight.Black); Text("Bilgini hızla kanıtla.", color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.Black); Text("1v1 • hız • bölge • Türkiye Ustası", color = Color.White.copy(.75f)) } }
        item { CardStable(Green) { Text("📊 GELİŞİMİN", color = Green, fontWeight = FontWeight.Black); Text("%$accuracy genel doğruluk", color = Deep, fontSize = 22.sp, fontWeight = FontWeight.Black); LinearProgressIndicator(progress = { accuracy / 100f }, modifier = Modifier.fillMaxWidth().height(7.dp), color = Green, trackColor = Soft) } }
    }
}

@Composable
private fun LibraryStable(launch: (String, List<Question>) -> Unit) {
    var search by remember { mutableStateOf("") }
    val topics = GeographyData.topics.filter { it.title.contains(search, true) || it.subtitle.contains(search, true) }
    LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("Kütüphane", color = Deep, fontSize = 30.sp, fontWeight = FontWeight.Black); Text("12 ana konu • sınav odaklı", color = Green, fontWeight = FontWeight.Bold); OutlinedTextField(search, { search = it }, Modifier.fillMaxWidth().padding(top = 8.dp), singleLine = true, leadingIcon = { Icon(Icons.Default.Search, null) }, placeholder = { Text("Konu ara…") }) }
        item { CardStable(Green, true) { Text("🧠 Oku → çöz → yanlışını öğren → tekrar et", color = Color.White, fontWeight = FontWeight.ExtraBold); Text("Bir konuya dokun ve konu testine geç.", color = Mint, fontSize = 12.sp) } }
        items(topics) { topic -> CardStable(Green, onClick = { val m = SharedGameMode("topic-${topic.title}", topic.title, topic.subtitle, topic.icon, 10, 180, 100, SharedQuestionPool.topicForLibrary(topic.title)); launch(topic.title, SharedQuestionPool.pick(m)) }) { Row(verticalAlignment = Alignment.CenterVertically) { Text(topic.icon, fontSize = 28.sp); Spacer(Modifier.width(10.dp)); Column(Modifier.weight(1f)) { Text(topic.title, color = Deep, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold); Text(topic.subtitle, color = Color.Gray, fontSize = 11.sp); LinearProgressIndicator(progress = { topic.progress / 100f }, modifier = Modifier.fillMaxWidth().padding(top = 5.dp), color = Green, trackColor = Soft) }; Icon(Icons.Default.ArrowForward, null, tint = Green) } } }
    }
}

@Composable
private fun MapStable() {
    val regions = listOf("Marmara", "Ege", "Akdeniz", "İç Anadolu", "Karadeniz", "Doğu Anadolu", "Güneydoğu Anadolu")
    var region by remember { mutableStateOf<String?>(null) }; var search by remember { mutableStateOf("") }; var selected by remember { mutableStateOf<Province?>(null) }
    val provinces = all81Stable().filter { (region == null || it.region == region) && (search.isBlank() || it.name.contains(search, true)) }
    LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
        item { Text("Türkiye Haritası", color = Deep, fontSize = 30.sp, fontWeight = FontWeight.Black); Text("81 il • 7 bölge", color = Green, fontWeight = FontWeight.Bold); OutlinedTextField(search, { search = it }, Modifier.fillMaxWidth().padding(top = 8.dp), singleLine = true, leadingIcon = { Icon(Icons.Default.Search, null) }, placeholder = { Text("İl ara…") }) }
        item { Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) { FilterChip(region == null, { region = null }, label = { Text("Tümü") }); regions.forEach { r -> FilterChip(region == r, { region = r }, label = { Text(r) }) } } }
        item { Text("${provinces.size} il", color = Deep, fontWeight = FontWeight.Bold) }
        items(provinces) { province -> CardStable(Green, onClick = { selected = province }) { Row(verticalAlignment = Alignment.CenterVertically) { Text(province.name, color = Deep, fontWeight = FontWeight.ExtraBold, modifier = Modifier.weight(1f)); Text(province.region, color = Green, fontSize = 11.sp); Icon(Icons.Default.LocationOn, null, tint = Green) } } }
    }
    selected?.let { p -> AlertDialog(onDismissRequest = { selected = null }, title = { Text("📍 ${p.name}", fontWeight = FontWeight.Black) }, text = { Column { Text(p.region, color = Green, fontWeight = FontWeight.Bold); Text(p.clue, color = Deep, fontWeight = FontWeight.SemiBold); p.facts.forEach { Text("• $it", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(top = 5.dp)) } } }, confirmButton = { TextButton({ selected = null }) { Text("Kapat", color = Green) } }) }
}

@Composable
private fun ArenaStable(launch: (String, List<Question>) -> Unit) {
    val modes = SharedGameModes.games + SharedGameModes.arenaModes
    LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("Arena & Oyunlar", color = Deep, fontSize = 30.sp, fontWeight = FontWeight.Black); Text("Öğrenirken yarış.", color = Green, fontWeight = FontWeight.Bold) }
        items(modes) { mode -> CardStable(if (mode.arena) Gold else Green, onClick = { launch(mode.title, SharedQuestionPool.pick(mode)) }) { Row(verticalAlignment = Alignment.CenterVertically) { Text(mode.icon, fontSize = 27.sp); Spacer(Modifier.width(10.dp)); Column(Modifier.weight(1f)) { Text(mode.title, color = Deep, fontWeight = FontWeight.ExtraBold); Text(mode.subtitle, color = Color.Gray, fontSize = 11.sp); Text("${mode.questions} soru • ${mode.seconds} sn • +${mode.rewardXp} XP", color = Green, fontSize = 10.sp, fontWeight = FontWeight.Black) }; Icon(Icons.Default.ArrowForward, null, tint = Green) } } }
    }
}

@Composable
private fun ProfileStable(p: SharedPreferences) {
    val solved = p.getInt("solved", 0); val correct = p.getInt("correct", 0); val wrong = p.getInt("wrong", 0); val xp = p.getInt("xp", 0); val streak = p.getInt("streak", 0); val level = 1 + xp / 500
    LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) { item { Text("Profil", color = Deep, fontSize = 30.sp, fontWeight = FontWeight.Black); Text("Seviye $level • $xp XP", color = Green, fontWeight = FontWeight.Bold) }; item { CardStable(Green, true) { Text("🧭 Gezgin", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black); Text("Türkiye coğrafyasını adım adım öğren.", color = Mint) } }; item { CardStable(Green) { Text("İSTATİSTİKLER", color = Green, fontWeight = FontWeight.Black); Stat("Çözülen soru", solved); Stat("Doğru", correct); Stat("Yanlış", wrong); Stat("Seri", streak) } }; item { CardStable(Gold) { Text("🏅 BAŞARIMLAR", color = Color(0xFF9B6A00), fontWeight = FontWeight.Black); Text(if (solved >= 10) "🥉 İlk 10 Soru ✓" else "🔒 İlk 10 Soru"); Text(if (streak >= 3) "🔥 3 Günlük Seri ✓" else "🔒 3 Günlük Seri", modifier = Modifier.padding(top = 7.dp)) } } }
}

@Composable
private fun QuizStable(title: String, source: List<Question>, p: SharedPreferences, finish: () -> Unit) {
    var index by remember { mutableIntStateOf(0) }; var selected by remember { mutableIntStateOf(-1) }; var correct by remember { mutableIntStateOf(0) }; var wrong by remember { mutableIntStateOf(0) }; var done by remember { mutableStateOf(false) }
    if (done) { LaunchedEffect(Unit) { p.edit().putInt("solved", p.getInt("solved", 0) + source.size).putInt("correct", p.getInt("correct", 0) + correct).putInt("wrong", p.getInt("wrong", 0) + wrong).putInt("xp", p.getInt("xp", 0) + correct * 10).apply() }; Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) { Text("🏆", fontSize = 56.sp); Text("Tur tamamlandı", color = Deep, fontSize = 28.sp, fontWeight = FontWeight.Black); Text("$correct / ${source.size} doğru", color = Green, fontSize = 22.sp, fontWeight = FontWeight.Bold); Text("$wrong yanlış • +${correct * 10} XP", color = Color.Gray); Button({ finish() }, Modifier.fillMaxWidth().padding(top = 20.dp), colors = ButtonDefaults.buttonColors(Green)) { Text("Devam Et") } }; return }
    val q = source[index]
    LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Row(verticalAlignment = Alignment.CenterVertically) { IconButton({ finish() }) { Icon(Icons.Default.Close, "Çık") }; Column(Modifier.weight(1f)) { Text(title, color = Deep, fontWeight = FontWeight.Black); Text("${index + 1} / ${source.size}", color = Green, fontSize = 11.sp) } } }
        item { LinearProgressIndicator(progress = { (index + 1f) / source.size }, Modifier.fillMaxWidth(), color = Green, trackColor = Soft) }
        item { CardStable(Green) { Text(q.topic, color = Green, fontWeight = FontWeight.Black, fontSize = 11.sp); Text(q.text, color = Deep, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold) } }
        items(q.options.size) { i -> val isCorrect = i == q.correctIndex; val isSelected = i == selected; val border = if (selected == -1) Color(0xFFDCE7E1) else if (isCorrect) Green else if (isSelected) Red else Color(0xFFDCE7E1); Card(Modifier.fillMaxWidth().border(2.dp, border, RoundedCornerShape(16.dp)).clickable(enabled = selected == -1) { selected = i; if (isCorrect) correct++ else wrong++ }, colors = CardDefaults.cardColors(if (selected != -1 && isCorrect) Soft else if (selected != -1 && isSelected) Color(0xFFFFE7E7) else Color.White)) { Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) { Text((('A'.code + i).toChar()).toString(), color = border, fontWeight = FontWeight.Black); Spacer(Modifier.width(10.dp)); Text(q.options[i], color = Deep, fontWeight = if (isSelected || (selected != -1 && isCorrect)) FontWeight.Bold else FontWeight.Normal) } } }
        if (selected != -1) { item { CardStable(Gold) { Text("💡 DİKKAT KÖŞESİ", color = Color(0xFF9B6A00), fontWeight = FontWeight.Black); Text(q.explanation, color = Deep, fontSize = 13.sp) } }; item { Button({ if (index + 1 == source.size) done = true else { index++; selected = -1 } }, Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(Green)) { Text(if (index + 1 == source.size) "Sonucu Gör" else "Sonraki Soru") } } }
    }
}

@Composable private fun CardStable(accent: Color, dark: Boolean = false, modifier: Modifier = Modifier, onClick: (() -> Unit)? = null, content: @Composable ColumnScope.() -> Unit) { Column(modifier.fillMaxWidth().then(if (onClick != null) Modifier.clickable { onClick() } else Modifier).background(if (dark) Deep else Color.White, RoundedCornerShape(19.dp)).border(1.dp, accent.copy(alpha = .22f), RoundedCornerShape(19.dp)).padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp), content = content) }
@Composable private fun Pill(value: String, label: String) { Column(Modifier.background(Color.White.copy(.12f), RoundedCornerShape(12.dp)).padding(10.dp)) { Text(value, color = Color.White, fontWeight = FontWeight.Black); Text(label, color = Mint, fontSize = 9.sp) } }
@Composable private fun Mini(icon: String, title: String, modifier: Modifier, onClick: () -> Unit) { Card(modifier.clickable { onClick() }, colors = CardDefaults.cardColors(Color.White), shape = RoundedCornerShape(17.dp)) { Column(Modifier.padding(14.dp)) { Text(icon, fontSize = 25.sp); Text(title, color = Deep, fontWeight = FontWeight.ExtraBold); Text("Başla", color = Green, fontSize = 10.sp) } } }
@Composable private fun Stat(label: String, value: Int) { Row(Modifier.fillMaxWidth().padding(vertical = 5.dp), horizontalArrangement = Arrangement.SpaceBetween) { Text(label, color = Color.Gray); Text(value.toString(), color = Deep, fontWeight = FontWeight.Black) } }

private fun all81Stable(): List<Province> {
    val groups = linkedMapOf(
        "Marmara" to listOf("Balıkesir","Bilecik","Bursa","Çanakkale","Edirne","İstanbul","Kırklareli","Kocaeli","Sakarya","Tekirdağ","Yalova"),
        "Ege" to listOf("Afyonkarahisar","Aydın","Denizli","İzmir","Kütahya","Manisa","Muğla","Uşak"),
        "Akdeniz" to listOf("Adana","Antalya","Burdur","Hatay","Isparta","Kahramanmaraş","Mersin","Osmaniye"),
        "İç Anadolu" to listOf("Aksaray","Ankara","Çankırı","Eskişehir","Karaman","Kayseri","Kırıkkale","Kırşehir","Konya","Nevşehir","Niğde","Sivas","Yozgat"),
        "Karadeniz" to listOf("Amasya","Artvin","Bartın","Bayburt","Bolu","Çorum","Düzce","Giresun","Gümüşhane","Karabük","Kastamonu","Ordu","Rize","Samsun","Sinop","Tokat","Trabzon","Zonguldak"),
        "Doğu Anadolu" to listOf("Ağrı","Ardahan","Bingöl","Bitlis","Elazığ","Erzincan","Erzurum","Hakkari","Iğdır","Kars","Malatya","Muş","Tunceli","Van"),
        "Güneydoğu Anadolu" to listOf("Adıyaman","Batman","Diyarbakır","Gaziantep","Kilis","Mardin","Siirt","Şanlıurfa","Şırnak")
    )
    val known = GeographyData.provinces.associateBy { it.name }
    return groups.flatMap { (region, names) -> names.map { name -> known[name] ?: Province(name, region, "$region bölgesinde yer alan il.", listOf("Bölge: $region", "İl-bölge bağlantısını öğren.", "Haritadaki konumunu hatırla.")) } }
}
