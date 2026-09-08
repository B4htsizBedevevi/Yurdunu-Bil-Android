package tr.yurdunubil.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val LSDeep = Color(0xFF06221B)
private val LSGreen = Color(0xFF18C986)
private val LSMint = Color(0xFFC9F8E1)
private val LSGold = Color(0xFFFFC857)
private val LSBackground = Color(0xFFF3F8F5)
private val LSMuted = Color(0xFF70847B)

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
    val lesson = remember(topic.title) { LibraryStudyData.forTopic(topic) }
    Box(Modifier.fillMaxSize().background(LSBackground).statusBarsPadding().navigationBarsPadding()) {
        LazyColumn(contentPadding = PaddingValues(16.dp, 5.dp, 16.dp, 28.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Geri", tint = LSDeep) }
                    Column(Modifier.weight(1f)) { Text("ÇALIŞMA", color = LSGreen, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.2.sp); Text(topic.title, color = LSDeep, fontSize = 22.sp, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                    Text(topic.icon, fontSize = 18.sp)
                }
            }
            item { StudyCard(dark = true) { Row(verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(46.dp).clip(CircleShape).background(LSGreen.copy(alpha = .13f)), contentAlignment = Alignment.Center) { Icon(Icons.Default.AutoStories, null, tint = LSGreen) }; Spacer(Modifier.width(10.dp)); Column(Modifier.weight(1f)) { Text("ÖNCE ANLA", color = LSMint, fontSize = 10.sp, fontWeight = FontWeight.Black); Spacer(Modifier.height(4.dp)); Text(lesson.intro, color = Color.White, fontSize = 15.sp, lineHeight = 21.sp, fontWeight = FontWeight.SemiBold) } } } }
            item { Text("Temel Bilgiler", color = LSDeep, fontSize = 18.sp, fontWeight = FontWeight.Black) }
            items(lesson.keyPoints) { point -> StudyCard { Row(verticalAlignment = Alignment.Top) { Text("✓", color = LSGreen, fontSize = 16.sp, fontWeight = FontWeight.Black); Spacer(Modifier.width(9.dp)); Text(point, color = LSDeep, fontSize = 13.sp, lineHeight = 19.sp, modifier = Modifier.weight(1f)) } } }
            item { StudyCard { Row(verticalAlignment = Alignment.Top) { Icon(Icons.Default.Lightbulb, null, tint = LSGold); Spacer(Modifier.width(9.dp)); Column(Modifier.weight(1f)) { Text("KPSS İPUCU", color = LSGold, fontSize = 10.sp, fontWeight = FontWeight.Black); Spacer(Modifier.height(4.dp)); Text(lesson.examTip, color = LSDeep, fontSize = 13.sp, lineHeight = 19.sp, fontWeight = FontWeight.SemiBold) } } } }
            item { Button(onClick = onQuiz, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(17.dp), colors = ButtonDefaults.buttonColors(containerColor = LSDeep)) { Icon(Icons.Default.PlayArrow, null); Spacer(Modifier.width(7.dp)); Text("Konuyu Test Et", fontWeight = FontWeight.Bold) } }
        }
    }
}

@Composable private fun StudyCard(dark: Boolean = false, content: @Composable ColumnScope.() -> Unit) { Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(if (dark) Brush.linearGradient(listOf(LSDeep, Color(0xFF0B342B))) else Brush.linearGradient(listOf(Color.White, Color.White))).padding(16.dp), content = content) }
