package tr.yurdunubil.app

import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val LSDeep = Color(0xFF06221B)
private val LSGreen = Color(0xFF18C986)
private val LSMint = Color(0xFFC9F8E1)
private val LSGold = Color(0xFFFFC857)
private val LSBackground = Color(0xFFF3F8F5)
private val LSMuted = Color(0xFF70847B)
private val LSDarkBg = Color(0xFF07110E)
private val LSDarkSurface = Color(0xFF10221C)
private val LSDarkSurface2 = Color(0xFF17372D)
private val LSDarkText = Color(0xFFF1F7F4)
private val LSDarkMuted = Color(0xFF9AB4A9)

data class StudyLesson(val title: String, val intro: String, val keyPoints: List<String>, val examTip: String)

object LibraryStudyData {
    private val lessons = mapOf(
        "Coğrafi Konum" to StudyLesson("Coğrafi Konum", "Türkiye'nin konumunu matematik ve özel konum olarak iki pencereden düşün.", listOf("36°–42° Kuzey paralelleri ve 26°–45° Doğu meridyenleri arasındadır.", "Orta kuşakta yer aldığı için dört mevsim belirgindir.", "Asya ile Avrupa arasındaki geçiş konumu ulaşım ve jeopolitiği etkiler.", "Boğazlar stratejik geçiş yollarıdır."), "Paralel = enlem, meridyen = boylam ayrımını önce kur."),
        "Yer Şekilleri" to StudyLesson("Yer Şekilleri", "Dağ, ova, plato ve vadileri oluşum ve uzanışlarıyla birlikte öğren.", listOf("Türkiye genç oluşumlu ve engebelidir.", "Kuzey Anadolu Dağları ve Toroslar genel olarak doğu-batı uzanır.", "İç kesimlerde plato ve ovalar yaygındır.", "Karstik şekiller kalkerli arazilerde gelişir."), "Dağların uzanışı ulaşım ve kıyı-İç kesim ilişkisini etkiler."),
        "Su Varlığı" to StudyLesson("Su Varlığı", "Akarsu ve gölleri kaynak, rejim, havza ve yer şekilleriyle anlamlandır.", listOf("Akarsu rejimleri çoğunlukla düzensizdir.", "Açık ve kapalı havzalar bulunur.", "Göller tektonik, karstik, volkanik ve set oluşumlu olabilir.", "Kıyı şekilleri liman ve ulaşımı etkiler."), "Akarsu sorularında rejim, debi, havza ve enerji kavramlarını ayrı değerlendir."),
        "İklim ve Bitki" to StudyLesson("İklim ve Bitki", "Sıcaklık, yağış, yükselti ve denize uzaklık ilişkisini kur.", listOf("Karadeniz'de yağış yıl içine daha düzenli dağılır.", "Akdeniz'de yazlar sıcak-kurak, kışlar ılık-yağışlıdır.", "İç ve Doğu Anadolu'da karasallık ve yükselti etkilidir.", "Bitki örtüsü iklimin doğal sonucudur."), "Önce yağış rejimini, sonra bitki örtüsünü tahmin et."),
        "Nüfus ve Yerleşme" to StudyLesson("Nüfus ve Yerleşme", "Nüfusu iklim, yükselti, tarım, sanayi, ulaşım ve hizmetlerle birlikte yorumla.", listOf("Sanayi ve hizmet merkezleri nüfusu çeker.", "Yüksek ve engebeli alanlarda yerleşme seyrektir.", "Göçte eğitim, sağlık ve istihdam önemli etkenlerdir.", "Yoğunluk nüfusun alana dağılışını ifade eder."), "Yoğunluk = nüfus / alan; büyük nüfus her zaman yoğunluk değildir."),
        "Tarım ve Hayvancılık" to StudyLesson("Tarım ve Hayvancılık", "Ürünleri iklim, su, toprak ve pazar şartlarıyla eşleştir.", listOf("Tahıllar karasal alanlarda önemlidir.", "Çay ve fındık nemli Karadeniz koşullarıyla ilişkilidir.", "Zeytin ve turunçgiller ılıman kış ister.", "Hayvancılık mera ve bitki örtüsüyle ilişkilidir."), "Ürün sorusunda önce sıcaklık ve yağış ipuçlarını ara."),
        "Maden ve Enerji" to StudyLesson("Maden ve Enerji", "Madenleri kaynak türü, jeolojik yapı ve enerji üretimiyle eşleştir.", listOf("Taş kömürü Zonguldak çevresinin klasik eşleştirmesidir.", "Linyit birçok termik santral için kullanılır.", "Hidroelektrik potansiyel yükselti farkı ve akarsularla ilişkilidir.", "Güneş ve rüzgâr potansiyeli iklim ve yer şekillerinden etkilenir."), "Madenin çıkarıldığı yer ile enerjinin üretildiği yeri karıştırma."),
        "Sanayi ve Ulaşım" to StudyLesson("Sanayi ve Ulaşım", "Sanayiyi hammadde, pazar, ulaşım, sermaye ve iş gücünün ortak sonucu olarak düşün.", listOf("Marmara sanayide yoğunlaşan önemli bölgedir.", "Limanlar dış ticareti destekler.", "Ulaşım güzergâhları yer şekillerinden etkilenir.", "Sanayi nüfus ve kentleşmeyi hızlandırabilir."), "Sanayi sorularında hammadde + pazar + ulaşım + iş gücünü birlikte düşün."),
        "Turizm" to StudyLesson("Turizm", "Kıyı, dağ, tarih ve kültür varlıklarını turizm türleriyle eşleştir.", listOf("Akdeniz ve Ege'de deniz turizmi güçlüdür.", "Uludağ ve Palandöken kış turizmine örnektir.", "Kapadokya doğal-kültürel turizmin önemli örneğidir.", "Ulaşılabilirlik ve mevsim uzunluğu önemlidir."), "Turizm türünü önce doğal veya kültürel unsurla eşleştir."),
        "Bölgeler" to StudyLesson("Bölgeler", "7 bölgeyi iklim, yer şekli, tarım ve ekonomi imzalarıyla karşılaştır.", listOf("Marmara: nüfus, sanayi ve ticaret.", "Ege: girintili-çıkıntılı kıyı ve tarım.", "Akdeniz: turizm, seracılık ve Toroslar.", "Karadeniz: dağlık kıyı, orman ve yağış.", "İç Anadolu: karasal iklim ve tahıl.", "Doğu Anadolu: yüksek yükselti ve sert karasallık.", "Güneydoğu Anadolu: sıcak koşullar ve GAP."), "Bölge sorularında iklim + yer şekli + ekonomik faaliyet üçlüsünü kullan."),
        "Doğal Afetler" to StudyLesson("Doğal Afetler", "Afetleri oluşum koşullarıyla öğren; şehir adı ezberinden daha kalıcıdır.", listOf("Deprem aktif faylarla ilişkilidir.", "Heyelan eğim, yağış ve zeminle artar.", "Erozyon bitki örtüsü zayıf alanlarda artabilir.", "Çığ yüksek ve eğimli kar örtülü sahalarda görülür."), "Soruda önce neden burada bağlantısını kur."),
        "Harita Bilgisi" to StudyLesson("Harita Bilgisi", "Ölçek, yön, yükselti ve sembolleri birlikte okuyarak haritayı çöz.", listOf("Büyük ölçek daha ayrıntılı gösterir.", "İzohipsler eş yükselti eğrileridir.", "İzohipslerin sıklaşması eğimin arttığını gösterir.", "Profil arazinin kesit görünüşüdür."), "Ölçek büyüdükçe ayrıntı artar ve ölçek paydası küçülür.")
    )
    fun forTopic(topic: Topic): StudyLesson = lessons[topic.title] ?: StudyLesson(topic.title, topic.subtitle, topic.lessons.map { "$it konusunu temel kavramlarıyla öğren." }, "Önce kavramı anla, sonra soru çöz.")
}

@Composable
fun LibraryStudyScreen(topic: Topic, onBack: () -> Unit, onQuiz: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val darkMode = remember { context.getSharedPreferences("yurdunu_bil_native", Context.MODE_PRIVATE).getBoolean("dark_mode", false) }
    val lesson = remember(topic.title) { LibraryStudyData.forTopic(topic) }
    var cardIndex by remember { mutableIntStateOf(0) }
    var revealed by remember { mutableStateOf(false) }
    val total = lesson.keyPoints.size
    val bg = if (darkMode) LSDarkBg else LSBackground
    val surface = if (darkMode) LSDarkSurface else Color.White
    val text = if (darkMode) LSDarkText else LSDeep
    val muted = if (darkMode) LSDarkMuted else LSMuted

    Column(Modifier.fillMaxSize().background(bg).statusBarsPadding().navigationBarsPadding()) {
        Row(Modifier.fillMaxWidth().padding(start = 8.dp, end = 12.dp, top = 7.dp, bottom = 3.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Geri", tint = text) }
            Column(Modifier.weight(1f)) {
                Text("KARTLA ÇALIŞ", color = LSGreen, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.3.sp)
                Text(topic.title, color = text, fontSize = 21.sp, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Surface(color = LSGreen.copy(alpha = .11f), shape = RoundedCornerShape(12.dp)) {
                Text("${cardIndex + 1}/$total", color = LSGreen, fontSize = 10.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 9.dp, vertical = 7.dp))
            }
        }
        LinearProgressIndicator(progress = { (cardIndex + 1f) / total }, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(7.dp).clip(RoundedCornerShape(8.dp)), color = LSGreen, trackColor = if (darkMode) LSDarkSurface2 else LSMint)

        LazyColumn(contentPadding = PaddingValues(16.dp, 14.dp, 16.dp, 28.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Card(Modifier.fillMaxWidth(), RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = if (darkMode) LSDarkSurface2 else LSMint)) {
                    Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(43.dp).clip(RoundedCornerShape(14.dp)).background(LSGreen.copy(alpha = .15f)), contentAlignment = Alignment.Center) { Icon(Icons.Default.AutoStories, null, tint = LSGreen, modifier = Modifier.size(22.dp)) }
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("ÖNCE ANLA", color = LSGreen, fontSize = 9.sp, fontWeight = FontWeight.Black, letterSpacing = 1.0.sp)
                                Spacer(Modifier.width(7.dp))
                                Box(Modifier.size(5.dp).clip(RoundedCornerShape(5.dp)).background(LSGreen))
                            }
                            Spacer(Modifier.height(3.dp))
                            Text(lesson.intro, color = text.copy(alpha = .82f), fontSize = 10.sp, lineHeight = 15.sp)
                        }
                    }
                }
            }
            item {
                val point = lesson.keyPoints[cardIndex]
                Card(
                    Modifier.fillMaxWidth().heightIn(min = 235.dp).clickable { revealed = !revealed },
                    RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = if (revealed) LSDeep else surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (revealed) Color.White.copy(alpha = .05f) else LSGreen.copy(alpha = .08f))
                ) {
                    Box(Modifier.fillMaxSize().background(if (revealed) Brush.verticalGradient(listOf(Color(0xFF0B3A2E), LSDeep)) else Brush.verticalGradient(listOf(if (darkMode) LSDarkSurface else Color.White, if (darkMode) Color(0xFF0C1D18) else Color(0xFFF8FCFA))))) {
                        Column(Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Surface(color = if (revealed) LSGold.copy(alpha = .12f) else LSGreen.copy(alpha = .10f), shape = RoundedCornerShape(50)) {
                                Row(Modifier.padding(horizontal = 11.dp, vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(if (revealed) Icons.Default.CheckCircle else Icons.Default.TouchApp, null, tint = if (revealed) LSGold else LSGreen, modifier = Modifier.size(14.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text(if (revealed) "CEVABI YAKALA" else "KENDİNİ DENE", color = if (revealed) LSGold else LSGreen, fontSize = 8.sp, fontWeight = FontWeight.Black, letterSpacing = 1.0.sp)
                                }
                            }
                            Spacer(Modifier.height(20.dp))
                            AnimatedContent(targetState = revealed, label = "flashcard-face") { showAnswer ->
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(if (showAnswer) point else "Bu bilgiyi kendin hatırlamaya çalış.", color = if (showAnswer) Color.White else text, fontSize = if (showAnswer) 18.sp else 19.sp, lineHeight = if (showAnswer) 27.sp else 27.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
                                    Spacer(Modifier.height(13.dp))
                                    Text(if (showAnswer) "Karta tekrar dokun • soruyu zihninde yeniden kur" else "Cevabı görmek için karta dokun", color = if (showAnswer) Color.White.copy(alpha = .56f) else muted, fontSize = 10.sp, textAlign = TextAlign.Center)
                                }
                            }
                            Spacer(Modifier.height(16.dp))
                            Text("Kartı dokunarak çevir", color = if (revealed) Color.White.copy(alpha = .35f) else muted, fontSize = 8.sp)
                        }
                    }
                }
            }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = { if (cardIndex > 0) { cardIndex--; revealed = false } }, enabled = cardIndex > 0, modifier = Modifier.weight(1f).height(49.dp), shape = RoundedCornerShape(16.dp), border = androidx.compose.foundation.BorderStroke(1.dp, if (darkMode) Color.White.copy(alpha = .08f) else Color.Black.copy(alpha = .08f))) {
                        Icon(Icons.Default.ArrowBack, null); Spacer(Modifier.width(5.dp)); Text("Önceki", fontWeight = FontWeight.Bold)
                    }
                    Button(onClick = { if (cardIndex < total - 1) { cardIndex++; revealed = false } else revealed = true }, modifier = Modifier.weight(1f).height(49.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = LSGreen, contentColor = LSDeep)) {
                        Text(if (cardIndex == total - 1) "Cevabı Gör" else "Sonraki", fontWeight = FontWeight.Black); Spacer(Modifier.width(5.dp)); Icon(Icons.Default.ArrowForward, null)
                    }
                }
            }
            item {
                Card(Modifier.fillMaxWidth(), RoundedCornerShape(19.dp), colors = CardDefaults.cardColors(containerColor = if (darkMode) LSDarkSurface2 else LSMint)) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
                        Box(Modifier.size(35.dp).clip(RoundedCornerShape(11.dp)).background(LSGold.copy(alpha = .12f)), contentAlignment = Alignment.Center) { Icon(Icons.Default.Lightbulb, null, tint = LSGold, modifier = Modifier.size(19.dp)) }
                        Spacer(Modifier.width(9.dp))
                        Column(Modifier.weight(1f)) {
                            Text("KPSS İPUCU", color = LSGold, fontSize = 9.sp, fontWeight = FontWeight.Black, letterSpacing = 1.0.sp)
                            Spacer(Modifier.height(3.dp))
                            Text(lesson.examTip, color = text, fontSize = 11.sp, lineHeight = 17.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
            item {
                Button(onClick = onQuiz, modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(18.dp), colors = ButtonDefaults.buttonColors(containerColor = if (darkMode) LSGreen else LSDeep, contentColor = if (darkMode) LSDeep else Color.White)) {
                    Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(21.dp)); Spacer(Modifier.width(8.dp)); Text("Kartları Bitir → Konuyu Test Et", fontWeight = FontWeight.Black)
                }
            }
            item {
                TextButton(onClick = { cardIndex = 0; revealed = false }, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Default.Replay, null); Spacer(Modifier.width(5.dp)); Text("Kartları baştan başlat", fontWeight = FontWeight.Bold) }
            }
        }
    }
}
