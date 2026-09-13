package tr.yurdunubil.app

import android.app.Activity
import android.content.SharedPreferences
import android.os.SystemClock
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.jan.supabase.functions.functions
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.serialization.json.buildJsonObject
import kotlin.math.roundToInt

private data class AppPalette(
    val bg: Color, val card: Color, val cardAlt: Color, val text: Color,
    val muted: Color, val green: Color, val gold: Color, val red: Color, val border: Color
)

private val LIGHT = AppPalette(Color(0xFFF3F7F5), Color.White, Color(0xFFE8F3EE), Color(0xFF07231B), Color(0xFF71847D), Color(0xFF18C98A), Color(0xFFFFC857), Color(0xFFE65353), Color(0xFFD8E7E0))
private val DARK = AppPalette(Color(0xFF06140F), Color(0xFF0E211B), Color(0xFF153229), Color(0xFFF3FBF7), Color(0xFF98B1A8), Color(0xFF28DE98), Color(0xFFFFC857), Color(0xFFFF7777), Color(0xFF234139))

@Composable
fun YurdunuBilMainV4() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("yurdunu_bil_native", 0) }
    var dark by rememberSaveable { mutableStateOf(prefs.getBoolean("dark_theme", false)) }
    val p = if (dark) DARK else LIGHT
    var tab by rememberSaveable { mutableIntStateOf(0) }
    var openTopic by remember { mutableStateOf<Topic?>(null) }
    var openProvince by remember { mutableStateOf<Province?>(null) }
    var openQuiz by remember { mutableStateOf<List<Question>?>(null) }
    var openQuizMode by remember { mutableStateOf<SharedGameMode?>(null) }
    var quizTitle by rememberSaveable { mutableStateOf("") }
    var arenaOpen by rememberSaveable { mutableStateOf(false) }
    var onlineArenaMode by remember { mutableStateOf<SharedGameMode?>(null) }
    var onlineMatchId by rememberSaveable { mutableStateOf<String?>(null) }
    var lastBackAt by remember { mutableLongStateOf(0L) }
    LaunchedEffect(Unit) { if (prefs.getBoolean("notifications_enabled", false) && NotificationHelper.canNotify(context)) NotificationHelper.scheduleDaily(context) }
    fun launchQuiz(title: String, mode: SharedGameMode) {
        val picked = SmartQuestionSelector.pick(mode, prefs)
        if (picked.isEmpty()) { Toast.makeText(context, "Bu mod için henüz yeterli soru yok.", Toast.LENGTH_SHORT).show(); return }
        quizTitle = title; openQuizMode = mode; openQuiz = picked; SmartQuestionSelector.remember(prefs, picked); prefs.edit().putString("last_activity", title).apply()
    }
    BackHandler {
        when { onlineMatchId != null -> onlineMatchId = null; onlineArenaMode != null -> onlineArenaMode = null; openQuiz != null -> openQuiz = null; openTopic != null -> openTopic = null; openProvince != null -> openProvince = null; arenaOpen -> arenaOpen = false; tab != 0 -> tab = 0; else -> { val now = SystemClock.elapsedRealtime(); if (now - lastBackAt < 1600L) (context as? Activity)?.finish() else { lastBackAt = now; Toast.makeText(context, "Çıkmak için geri tuşuna bir kez daha bas.", Toast.LENGTH_SHORT).show() } } }
    }
    if (openTopic != null) { val topic = openTopic!!; LessonV4(topic, p, prefs, onBack = { openTopic = null }) { openTopic = null; launchQuiz(topic.title, SharedGameMode("lesson-${topic.title}", topic.title, topic.subtitle, topic.icon, 10, 180, 100, SharedQuestionPool.topicForLibrary(topic.title))) }; return }
    if (openProvince != null) { ProvinceV4(openProvince!!, p) { openProvince = null }; return }
    if (openQuiz != null && openQuizMode != null) { QuizV4(quizTitle, openQuiz!!, openQuizMode!!, p, prefs) { openQuiz = null; openQuizMode = null }; return }
    if (onlineMatchId != null && onlineArenaMode != null) { ArenaMatchScreen(dark, onlineArenaMode!!, onlineMatchId!!) { onlineMatchId = null; onlineArenaMode = null }; return }
    if (onlineArenaMode != null) { OnlineArenaScreen(dark, onlineArenaMode!!, onBack = { onlineArenaMode = null }) { matchId -> onlineMatchId = matchId }; return }
    if (arenaOpen) { ArenaRootScreen(darkMode = dark, onExit = { arenaOpen = false }); return }
    MaterialTheme(colorScheme = if (dark) darkColorScheme(primary = p.green, background = p.bg, surface = p.card, onBackground = p.text, onSurface = p.text, onPrimary = Color(0xFF052118)) else lightColorScheme(primary = p.green, background = p.bg, surface = p.card, onBackground = p.text, onSurface = p.text, onPrimary = Color(0xFF052118))) {
        Scaffold(containerColor = p.bg, bottomBar = {
            NavigationBar(containerColor = p.card, tonalElevation = 3.dp) {
                val nav = listOf("Ana Sayfa" to Icons.Default.Home, "Kütüphane" to Icons.Default.MenuBook, "Etkinlikler" to Icons.Default.SportsEsports, "Ayarlar" to Icons.Default.Settings)
                nav.forEachIndexed { i, pair -> NavigationBarItem(selected = tab == i, onClick = { tab = i }, icon = { Icon(pair.second, pair.first) }, label = { Text(pair.first, fontSize = 10.sp) }, colors = NavigationBarItemDefaults.colors(selectedIconColor = p.text, selectedTextColor = p.text, indicatorColor = p.green.copy(alpha = .18f), unselectedIconColor = p.muted, unselectedTextColor = p.muted)) }
            }
        }) { pad ->
            Box(Modifier.fillMaxSize().padding(pad)) {
                AnimatedContent(targetState = tab, transitionSpec = { fadeIn() togetherWith fadeOut() }, label = "tab") { selectedTab ->
                    when (selectedTab) { 0 -> HomeV5(p, prefs, { launchQuiz("Bugünün Testi", SharedGameModes.quick) }) { arenaOpen = true }; 1 -> LibraryV5(p, prefs, { openTopic = it }, { openProvince = it }); 2 -> EventsV5(p) { mode -> if (mode.arena) onlineArenaMode = mode else launchQuiz(mode.title, mode) }; else -> SettingsV4(p, prefs, dark) { dark = it; prefs.edit().putBoolean("dark_theme", it).apply() } }
                }
            }
        }
    }
}

@Composable private fun AppCard(p: AppPalette, modifier: Modifier = Modifier, dark: Boolean = false, content: @Composable ColumnScope.() -> Unit) { Card(modifier = modifier, shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, if (dark) Color.White.copy(alpha = .07f) else p.border), colors = CardDefaults.cardColors(containerColor = if (dark) Color(0xFF0B2A22) else p.card), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) { Column(Modifier.padding(16.dp), content = content) } }
@Composable private fun Eyebrow(text: String, color: Color) = Text(text, color = color, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)

@Composable private fun HomeV4(p: AppPalette, prefs: SharedPreferences, quick: () -> Unit, arena: () -> Unit) {
    val solved = prefs.getInt("solved", 0); val correct = prefs.getInt("correct", 0); val xp = prefs.getInt("xp", 0); val streak = prefs.getInt("streak", 0); val accuracy = if (solved == 0) 0 else (correct * 100f / solved).roundToInt(); val recent = prefs.getString("last_activity", "Henüz çalışma yok") ?: "Henüz çalışma yok"
    LazyColumn(contentPadding = PaddingValues(bottom = 22.dp), verticalArrangement = Arrangement.spacedBy(11.dp)) {
        item { Box(Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(Color(0xFF05241B), Color(0xFF0E6B50))), RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)).statusBarsPadding().padding(20.dp)) { Column { Row(verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(52.dp).clip(RoundedCornerShape(15.dp)).background(Color.White.copy(alpha = .08f)).padding(5.dp)) { Image(painterResource(R.drawable.yurdunu_bil_app_icon), "Yurdunu Bil", Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp))) }; Spacer(Modifier.width(11.dp)); Column(Modifier.weight(1f)) { Text("Yurdunu Bil", color = Color.White, fontSize = 27.sp, fontWeight = FontWeight.Black); Text("KPSS Önlisans • Türkiye Coğrafyası", color = Color(0xFFC9F8E1), fontSize = 11.sp) }; Text("Lv.${1 + xp / 500}", color = Color.White, fontWeight = FontWeight.Black) }; Spacer(Modifier.height(16.dp)); Text("Bugün Türkiye'yi biraz daha çöz.", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold); Spacer(Modifier.height(12.dp)); Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { StatChip("$solved", "SORU"); StatChip("$xp", "XP"); StatChip("$streak", "SERİ") } } } }
        item { AppCard(p) { Row(verticalAlignment = Alignment.CenterVertically) { Icon(YBIcons.Target, null, tint = p.green, modifier = Modifier.size(31.dp)); Spacer(Modifier.width(10.dp)); Column(Modifier.weight(1f)) { Eyebrow("BUGÜNÜN GÖREVİ", p.green); Text("Hızlı 10", color = p.text, fontSize = 19.sp, fontWeight = FontWeight.Black); Text("Kısa bir coğrafya turuyla ritmini koru.", color = p.muted, fontSize = 11.sp) } }; Spacer(Modifier.height(10.dp)); GreenButton("Başla • 10 Soru", p, quick) } }
        item { Row(Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) { CompactHomeCard(p, "speed", "Hızlı 10", "10 soru", quick, Modifier.weight(1f)); CompactHomeCard(p, "chain", "Bilgi Zinciri", "seri yap", quick, Modifier.weight(1f)) } }
        item { AppCard(p, dark = true) { Row(verticalAlignment = Alignment.CenterVertically) { Icon(YBIcons.Swords, null, tint = p.gold, modifier = Modifier.size(31.dp)); Spacer(Modifier.width(10.dp)); Column(Modifier.weight(1f)) { Eyebrow("ARENA", p.gold); Text("Bilgini sahaya çıkar.", color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Black); Text("1v1 • hız • bölge • Türkiye Ustası", color = Color.White.copy(alpha = .72f), fontSize = 11.sp) } }; Spacer(Modifier.height(10.dp)); Button(onClick = arena, modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(13.dp), colors = ButtonDefaults.buttonColors(containerColor = p.gold, contentColor = Color(0xFF162118))) { Text("Arena'ya Git", fontWeight = FontWeight.Black) } } }
        item { AppCard(p) { Eyebrow("GELİŞİMİN", p.green); Text("%$accuracy genel doğruluk", color = p.text, fontSize = 20.sp, fontWeight = FontWeight.Black); Text(if (solved == 0) "İlk testini çöz ve ilerlemeni başlat." else "$correct doğru cevapla devam ediyorsun.", color = p.muted, fontSize = 11.sp); Spacer(Modifier.height(9.dp)); LinearProgressIndicator(progress = { accuracy / 100f }, modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(8.dp)), color = p.green, trackColor = p.cardAlt) } }
        item { AppCard(p) { Row(verticalAlignment = Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Eyebrow("DEVAM ET", p.gold); Text(recent, color = p.text, fontSize = 16.sp, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis); Text("Son çalışmana kaldığın yerden dön.", color = p.muted, fontSize = 11.sp) }; Icon(Icons.Default.ArrowForward, null, tint = p.green) } } }
    }
}
@Composable private fun StatChip(value: String, label: String) { Column(Modifier.clip(RoundedCornerShape(11.dp)).background(Color.White.copy(alpha = .09f)).padding(horizontal = 11.dp, vertical = 7.dp)) { Text(value, color = Color.White, fontWeight = FontWeight.Black); Text(label, color = Color.White.copy(alpha = .58f), fontSize = 7.sp) } }
@Composable private fun GreenButton(text: String, p: AppPalette, click: () -> Unit) = Button(onClick = click, modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(13.dp), colors = ButtonDefaults.buttonColors(containerColor = p.green, contentColor = Color(0xFF052118))) { Text(text, fontWeight = FontWeight.Black) }
@Composable private fun CompactHomeCard(p: AppPalette, icon: String, title: String, subtitle: String, click: () -> Unit, modifier: Modifier) { AppCard(p, modifier.clickable(onClick = click)) { YBGameIcon(icon, p.green, 26.dp); Spacer(Modifier.height(5.dp)); Text(title, color = p.text, fontWeight = FontWeight.Black, fontSize = 14.sp); Text(subtitle, color = p.green, fontSize = 10.sp, fontWeight = FontWeight.Bold) } }

@Composable private fun LibraryV4(p: AppPalette, onTopic: (Topic) -> Unit, onProvince: (Province) -> Unit) {
    var search by rememberSaveable { mutableStateOf("") }; val topics = GeographyData.topics.filter { it.title.contains(search, true) || it.subtitle.contains(search, true) }
    LazyColumn(contentPadding = PaddingValues(top = 12.dp, bottom = 26.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Column(Modifier.padding(horizontal = 16.dp)) { Text("Kütüphane", color = p.text, fontSize = 29.sp, fontWeight = FontWeight.Black); Text("Oku → anla → bağlantı kur → kendini test et.", color = p.green, fontSize = 12.sp, fontWeight = FontWeight.Bold); Spacer(Modifier.height(8.dp)); OutlinedTextField(value = search, onValueChange = { search = it }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(15.dp), label = { Text("Konu ara") }, leadingIcon = { Icon(Icons.Default.Search, null) }, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = p.green, unfocusedBorderColor = p.border, focusedLabelColor = p.green, cursorColor = p.green, focusedTextColor = p.text, unfocusedTextColor = p.text)) } }
        item { AppCard(p, dark = true) { Eyebrow("ÇALIŞMA KÜTÜPHANESİ", p.green); Text("17 ana konu • derinlemesine öğrenme", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black); Text("Tek paragrafla geçmiyoruz: kavramlar, neden-sonuç ilişkileri, sınav tuzakları, hızlı tekrar ve mini yoklama var.", color = Color.White.copy(alpha = .74f), fontSize = 12.sp, lineHeight = 18.sp) } }
        item { SectionTitle("Konu Bankası", p) }
        items(topics.chunked(2), key = { row -> row.first().title }) { row -> Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Top) { row.forEach { topic -> AppCard(p, Modifier.weight(1f).height(154.dp).clickable { onTopic(topic) }) { Box(Modifier.size(46.dp).clip(RoundedCornerShape(13.dp)).background(p.cardAlt), contentAlignment = Alignment.Center) { YBGameIcon(topic.icon, ybGameAccent(topic.icon), 24.dp) }; Spacer(Modifier.height(9.dp)); Text(topic.title, color = p.text, fontSize = 15.sp, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis); Spacer(Modifier.height(3.dp)); Text(topic.subtitle, color = p.muted, fontSize = 10.sp, lineHeight = 14.sp, maxLines = 2, overflow = TextOverflow.Ellipsis); Spacer(Modifier.weight(1f)); Text("Ders sayfası →", color = p.green, fontSize = 9.sp, fontWeight = FontWeight.Bold, maxLines = 1) } }; if (row.size == 1) Spacer(Modifier.weight(1f)) } }
        item { Column(Modifier.padding(horizontal = 16.dp)) { Spacer(Modifier.height(6.dp)); Text("İl Keşfi", color = p.text, fontSize = 19.sp, fontWeight = FontWeight.Black); Text("İl → bölge → doğal / ekonomik özellik bağlantısını kur.", color = p.muted, fontSize = 11.sp) } }
        items(GeographyData.provinces.chunked(2), key = { row -> row.first().name }) { row -> Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Top) { row.forEach { city -> AppCard(p, Modifier.weight(1f).height(132.dp).clickable { onProvince(city) }) { Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.LocationOn, null, tint = p.green, modifier = Modifier.size(21.dp)); Spacer(Modifier.width(7.dp)); Column(Modifier.weight(1f)) { Text(city.name, color = p.text, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis); Text(city.region, color = p.green, fontSize = 9.sp, fontWeight = FontWeight.Bold) } }; Spacer(Modifier.height(7.dp)); Text(city.clue, color = p.muted, fontSize = 10.sp, lineHeight = 14.sp, maxLines = 3, overflow = TextOverflow.Ellipsis); Spacer(Modifier.weight(1f)); Text("Detay →", color = p.green, fontSize = 9.sp, fontWeight = FontWeight.Bold) } }; if (row.size == 1) Spacer(Modifier.weight(1f)) } }
    }
}
@Composable private fun SectionTitle(text: String, p: AppPalette) = Text(text, color = p.text, fontSize = 19.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 16.dp))

@Composable private fun LessonV4(topic: Topic, p: AppPalette, prefs: SharedPreferences, onBack: () -> Unit, test: () -> Unit) { val lesson = remember(topic.title) { DeepLibrary.forTopic(topic) }; LazyColumn(contentPadding = PaddingValues(top = 5.dp, bottom = 28.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) { item { Row(Modifier.padding(horizontal = 6.dp), verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Geri", tint = p.text) }; Column(Modifier.weight(1f)) { Eyebrow("DERS SAYFASI", p.green); Text(topic.title, color = p.text, fontSize = 20.sp, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis) }; YBGameIcon(topic.icon, ybGameAccent(topic.icon), 23.dp) } }; item { AppCard(p, Modifier.padding(horizontal = 16.dp), dark = true) { Text(lesson.title, color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Black); Text(lesson.subtitle, color = p.green, fontSize = 12.sp, fontWeight = FontWeight.Bold, lineHeight = 18.sp); Spacer(Modifier.height(8.dp)); Text(lesson.summary, color = Color.White.copy(alpha = .9f), fontSize = 14.sp, lineHeight = 21.sp) } }; items(lesson.sections.indices.toList()) { index -> val section = lesson.sections[index]; AppCard(p, Modifier.padding(horizontal = 16.dp)) { Eyebrow("${index + 1}. ${section.title.uppercase()}", p.green); Spacer(Modifier.height(5.dp)); Text(section.body, color = p.text, fontSize = 13.sp, lineHeight = 20.sp); section.bullets.forEach { point -> Row(Modifier.padding(top = 7.dp), verticalAlignment = Alignment.Top) { Text("•", color = p.green, fontWeight = FontWeight.Black); Spacer(Modifier.width(7.dp)); Text(point, color = p.text, fontSize = 12.sp, lineHeight = 18.sp) } } } }; item { AppCard(p, Modifier.padding(horizontal = 16.dp), dark = true) { Eyebrow("KPSS SINAV TUZAĞI", p.gold); Spacer(Modifier.height(5.dp)); Text(lesson.examTrap, color = Color.White, fontSize = 13.sp, lineHeight = 20.sp) } }; item { AppCard(p, Modifier.padding(horizontal = 16.dp)) { Eyebrow("KENDİNİ YOKLA", p.green); lesson.recall.forEachIndexed { i, q -> Row(Modifier.padding(vertical = 5.dp), verticalAlignment = Alignment.Top) { Text("${i + 1}", color = p.green, fontWeight = FontWeight.Black); Spacer(Modifier.width(8.dp)); Text(q, color = p.text, fontSize = 13.sp, lineHeight = 19.sp) } }; Spacer(Modifier.height(7.dp)); Button(onClick = { prefs.edit().putBoolean("read_${topic.title}", true).apply(); test() }, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = p.green, contentColor = Color(0xFF052118))) { Text("Bu Konuyu Test Et", fontWeight = FontWeight.Black) } } } } }
@Composable private fun ProvinceV4(city: Province, p: AppPalette, onBack: () -> Unit) { LazyColumn(contentPadding = PaddingValues(top = 6.dp, bottom = 26.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) { item { Row(Modifier.padding(horizontal = 6.dp), verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Geri", tint = p.text) }; Column { Text(city.name, color = p.text, fontSize = 25.sp, fontWeight = FontWeight.Black); Text(city.region, color = p.green, fontWeight = FontWeight.Bold) } } }; item { AppCard(p, Modifier.padding(horizontal = 16.dp), dark = true) { Eyebrow("COĞRAFYA KARTI", p.green); Text(city.clue, color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Black, lineHeight = 25.sp) } }; item { SectionTitle("Bilmen Gerekenler", p) }; items(city.facts) { fact -> AppCard(p, Modifier.padding(horizontal = 16.dp)) { Row(verticalAlignment = Alignment.Top) { Text("✓", color = p.green, fontWeight = FontWeight.Black); Spacer(Modifier.width(10.dp)); Text(fact, color = p.text, fontSize = 13.sp, lineHeight = 19.sp) } } } } }
@Composable private fun ArenaV4(p: AppPalette, prefs: SharedPreferences, onBack: () -> Unit, launch: (SharedGameMode) -> Unit) { val solved = prefs.getInt("solved", 0); val xp = prefs.getInt("xp", 0); LazyColumn(contentPadding = PaddingValues(top = 8.dp, bottom = 28.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) { item { Row(Modifier.padding(horizontal = 6.dp), verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Geri", tint = p.text) }; Column(Modifier.weight(1f)) { Eyebrow("ARENA MERKEZİ", p.gold); Text("Bilgini sahaya çıkar.", color = p.text, fontSize = 24.sp, fontWeight = FontWeight.Black) }; Icon(YBIcons.Swords, null, tint = p.gold, modifier = Modifier.size(28.dp)) } }; item { AppCard(p, Modifier.padding(horizontal = 16.dp), dark = true) { Text("Liginin temeli bugün başlıyor.", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black); Text("Çözdüğün her tur sana deneyim ve skor kazandırır. Aynı cihazda tekrar tekrar oynayıp kendi rekorunu kır.", color = Color.White.copy(alpha = .74f), fontSize = 12.sp, lineHeight = 18.sp); Spacer(Modifier.height(10.dp)); Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) { ArenaStat("$solved", "Toplam Soru"); ArenaStat("$xp", "XP") } } }; item { SectionTitle("Arena Modları", p) }; items(SharedGameModes.arenaModes, key = { it.id }) { mode -> ArenaGameCard(p, mode, launch) }; item { SectionTitle("Nasıl oynanır?", p) }; item { AppCard(p, Modifier.padding(horizontal = 16.dp)) { Text("1  Modu seç", color = p.text, fontWeight = FontWeight.Black); Text("2  Soruları mümkün olduğunca hızlı ve doğru çöz", color = p.muted, fontSize = 12.sp); Text("3  Sonuç ekranında XP ve başarı yüzdeliğini gör", color = p.muted, fontSize = 12.sp); Text("4  Bir sonraki turda kendi skorunu geç", color = p.muted, fontSize = 12.sp) } } } }
@Composable private fun ArenaStat(value: String, title: String) { Column(Modifier.clip(RoundedCornerShape(12.dp)).background(Color.White.copy(alpha = .08f)).padding(horizontal = 12.dp, vertical = 8.dp)) { Text(value, color = Color.White, fontWeight = FontWeight.Black); Text(title, color = Color.White.copy(alpha = .58f), fontSize = 8.sp) } }
@Composable private fun ArenaGameCard(p: AppPalette, mode: SharedGameMode, launch: (SharedGameMode) -> Unit) { AppCard(p, Modifier.padding(horizontal = 16.dp).clickable { launch(mode) }) { Row(verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(46.dp).clip(RoundedCornerShape(13.dp)).background(p.gold.copy(alpha = .14f)), contentAlignment = Alignment.Center) { YBGameIcon(mode.icon, p.gold, 24.dp) }; Spacer(Modifier.width(11.dp)); Column(Modifier.weight(1f)) { Text(mode.title, color = p.text, fontSize = 16.sp, fontWeight = FontWeight.Black); Text(mode.subtitle, color = p.muted, fontSize = 11.sp, lineHeight = 17.sp); Text("${mode.questions} soru • +${mode.rewardXp} XP", color = p.gold, fontSize = 10.sp, fontWeight = FontWeight.Bold) }; Icon(Icons.Default.PlayArrow, null, tint = p.green) } } }

@Composable private fun EventsV4(p: AppPalette, launch: (SharedGameMode) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(top = 12.dp, bottom = 28.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Column(Modifier.padding(horizontal = 16.dp)) { Text("Etkinlikler", color = p.text, fontSize = 29.sp, fontWeight = FontWeight.Black); Text("Öğren, yarış, tekrar et.", color = p.green, fontSize = 12.sp, fontWeight = FontWeight.Bold) } }
        item { AppCard(p, Modifier.padding(horizontal = 16.dp), dark = true) { Row(verticalAlignment = Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Eyebrow("GÜNÜN AKIŞI", p.green); Text("Bugünün turunu seç.", color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Black); Text("Kısa oyunlar, hızlı tekrarlar ve Arena turları.", color = Color.White.copy(alpha = .7f), fontSize = 11.sp, lineHeight = 16.sp) }; Icon(Icons.Default.SportsEsports, null, tint = Color.White, modifier = Modifier.size(28.dp)) } } }
        item { SectionTitle("Çalışma Oyunları", p) }
        items(SharedGameModes.games.chunked(2), key = { row -> row.first().id }) { row -> EventGridRow(p, row, launch) }
        item { SectionTitle("Arena", p) }
        items(SharedGameModes.arenaModes.chunked(2), key = { row -> row.first().id }) { row -> EventGridRow(p, row, launch) }
    }
}

@Composable private fun EventGridRow(p: AppPalette, row: List<SharedGameMode>, launch: (SharedGameMode) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Top) {
        row.forEach { mode ->
            AppCard(p, Modifier.weight(1f).height(150.dp).clickable { launch(mode) }) {
                val accent = ybGameAccent(mode.icon, mode.arena)
                Box(Modifier.size(44.dp).clip(RoundedCornerShape(13.dp)).background(accent.copy(alpha = .13f)), contentAlignment = Alignment.Center) { YBGameIcon(mode.icon, accent, 24.dp) }
                Spacer(Modifier.height(8.dp))
                Text(mode.title, color = p.text, fontSize = 14.sp, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(3.dp))
                Text(mode.subtitle, color = p.muted, fontSize = 10.sp, lineHeight = 14.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.weight(1f))
                Text("${mode.questions} soru • +${mode.rewardXp} XP", color = accent, fontSize = 9.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                Text("Başla →", color = p.green, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
        }
        if (row.size == 1) Spacer(Modifier.weight(1f))
    }
}

@Composable private fun SettingsV4(p: AppPalette, prefs: SharedPreferences, dark: Boolean, setDark: (Boolean) -> Unit) {
    val context = LocalContext.current
    val activity = context as? Activity
    var notifications by rememberSaveable { mutableStateOf(prefs.getBoolean("notifications_enabled", false)) }
    var message by rememberSaveable { mutableStateOf<String?>(null) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var deletingAccount by rememberSaveable { mutableStateOf(false) }

    LazyColumn(contentPadding = PaddingValues(top = 12.dp, bottom = 28.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Column(Modifier.padding(horizontal = 16.dp)) {
                Text("Ayarlar", color = p.text, fontSize = 29.sp, fontWeight = FontWeight.Black)
                Text("Tema, bildirim, hesap, ilerleme ve güvenlik.", color = p.muted, fontSize = 12.sp)
            }
        }

        item {
            AppCard(p, Modifier.padding(horizontal = 16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DarkMode, null, tint = p.green, modifier = Modifier.size(23.dp))
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Karanlık tema", color = p.text, fontWeight = FontWeight.Black)
                        Text("Koyu arayüzü kalıcı olarak kullan", color = p.muted, fontSize = 10.sp)
                    }
                    Switch(checked = dark, onCheckedChange = setDark)
                }
            }
        }

        item {
            AppCard(p, Modifier.padding(horizontal = 16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(YBIcons.AvatarShield, null, tint = p.green, modifier = Modifier.size(23.dp))
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Bildirimler", color = p.text, fontWeight = FontWeight.Black)
                        Text("İsteğe bağlı çalışma ve duyuru bildirimleri", color = p.muted, fontSize = 10.sp)
                    }
                    Switch(
                        checked = notifications,
                        onCheckedChange = { value ->
                            if (value &&
                                android.os.Build.VERSION.SDK_INT >= 33 &&
                                activity?.checkSelfPermission("android.permission.POST_NOTIFICATIONS") != android.content.pm.PackageManager.PERMISSION_GRANTED
                            ) {
                                activity.requestPermissions(arrayOf("android.permission.POST_NOTIFICATIONS"), 9001)
                                message = "Bildirim izni istendi. İzin verdikten sonra tekrar aç."
                            } else {
                                notifications = value
                                prefs.edit().putBoolean("notifications_enabled", value).apply()
                                if (value) {
                                    MainScope().launch {
                                        NotificationHelper.registerCurrentToken(context)
                                        NotificationAutomation.syncAndSchedule(context)
                                    }
                                    message = "Otomatik bildirimler senkronlandı."
                                } else {
                                    MainScope().launch { NotificationHelper.deactivateCurrentToken() }
                                    NotificationHelper.cancelDaily(context)
                                    message = "Bildirimler kapatıldı."
                                }
                            }
                        }
                    )
                }

                Spacer(Modifier.height(9.dp))
                OutlinedButton(
                    onClick = {
                        if (!notifications) {
                            message = "Bildirimler kapalı. Test bildirimi gönderilmedi."
                        } else if (NotificationHelper.canNotify(context)) {
                            NotificationHelper.sendTest(context)
                            message = "Test bildirimi gönderildi."
                        } else if (android.os.Build.VERSION.SDK_INT >= 33) {
                            activity?.requestPermissions(arrayOf("android.permission.POST_NOTIFICATIONS"), 9001)
                            message = "Önce bildirim iznini ver, sonra düğmeye tekrar bas."
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(13.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = p.green)
                ) {
                    Icon(Icons.Default.NotificationsActive, null)
                    Spacer(Modifier.width(7.dp))
                    Text("Test bildirimi gönder", fontWeight = FontWeight.Bold)
                }

                if (message != null) {
                    Spacer(Modifier.height(6.dp))
                    Text(message!!, color = p.muted, fontSize = 10.sp)
                }
            }
        }

        item {
            AppCard(p, Modifier.padding(horizontal = 16.dp)) {
                Eyebrow("HESAP", p.red)
                Text("Hesabını sil", color = p.text, fontWeight = FontWeight.Black)
                Text(
                    "Hesabın ve ilişkili uygulama verilerin kalıcı olarak silinir. Bu işlem geri alınamaz.",
                    color = p.muted,
                    fontSize = 10.sp,
                    lineHeight = 16.sp
                )
                Spacer(Modifier.height(10.dp))
                OutlinedButton(
                    enabled = !deletingAccount,
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(13.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = p.red)
                ) {
                    Text(
                        if (deletingAccount) "Hesap siliniyor…" else "Hesabımı kalıcı olarak sil",
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        item {
            AppCard(p, Modifier.padding(horizontal = 16.dp)) {
                Eyebrow("İLERLEME", p.green)
                Text("Çözülen soru: " + prefs.getInt("solved", 0), color = p.text, fontWeight = FontWeight.Bold)
                Text("XP: " + prefs.getInt("xp", 0), color = p.text, fontWeight = FontWeight.Bold)
                Text("Doğru: " + prefs.getInt("correct", 0) + "  •  Yanlış: " + prefs.getInt("wrong", 0), color = p.muted, fontSize = 12.sp)
            }
        }

        item {
            AppCard(p, Modifier.padding(horizontal = 16.dp)) {
                Eyebrow("KILAVUZ", p.gold)
                Text("Çalışma sırası", color = p.text, fontSize = 16.sp, fontWeight = FontWeight.Black)
                Text("1. Kütüphaneden konuyu aç", color = p.muted, fontSize = 11.sp)
                Text("2. Ders sayfasını tamamen oku", color = p.muted, fontSize = 11.sp)
                Text("3. Kendini yokla", color = p.muted, fontSize = 11.sp)
                Text("4. Aynı konunun testini çöz", color = p.muted, fontSize = 11.sp)
            }
        }

        item {
            AppCard(p, Modifier.padding(horizontal = 16.dp), dark = true) {
                Eyebrow("UYGULAMA", p.gold)
                Text("Yurdunu Bil • 0.6.5", color = Color.White, fontWeight = FontWeight.Black)
                Text(
                    "Gerçek push bildirimleri • otomatik çalışma hatırlatmaları • hesap yönetimi • Arena • sosyal merkez",
                    color = Color.White.copy(alpha = .72f),
                    fontSize = 11.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { if (!deletingAccount) showDeleteDialog = false },
            title = { Text("Hesabı silmek istediğine emin misin?") },
            text = {
                Text("Profilin, bildirim cihaz kayıtların, ilerlemen, sosyal ve Arena ile ilişkili uygulama verilerin silinecek. Bu işlem geri alınamaz.")
            },
            confirmButton = {
                TextButton(
                    enabled = !deletingAccount,
                    onClick = {
                        deletingAccount = true
                        MainScope().launch {
                            runCatching {
                                SupabaseClientProvider.client.functions.invoke(
                                    function = "delete-account",
                                    body = buildJsonObject {}
                                )
                            }.onSuccess {
                                runCatching { SupabaseClientProvider.client.auth.signOut() }
                                prefs.edit().clear().apply()
                                showDeleteDialog = false
                                deletingAccount = false
                                context.startActivity(
                                    Intent(context, ModernLaunchActivity::class.java).apply {
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    }
                                )
                            }.onFailure {
                                deletingAccount = false
                                message = "Hesap silinemedi. Lütfen tekrar dene."
                            }
                        }
                    }
                ) { Text("Evet, sil", color = p.red, fontWeight = FontWeight.Black) }
            },
            dismissButton = {
                TextButton(
                    enabled = !deletingAccount,
                    onClick = { showDeleteDialog = false }
                ) { Text("Vazgeç", color = p.text) }
            }
        )
    }
}

@Composable private fun QuizV4(title: String, qs: List<Question>, mode: SharedGameMode, p: AppPalette, prefs: SharedPreferences, done: () -> Unit) {
    var index by rememberSaveable(title) { mutableIntStateOf(0) }; var selected by rememberSaveable(title) { mutableIntStateOf(-1) }; var correct by rememberSaveable(title) { mutableIntStateOf(0) }; var wrong by rememberSaveable(title) { mutableIntStateOf(0) }; var finished by rememberSaveable(title) { mutableStateOf(false) }; val q = qs.getOrNull(index)
    Column(Modifier.fillMaxSize().background(p.bg).statusBarsPadding().navigationBarsPadding().padding(horizontal = 16.dp, vertical = 8.dp)) {
        if (finished || q == null) { LazyColumn(contentPadding = PaddingValues(vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(11.dp)) { item { AppCard(p, dark = true) { Row(verticalAlignment = Alignment.CenterVertically) { Icon(YBIcons.Trophy, null, tint = p.green, modifier = Modifier.size(17.dp)); Spacer(Modifier.width(5.dp)); Eyebrow("TEST TAMAMLANDI", p.green) }; Text(title, color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.Black); Spacer(Modifier.height(8.dp)); Text("$correct doğru • $wrong yanlış", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black); Text("%${if (qs.isEmpty()) 0 else (correct * 100f / qs.size).roundToInt()} başarı", color = Color.White.copy(alpha = .72f)); Spacer(Modifier.height(14.dp)); GreenButton("Devam Et", p, done) } }; item { AppCard(p) { Eyebrow("TEKRAR TAKTİĞİ", p.gold); Text("Yanlış yaptığın soruların açıklamasını tekrar oku. Ardından aynı konunun ders sayfasına dön.", color = p.text, fontSize = 12.sp, lineHeight = 18.sp) } } } } else { Row(verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = done) { Icon(Icons.Default.ArrowBack, "Çık", tint = p.text) }; Column(Modifier.weight(1f)) { Eyebrow(title, p.green); Text("Soru ${index + 1} / ${qs.size}", color = p.text, fontWeight = FontWeight.Black) } }; LinearProgressIndicator(progress = { (index + 1f) / qs.size }, modifier = Modifier.fillMaxWidth().height(7.dp), color = p.green, trackColor = p.cardAlt); Spacer(Modifier.height(10.dp)); LazyColumn(contentPadding = PaddingValues(bottom = 20.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) { item { AppCard(p) { Text(q.topic, color = p.green, fontSize = 10.sp, fontWeight = FontWeight.Black); Spacer(Modifier.height(5.dp)); Text(q.text, color = p.text, fontSize = 17.sp, lineHeight = 24.sp, fontWeight = FontWeight.Bold) } }; items(q.options.indices.toList()) { i -> val right = i == q.correctIndex; val bg = when { selected == -1 -> p.card; selected == i && right -> p.green.copy(alpha = .18f); selected == i && !right -> p.red.copy(alpha = .16f); selected != -1 && right -> p.green.copy(alpha = .11f); else -> p.card }; Card(Modifier.fillMaxWidth().clickable(enabled = selected == -1) { selected = i; SmartQuestionSelector.record(prefs, q.topic, right); if (right) correct++ else wrong++ }, shape = RoundedCornerShape(15.dp), border = BorderStroke(1.dp, if (selected == i) p.green.copy(alpha = .45f) else p.border), colors = CardDefaults.cardColors(containerColor = bg)) { Row(Modifier.padding(14.dp), verticalAlignment = Alignment.Top) { Text("${('A'.code + i).toChar()}", color = p.green, fontWeight = FontWeight.Black); Spacer(Modifier.width(10.dp)); Text(q.options[i], color = p.text, fontSize = 13.sp, lineHeight = 19.sp, modifier = Modifier.weight(1f)); if (selected != -1 && right) Icon(Icons.Default.CheckCircle, null, tint = p.green) else if (selected == i) Icon(Icons.Default.Cancel, null, tint = p.red) } } }; if (selected != -1) { item { AppCard(p, dark = selected == q.correctIndex) { Eyebrow(if (selected == q.correctIndex) "DOĞRU" else "YANLIŞ", if (selected == q.correctIndex) p.green else p.red); Text(q.explanation, color = if (selected == q.correctIndex) Color.White else p.text, fontSize = 12.sp, lineHeight = 18.sp) } }; item { GreenButton(if (index + 1 == qs.size) "Sonucu Gör" else "Sonraki Soru", p) { if (index + 1 == qs.size) { ProgressTracker.recordQuiz(prefs, mode, qs.size, correct, wrong); finished = true } else { index++; selected = -1 } } } } } }
    }
}

@Composable private fun ProgressBarRow(label: String, value: Int, p: AppPalette) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Text(label, color = p.text, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(122.dp))
        LinearProgressIndicator(progress = { value.coerceIn(0, 100) / 100f }, modifier = Modifier.weight(1f).height(7.dp).clip(RoundedCornerShape(8.dp)), color = p.green, trackColor = p.cardAlt)
        Spacer(Modifier.width(8.dp))
        Text("%$value", color = p.muted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}
@Composable private fun HomeV5(p: AppPalette, prefs: SharedPreferences, quick: () -> Unit, arena: () -> Unit) {
    var factIndex by rememberSaveable { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(180_000L)
            factIndex = (factIndex + 1) % CurrentFactFeed.all.size
        }
    }
    val solved = prefs.getInt("solved", 0); val correct = prefs.getInt("correct", 0); val wrong = prefs.getInt("wrong", 0)
    val xp = prefs.getInt("xp", 0); val streak = prefs.getInt("streak", 0)
    val todaySolved = prefs.getInt("today_solved", 0); val todayCorrect = prefs.getInt("today_correct", 0); val todayXp = prefs.getInt("today_xp", 0)
    val accuracy = if (solved == 0) 0 else (correct * 100f / solved).roundToInt()
    val todayGoal = 20; val todayPct = (todaySolved * 100 / todayGoal).coerceIn(0, 100)
    val weak = GeographyData.topics.map { it to (SmartQuestionSelector.accuracy(prefs, it.title) * 100f).roundToInt() }.sortedBy { it.second }.take(2)
    LazyColumn(contentPadding = PaddingValues(bottom = 26.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Box(Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(Color(0xFF042117), Color(0xFF087451))), RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)).statusBarsPadding().padding(20.dp)) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(52.dp).clip(RoundedCornerShape(15.dp)).background(Color.White.copy(alpha = .08f)).padding(5.dp)) { Image(painterResource(R.drawable.yurdunu_bil_app_icon), "Yurdunu Bil", Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp))) }
                    Spacer(Modifier.width(11.dp)); Column(Modifier.weight(1f)) { Text("Yurdunu Bil", color = Color.White, fontSize = 27.sp, fontWeight = FontWeight.Black); Text("KPSS Önlisans • Türkiye Coğrafyası", color = Color(0xFFC9F8E1), fontSize = 11.sp) }
                    Text("Lv.${1 + xp / 500}", color = Color.White, fontWeight = FontWeight.Black)
                }
                Spacer(Modifier.height(14.dp)); Text("Bugün senin çalışma günün.", color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(Modifier.height(6.dp)); Text(if (todaySolved == 0) "12 dakikalık bir rota hazır. Başlamak yeterli." else "${todaySolved} soru çözdün • ${todayCorrect} doğru • +${todayXp} XP", color = Color.White.copy(alpha = .76f), fontSize = 12.sp)
                Spacer(Modifier.height(12.dp)); Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { StatChip("${todaySolved}/${todayGoal}", "BUGÜN"); StatChip($streak, "SERİ"); StatChip("%$accuracy", "GENEL") }
            }
        } }
        item { AppCard(p, Modifier.padding(horizontal = 16.dp), dark = true) {
            Eyebrow("BUGÜNÜN ROTASI", p.green); Text(if (todaySolved == 0) "İlk turunu başlat." else "Ritmi koru.", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
            Text("Önce zayıf konular → sonra kısa tekrar → en son güncel bilgi.", color = Color.White.copy(alpha = .74f), fontSize = 12.sp, lineHeight = 18.sp, modifier = Modifier.padding(top = 5.dp))
            Spacer(Modifier.height(10.dp)); LinearProgressIndicator(progress = { todayPct / 100f }, modifier = Modifier.fillMaxWidth().height(9.dp).clip(RoundedCornerShape(9.dp)), color = p.green, trackColor = Color.White.copy(alpha = .1f))
            Spacer(Modifier.height(10.dp)); GreenButton(if (todaySolved == 0) "Başla • 10 Soru" else "Devam Et • 10 Soru", p, quick)
        } }
        item { AppCard(p, Modifier.padding(horizontal = 16.dp)) {
            Eyebrow("SANA ÖZEL", p.gold); Text("Bugün önce bunlara bak.", color = p.text, fontSize = 19.sp, fontWeight = FontWeight.Black); Spacer(Modifier.height(8.dp))
            weak.forEachIndexed { index, pair ->
                Row(Modifier.fillMaxWidth().padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(28.dp).clip(RoundedCornerShape(9.dp)).background(p.red.copy(alpha = .12f)), contentAlignment = Alignment.Center) { Text("${index + 1}", color = p.red, fontWeight = FontWeight.Black, fontSize = 11.sp) }
                    Spacer(Modifier.width(9.dp)); Column(Modifier.weight(1f)) { Text(pair.first.title, color = p.text, fontSize = 13.sp, fontWeight = FontWeight.Bold); Text("Performansına göre tekrar öneriliyor", color = p.muted, fontSize = 10.sp) }
                    Text("%${pair.second}", color = p.red, fontWeight = FontWeight.Black, fontSize = 12.sp)
                }
            }
        } }
        item { Row(Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AppCard(p, Modifier.weight(1f)) { Eyebrow("İLERLEME", p.green); Text("$solved", color = p.text, fontSize = 25.sp, fontWeight = FontWeight.Black); Text("çözülen soru", color = p.muted, fontSize = 10.sp) }
            AppCard(p, Modifier.weight(1f)) { Eyebrow("DOĞRULUK", p.green); Text("%$accuracy", color = p.text, fontSize = 25.sp, fontWeight = FontWeight.Black); Text("$correct doğru • $wrong yanlış", color = p.muted, fontSize = 10.sp) }
        } }
        item {
            val fact = CurrentFactFeed.all[factIndex.coerceIn(0, CurrentFactFeed.all.lastIndex)]
            AppCard(p, Modifier.padding(horizontal = 16.dp).clickable {
                factIndex = (factIndex + 1) % CurrentFactFeed.all.size
            }, dark = true) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(44.dp).clip(RoundedCornerShape(13.dp)).background(p.green.copy(alpha = .14f)), contentAlignment = Alignment.Center) {
                        YBGameIcon(fact.icon, p.green, 23.dp)
                    }
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Eyebrow("COĞRAFYA NOTU • " + (factIndex + 1) + "/" + CurrentFactFeed.all.size, p.gold)
                        Text(fact.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Icon(Icons.Default.SwapHoriz, null, tint = Color.White.copy(alpha = .7f))
                }
                Spacer(Modifier.height(8.dp))
                Text(fact.value, color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.Black)
                Text(fact.detail, color = Color.White.copy(alpha = .76f), fontSize = 11.sp, lineHeight = 17.sp, modifier = Modifier.padding(top = 4.dp))
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(fact.source + " • " + fact.year, color = p.green, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Text("3 dk'da yenilenir", color = Color.White.copy(alpha = .52f), fontSize = 9.sp)
                }
            }
        }
        item { AppCard(p, Modifier.padding(horizontal = 16.dp), dark = true) {
            Row(verticalAlignment = Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Eyebrow("ARENA", p.gold); Text("Bilgini sahaya çıkar.", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black); Text("1v1 • hız • bölge • Türkiye Ustası", color = Color.White.copy(alpha = .68f), fontSize = 11.sp) }; Icon(YBIcons.Swords, null, tint = p.gold, modifier = Modifier.size(30.dp)) }
            Spacer(Modifier.height(9.dp)); Button(onClick = arena, modifier = Modifier.fillMaxWidth().height(46.dp), shape = RoundedCornerShape(13.dp), colors = ButtonDefaults.buttonColors(containerColor = p.gold, contentColor = Color(0xFF162118))) { Text("Arena'ya Git", fontWeight = FontWeight.Black) }
        } }
    }
}

@Composable private fun LibraryV5(p: AppPalette, prefs: SharedPreferences, onTopic: (Topic) -> Unit, onProvince: (Province) -> Unit) {
    var search by rememberSaveable { mutableStateOf("") }
    val topics = GeographyData.topics.filter { it.title.contains(search, true) || it.subtitle.contains(search, true) }
    val measured = topics.filter { prefs.getInt("topic_attempts_" + it.title, 0) > 0 }
    val average = if (measured.isEmpty()) 0 else measured.map { (SmartQuestionSelector.accuracy(prefs, it.title) * 100f).roundToInt() }.average().roundToInt()
    LazyColumn(contentPadding = PaddingValues(top = 12.dp, bottom = 28.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Column(Modifier.padding(horizontal = 16.dp)) {
                Text("Kütüphane", color = p.text, fontSize = 29.sp, fontWeight = FontWeight.Black)
                Text("Öğren → yokla → tekrar et → ustalaş.", color = p.green, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = search, onValueChange = { search = it }, modifier = Modifier.fillMaxWidth(), singleLine = true, placeholder = { Text("Konu ara") }, leadingIcon = { Icon(Icons.Default.Search, null) }, shape = RoundedCornerShape(15.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = p.green, unfocusedBorderColor = p.border, cursorColor = p.green, focusedTextColor = p.text, unfocusedTextColor = p.text))
            }
        }
        item {
            AppCard(p, Modifier.padding(horizontal = 16.dp), dark = true) {
                Eyebrow("ÇALIŞMA KÜTÜPHANESİ", p.green)
                Text(if (average == 0) "Kendi çalışma haritanı oluştur." else "%" + average + " ortalama konu performansı", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
                Text("Konular performansına göre önceliklendirilir; güçlü olduğun alanlar aralıklı olarak tekrar edilir.", color = Color.White.copy(alpha = .72f), fontSize = 12.sp, lineHeight = 18.sp, modifier = Modifier.padding(top = 5.dp))
            }
        }
        item { SectionTitle("Konu Bankası", p) }
        items(topics, key = { it.title }) { topic ->
            val score = (SmartQuestionSelector.accuracy(prefs, topic.title) * 100f).roundToInt()
            AppCard(p, Modifier.padding(horizontal = 16.dp).clickable { onTopic(topic) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(p.cardAlt), contentAlignment = Alignment.Center) { YBGameIcon(topic.icon, ybGameAccent(topic.icon), 25.dp) }
                    Spacer(Modifier.width(11.dp))
                    Column(Modifier.weight(1f)) {
                        Text(topic.title, color = p.text, fontSize = 15.sp, fontWeight = FontWeight.Black)
                        Text(topic.subtitle, color = p.muted, fontSize = 10.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        Spacer(Modifier.height(6.dp))
                        LinearProgressIndicator(progress = { score.coerceIn(0, 100) / 100f }, modifier = Modifier.fillMaxWidth().height(7.dp).clip(RoundedCornerShape(8.dp)), color = p.green, trackColor = p.cardAlt)
                        Text(score.toString() + "% öğrenme", color = p.muted, fontSize = 9.sp, modifier = Modifier.padding(top = 3.dp))
                    }
                    Spacer(Modifier.width(8.dp)); Icon(Icons.Default.ChevronRight, null, tint = p.green)
                }
            }
        }
        item { SectionTitle("İl Keşfi", p) }
        items(GeographyData.provinces.chunked(2), key = { it.first().name }) { row ->
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { city ->
                    AppCard(p, Modifier.weight(1f).height(116.dp).clickable { onProvince(city) }) {
                        Text(city.name, color = p.text, fontWeight = FontWeight.Black)
                        Text(city.region, color = p.green, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text(city.clue, color = p.muted, fontSize = 10.sp, maxLines = 3, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 5.dp))
                    }
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable private fun EventsV5(p: AppPalette, launch: (SharedGameMode) -> Unit) {
    val games = SharedGameModes.games
    val quick = games.filter { it.id == "quick" || it.id == "chain" || it.id == "current" }
    val topic = games.filter { it.id != "quick" && it.id != "chain" && it.id != "current" }
    LazyColumn(contentPadding = PaddingValues(top = 12.dp, bottom = 28.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Column(Modifier.padding(horizontal = 16.dp)) { Text("Etkinlikler", color = p.text, fontSize = 29.sp, fontWeight = FontWeight.Black); Text("Kısa çalış, tekrar et, sonra yarış.", color = p.green, fontSize = 12.sp, fontWeight = FontWeight.Bold) } }
        item { AppCard(p, Modifier.padding(horizontal = 16.dp), dark = true) { Eyebrow("BUGÜN", p.green); Text("Vaktine göre ilerle.", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black); Text("Hızlı tur → konu oyunu → Arena.", color = Color.White.copy(alpha = .72f), fontSize = 12.sp) } }
        item { SectionTitle("Hızlı Çalış", p) }
        items(quick, key = { it.id }) { mode ->
            AppCard(p, Modifier.padding(horizontal = 16.dp).clickable { launch(mode) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(46.dp).clip(RoundedCornerShape(13.dp)).background(p.cardAlt), contentAlignment = Alignment.Center) { YBGameIcon(mode.icon, if (mode.id == "current") p.gold else p.green, 24.dp) }
                    Spacer(Modifier.width(11.dp)); Column(Modifier.weight(1f)) { Text(mode.title, color = p.text, fontSize = 16.sp, fontWeight = FontWeight.Black); Text(mode.subtitle, color = p.muted, fontSize = 11.sp, maxLines = 2, overflow = TextOverflow.Ellipsis); Text(mode.questions.toString() + " soru • +" + mode.rewardXp + " XP", color = if (mode.id == "current") p.gold else p.green, fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                    Icon(Icons.Default.PlayArrow, null, tint = p.green)
                }
            }
        }
        item { SectionTitle("Konu Oyunları", p) }
        items(topic, key = { it.id }) { mode ->
            AppCard(p, Modifier.padding(horizontal = 16.dp).clickable { launch(mode) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(46.dp).clip(RoundedCornerShape(13.dp)).background(p.green.copy(alpha = .1f)), contentAlignment = Alignment.Center) { YBGameIcon(mode.icon, p.green, 24.dp) }
                    Spacer(Modifier.width(11.dp)); Column(Modifier.weight(1f)) { Text(mode.title, color = p.text, fontSize = 16.sp, fontWeight = FontWeight.Black); Text(mode.subtitle, color = p.muted, fontSize = 11.sp, maxLines = 2, overflow = TextOverflow.Ellipsis); Text(mode.questions.toString() + " soru • +" + mode.rewardXp + " XP", color = p.green, fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                    Icon(Icons.Default.PlayArrow, null, tint = p.green)
                }
            }
        }
        item {
            val arenaMode = SharedGameModes.arena.firstOrNull()
            AppCard(p, Modifier.padding(horizontal = 16.dp), dark = true) {
                Eyebrow("REKABET", p.gold); Text("Bilgini sahaya çıkar.", color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Black); Text("1v1 • hız • bölge • Türkiye Ustası", color = Color.White.copy(alpha = .7f), fontSize = 11.sp)
                Spacer(Modifier.height(8.dp)); Button(onClick = { if (arenaMode != null) launch(arenaMode) }, modifier = Modifier.fillMaxWidth().height(46.dp), shape = RoundedCornerShape(13.dp), colors = ButtonDefaults.buttonColors(containerColor = p.gold, contentColor = Color(0xFF162118))) { Text("Arena'ya Git", fontWeight = FontWeight.Black) }
            }
        }
    }
}
