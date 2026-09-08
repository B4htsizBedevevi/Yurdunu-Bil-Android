package tr.yurdunubil.app

import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

private val FDeep = Color(0xFF06221B)
private val FGreen = Color(0xFF18B77A)
private val FMint = Color(0xFFC8F7DF)
private val FGold = Color(0xFFFFC857)
private val FRed = Color(0xFFE65353)
private val FBg = Color(0xFFF4F8F6)
private val FSoft = Color(0xFFE6F7EE)

@Composable
fun FourTabMainApp() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("yurdunu_bil_native", 0) }
    var tab by remember { mutableIntStateOf(0) }
    var quiz by remember { mutableStateOf<List<Question>?>(null) }
    var quizTitle by remember { mutableStateOf("") }
    val launchQuiz: (String, List<Question>) -> Unit = { title, questions -> if (questions.isNotEmpty()) { quizTitle = title; quiz = questions } }
    if (quiz != null) { FourTabQuiz(quizTitle, quiz!!, prefs) { quiz = null }; return }
    Scaffold(containerColor = FBg, bottomBar = {
        NavigationBar(containerColor = Color.White) {
            val items = listOf(Triple("Ana Sayfa", Icons.Default.Home, 0), Triple("Kütüphane", Icons.Default.MenuBook, 1), Triple("Etkinlikler", Icons.Default.SportsEsports, 2), Triple("Ayarlar", Icons.Default.Settings, 3))
            items.forEach { (label, icon, index) -> NavigationBarItem(selected = tab == index, onClick = { tab = index }, icon = { Icon(icon, label) }, label = { Text(label, fontSize = 10.sp) }) }
        }
    }) { pad ->
        Box(Modifier.fillMaxSize().padding(pad)) {
            when (tab) {
                0 -> FourHome(prefs, launchQuiz) { tab = 2 }
                1 -> FourLibrary(launchQuiz)
                2 -> FourEvents(launchQuiz)
                3 -> FourSettings(prefs)
            }
        }
    }
}

@Composable private fun FourHome(p: SharedPreferences, launch: (String, List<Question>) -> Unit, openEvents: () -> Unit) {
    val solved = p.getInt("solved", 0); val correct = p.getInt("correct", 0); val xp = p.getInt("xp", 0); val streak = p.getInt("streak", 0)
    val accuracy = if (solved == 0) 0 else (correct * 100f / solved).roundToInt(); val daily = SharedQuestionPool.dailyMode()
    LazyColumn(contentPadding = PaddingValues(bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(13.dp)) {
        item { Column(Modifier.fillMaxWidth().background(FDeep, RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)).padding(22.dp)) { Text("Yurdunu Bil", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Black); Text("KPSS Önlisans • Türkiye Coğrafyası", color = FMint, fontSize = 13.sp); Spacer(Modifier.height(12.dp)); Text("Her gün biraz daha Türkiye.", color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.ExtraBold); Spacer(Modifier.height(14.dp)); Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { FourPill("$solved", "Soru"); FourPill("$xp", "XP"); FourPill("$streak", "Seri") } } }
        item { FourCard(FGreen) { Text("🎯 BUGÜNÜN GÖREVİ", color = FGreen, fontWeight = FontWeight.Black); Text(daily.title, color = FDeep, fontSize = 21.sp, fontWeight = FontWeight.Black); Text("${daily.questions} soru • +${daily.rewardXp} XP", color = Color.Gray); Button({ launch(daily.title, SharedQuestionPool.pick(daily)) }, Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(FGreen)) { Text("Göreve Başla") } } }
        item { Row(Modifier.padding(horizontal = 18.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) { FourMini("⚡", "Hızlı 10", Modifier.weight(1f)) { launch("Hızlı 10", SharedQuestionPool.pick(SharedGameModes.quick)) }; FourMini("🔥", "Bilgi Zinciri", Modifier.weight(1f)) { launch("Bilgi Zinciri", SharedQuestionPool.pick(SharedGameModes.chain)) } } }
        item { FourCard(FGold, dark = true, onClick = openEvents) { Text("⚔️ ARENA", color = FGold, fontWeight = FontWeight.Black); Text("Bilgini hızla kanıtla.", color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.Black); Text("1v1 • hız • bölge • Türkiye Ustası", color = Color.White.copy(.75f)); Text("Etkinliklere git →", color = FMint, fontWeight = FontWeight.Bold) } }
        item { FourCard(FGreen) { Text("📊 GELİŞİMİN", color = FGreen, fontWeight = FontWeight.Black); Text("%$accuracy genel doğruluk", color = FDeep, fontSize = 21.sp, fontWeight = FontWeight.Black); LinearProgressIndicator({ accuracy / 100f }, Modifier.fillMaxWidth().height(7.dp), color = FGreen, trackColor = FSoft) } }
    }
}

@Composable private fun FourLibrary(launch: (String, List<Question>) -> Unit) {
    var search by remember { mutableStateOf("") }; var selectedProvince by remember { mutableStateOf<Province?>(null) }
    val topics = GeographyData.topics.filter { it.title.contains(search, true) || it.subtitle.contains(search, true) }; val provinces = all81Four().filter { search.isBlank() || it.name.contains(search, true) || it.region.contains(search, true) }
    LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("Kütüphane", color = FDeep, fontSize = 30.sp, fontWeight = FontWeight.Black); Text("Oku → çöz → yanlışını öğren → tekrar et", color = FGreen, fontWeight = FontWeight.Bold); OutlinedTextField(search, { search = it }, Modifier.fillMaxWidth().padding(top = 8.dp), singleLine = true, leadingIcon = { Icon(Icons.Default.Search, null) }, placeholder = { Text("Konu veya il ara…") }) }
        item { FourCard(FGreen, dark = true) { Text("🗺️ TÜRKİYE ATLASI", color = FMint, fontWeight = FontWeight.Black); Text("81 il • 7 bölge", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold); Text("Harita sekmesini kaldırdık; illeri burada daha hızlı keşfet.", color = Color.White.copy(.72f), fontSize = 12.sp) } }
        item { Text("Konu Bankası", color = FDeep, fontSize = 19.sp, fontWeight = FontWeight.Black) }
        items(topics) { topic -> FourCard(FGreen, onClick = { val mode = SharedGameMode("topic-${topic.title}", topic.title, topic.subtitle, topic.icon, 10, 180, 100, SharedQuestionPool.topicForLibrary(topic.title)); launch(topic.title, SharedQuestionPool.pick(mode)) }) { Row(verticalAlignment = Alignment.CenterVertically) { Text(topic.icon, fontSize = 28.sp); Spacer(Modifier.width(10.dp)); Column(Modifier.weight(1f)) { Text(topic.title, color = FDeep, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold); Text(topic.subtitle, color = Color.Gray, fontSize = 11.sp); LinearProgressIndicator({ topic.progress / 100f }, Modifier.fillMaxWidth().padding(top = 5.dp), color = FGreen, trackColor = FSoft) }; Icon(Icons.Default.ArrowForward, null, tint = FGreen) } } }
        item { Text("İller", color = FDeep, fontSize = 19.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(top = 8.dp)) }
        items(provinces) { province -> FourCard(FGreen, onClick = { selectedProvince = province }) { Row(verticalAlignment = Alignment.CenterVertically) { Text("📍", fontSize = 21.sp); Spacer(Modifier.width(9.dp)); Column(Modifier.weight(1f)) { Text(province.name, color = FDeep, fontWeight = FontWeight.ExtraBold); Text(province.region, color = FGreen, fontSize = 11.sp) }; Icon(Icons.Default.ChevronRight, null, tint = FGreen) } } }
    }
    selectedProvince?.let { province -> AlertDialog(onDismissRequest = { selectedProvince = null }, title = { Text("📍 ${province.name}", fontWeight = FontWeight.Black) }, text = { Column { Text(province.region, color = FGreen, fontWeight = FontWeight.Bold); Text(province.clue, color = FDeep, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 6.dp)); province.facts.forEach { Text("• $it", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(top = 5.dp)) } } }, confirmButton = { TextButton({ selectedProvince = null }) { Text("Kapat", color = FGreen) } }) }
}

@Composable private fun FourEvents(launch: (String, List<Question>) -> Unit) {
    val modes = SharedGameModes.games + SharedGameModes.arenaModes
    LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("Etkinlikler & Arena", color = FDeep, fontSize = 30.sp, fontWeight = FontWeight.Black); Text("Öğrenirken yarış, XP kazan.", color = FGreen, fontWeight = FontWeight.Bold) }
        item { FourCard(FGold, dark = true) { Text("🏆 SEZON 1", color = FGold, fontWeight = FontWeight.Black); Text("Türkiye Coğrafyası Ligi", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black); Text("Hız • bölge • bilgi • meydan okuma", color = Color.White.copy(.72f)) } }
        items(modes) { mode -> FourCard(if (mode.arena) FGold else FGreen, onClick = { launch(mode.title, SharedQuestionPool.pick(mode)) }) { Row(verticalAlignment = Alignment.CenterVertically) { Text(mode.icon, fontSize = 28.sp); Spacer(Modifier.width(11.dp)); Column(Modifier.weight(1f)) { Text(mode.title, color = FDeep, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp); Text(mode.subtitle, color = Color.Gray, fontSize = 11.sp); Text("${mode.questions} soru • ${mode.seconds} sn • +${mode.rewardXp} XP", color = FGreen, fontSize = 10.sp, fontWeight = FontWeight.Black) }; Icon(Icons.Default.PlayArrow, null, tint = FGreen) } } }
    }
}

@Composable private fun FourSettings(p: SharedPreferences) {
    val solved = p.getInt("solved", 0); val correct = p.getInt("correct", 0); val wrong = p.getInt("wrong", 0); val xp = p.getInt("xp", 0); val level = 1 + xp / 500; var resetConfirm by remember { mutableStateOf(false) }
    LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(11.dp)) {
        item { Text("Ayarlar", color = FDeep, fontSize = 30.sp, fontWeight = FontWeight.Black); Text("Hesabın ve uygulama tercihlerin", color = FGreen, fontWeight = FontWeight.Bold) }
        item { FourCard(FGreen, dark = true) { Text("🧭 Gezgin", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black); Text("Seviye $level • $xp XP", color = FMint); Text("$solved soru • $correct doğru • $wrong yanlış", color = Color.White.copy(.72f), fontSize = 12.sp) } }
        item { FourSettingRow(Icons.Default.BarChart, "İstatistikler", "$solved soru • %${if (solved == 0) 0 else (correct * 100 / solved)} doğruluk") }
        item { FourSettingRow(Icons.Default.Lock, "Hesap", "Supabase oturumu Launch ekranından yönetilir") }
        item { FourSettingRow(Icons.Default.Info, "Sürüm", "Yurdunu Bil Android 0.2.0") }
        item { FourCard(FRed, onClick = { resetConfirm = true }) { Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.RestartAlt, null, tint = FRed); Spacer(Modifier.width(10.dp)); Column { Text("İlerlemeyi Sıfırla", color = FRed, fontWeight = FontWeight.ExtraBold); Text("Yerel soru, XP ve seri verilerini temizler", color = Color.Gray, fontSize = 11.sp) } } } }
    }
    if (resetConfirm) AlertDialog(onDismissRequest = { resetConfirm = false }, title = { Text("İlerleme sıfırlansın mı?", fontWeight = FontWeight.Black) }, text = { Text("Bu işlem cihazdaki yerel XP, soru ve istatistiklerini temizler.") }, confirmButton = { TextButton({ p.edit().clear().apply(); resetConfirm = false }) { Text("Sıfırla", color = FRed, fontWeight = FontWeight.Bold) } }, dismissButton = { TextButton({ resetConfirm = false }) { Text("Vazgeç") } })
}

@Composable private fun FourTabQuiz(title: String, source: List<Question>, p: SharedPreferences, finish: () -> Unit) {
    var index by remember { mutableIntStateOf(0) }; var selected by remember { mutableIntStateOf(-1) }; var correct by remember { mutableIntStateOf(0) }; var wrong by remember { mutableIntStateOf(0) }; var saved by remember { mutableStateOf(false) }
    if (saved) { LaunchedEffect(Unit) { p.edit().putInt("solved", p.getInt("solved", 0) + source.size).putInt("correct", p.getInt("correct", 0) + correct).putInt("wrong", p.getInt("wrong", 0) + wrong).putInt("xp", p.getInt("xp", 0) + correct * 10).apply() }; Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) { Text("🏆", fontSize = 56.sp); Text("Tur tamamlandı", color = FDeep, fontSize = 28.sp, fontWeight = FontWeight.Black); Text("$correct / ${source.size} doğru", color = FGreen, fontSize = 22.sp, fontWeight = FontWeight.Bold); Text("$wrong yanlış • +${correct * 10} XP", color = Color.Gray); Button(finish, Modifier.fillMaxWidth().padding(top = 20.dp), colors = ButtonDefaults.buttonColors(FGreen)) { Text("Devam Et") } }; return }
    val q = source[index]
    LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Row(verticalAlignment = Alignment.CenterVertically) { IconButton(finish) { Icon(Icons.Default.Close, "Çık") }; Column(Modifier.weight(1f)) { Text(title, color = FDeep, fontWeight = FontWeight.Black); Text("${index + 1} / ${source.size}", color = FGreen, fontSize = 11.sp) } } }
        item { LinearProgressIndicator({ (index + 1f) / source.size }, Modifier.fillMaxWidth(), color = FGreen, trackColor = FSoft) }
        item { FourCard(FGreen) { Text(q.topic, color = FGreen, fontWeight = FontWeight.Black, fontSize = 11.sp); Text(q.text, color = FDeep, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold) } }
        items(q.options.size) { i -> val isCorrect = i == q.correctIndex; val isSelected = i == selected; val border = if (selected == -1) Color(0xFFDCE7E1) else if (isCorrect) FGreen else if (isSelected) FRed else Color(0xFFDCE7E1); Card(Modifier.fillMaxWidth().border(2.dp, border, RoundedCornerShape(16.dp)).clickable(enabled = selected == -1) { selected = i; if (isCorrect) correct++ else wrong++ }, colors = CardDefaults.cardColors(if (selected != -1 && isCorrect) FSoft else if (selected != -1 && isSelected) Color(0xFFFFE7E7) else Color.White)) { Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) { Text((('A'.code + i).toChar()).toString(), color = border, fontWeight = FontWeight.Black); Spacer(Modifier.width(10.dp)); Text(q.options[i], color = FDeep, fontWeight = if (isSelected || (selected != -1 && isCorrect)) FontWeight.Bold else FontWeight.Normal) } } }
        if (selected != -1) { item { FourCard(FGold) { Text("💡 DİKKAT KÖŞESİ", color = Color(0xFF9B6A00), fontWeight = FontWeight.Black); Text(q.explanation, color = FDeep, fontSize = 13.sp) } }; item { Button({ if (index + 1 == source.size) saved = true else { index++; selected = -1 } }, Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(FGreen)) { Text(if (index + 1 == source.size) "Sonucu Gör" else "Sonraki Soru") } } }
    }
}

@Composable private fun FourCard(accent: Color, dark: Boolean = false, modifier: Modifier = Modifier, onClick: (() -> Unit)? = null, content: @Composable ColumnScope.() -> Unit) { Column(modifier.fillMaxWidth().then(if (onClick != null) Modifier.clickable { onClick() } else Modifier).background(if (dark) FDeep else Color.White, RoundedCornerShape(19.dp)).border(1.dp, accent.copy(alpha = .22f), RoundedCornerShape(19.dp)).padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp), content = content) }
@Composable private fun FourPill(value: String, label: String) { Column(Modifier.background(Color.White.copy(.12f), RoundedCornerShape(12.dp)).padding(10.dp)) { Text(value, color = Color.White, fontWeight = FontWeight.Black); Text(label, color = FMint, fontSize = 9.sp) } }
@Composable private fun FourMini(icon: String, title: String, modifier: Modifier, onClick: () -> Unit) { Card(modifier.clickable { onClick() }, colors = CardDefaults.cardColors(Color.White), shape = RoundedCornerShape(17.dp)) { Column(Modifier.padding(14.dp)) { Text(icon, fontSize = 25.sp); Text(title, color = FDeep, fontWeight = FontWeight.ExtraBold); Text("Başla", color = FGreen, fontSize = 10.sp) } } }
@Composable private fun FourSettingRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String) { FourCard(FGreen) { Row(verticalAlignment = Alignment.CenterVertically) { Icon(icon, null, tint = FGreen); Spacer(Modifier.width(11.dp)); Column { Text(title, color = FDeep, fontWeight = FontWeight.ExtraBold); Text(subtitle, color = Color.Gray, fontSize = 11.sp) } } } }

private fun all81Four(): List<Province> {
    val groups = linkedMapOf("Marmara" to listOf("Balıkesir","Bilecik","Bursa","Çanakkale","Edirne","İstanbul","Kırklareli","Kocaeli","Sakarya","Tekirdağ","Yalova"), "Ege" to listOf("Afyonkarahisar","Aydın","Denizli","İzmir","Kütahya","Manisa","Muğla","Uşak"), "Akdeniz" to listOf("Adana","Antalya","Burdur","Hatay","Isparta","Kahramanmaraş","Mersin","Osmaniye"), "İç Anadolu" to listOf("Aksaray","Ankara","Çankırı","Eskişehir","Karaman","Kayseri","Kırıkkale","Kırşehir","Konya","Nevşehir","Niğde","Sivas","Yozgat"), "Karadeniz" to listOf("Amasya","Artvin","Bartın","Bayburt","Bolu","Çorum","Düzce","Giresun","Gümüşhane","Karabük","Kastamonu","Ordu","Rize","Samsun","Sinop","Tokat","Trabzon","Zonguldak"), "Doğu Anadolu" to listOf("Ağrı","Ardahan","Bingöl","Bitlis","Elazığ","Erzincan","Erzurum","Hakkari","Iğdır","Kars","Malatya","Muş","Tunceli","Van"), "Güneydoğu Anadolu" to listOf("Adıyaman","Batman","Diyarbakır","Gaziantep","Kilis","Mardin","Siirt","Şanlıurfa","Şırnak"))
    val known = GeographyData.provinces.associateBy { it.name }
    return groups.flatMap { (region, names) -> names.map { name -> known[name] ?: Province(name, region, "$region bölgesinde yer alan il.", listOf("Bölge: $region", "İl-bölge bağlantısını öğren.", "Haritadaki konumunu hatırla.")) } }
}
