package tr.yurdunubil.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val LSDeep = Color(0xFF06221B)
private val LSDeep2 = Color(0xFF0B342B)
private val LSGreen = Color(0xFF18C986)
private val LSMint = Color(0xFFC9F8E1)
private val LSBackground = Color(0xFFF3F8F5)
private val LSMuted = Color(0xFF70847B)
private val LSGold = Color(0xFFFFC857)

/** Small, readable study lessons designed to explain a topic before asking questions. */
data class StudyLesson(
    val title: String,
    val intro: String,
    val keyPoints: List<String>,
    val examTip: String
)

object LibraryStudyData {
    private val lessons = listOf(
        StudyLesson(
            "Coğrafi Konum",
            "Türkiye'nin konumunu iki ana pencereden düşün: matematik (mutlak) konum ve özel konum.",
            listOf("36°–42° Kuzey paralelleri ile 26°–45° Doğu meridyenleri arasında yer alır.", "Orta kuşakta olduğu için dört mevsim belirgin yaşanır.", "Asya ile Avrupa arasındaki geçiş konumu ulaşım ve jeopolitiği güçlendirir.", "Boğazlar; Karadeniz'i Akdeniz sistemine bağlayan stratejik geçişlerdir."),
            "Koordinat sorularında önce paralel = enlem, meridyen = boylam ayrımını kur."
        ),
        StudyLesson(
            "Yer Şekilleri",
            "Dağ, ova, plato ve vadileri tek tek ezberlemek yerine oluşum ve uzanışlarıyla bağdaştır.",
            listOf("Türkiye genç oluşumlu ve engebeli bir araziye sahiptir.", "Kuzey Anadolu Dağları ve Toroslar genel olarak doğu-batı yönünde uzanır.", "İç kesimlerde plato ve ovalar geniş yer kaplar.", "Karstik şekiller özellikle kalkerli kayaçların bulunduğu alanlarda gelişir."),
            "Dağların uzanışı kıyı-İç kesim ulaşımını, iklimi ve kıyıların özelliklerini etkileyebilir."
        ),
        StudyLesson(
            "Su Varlığı",
            "Akarsular, göller ve denizleri; kaynak, rejim, havza ve yer şekilleri üzerinden anlamlandır.",
            listOf("Türkiye'de akarsuların rejimi çoğunlukla düzensizdir.", "Karadeniz ve Akdeniz'e ulaşan akarsuların yanı sıra kapalı havzalar da vardır.", "Göllerin oluşumunda tektonik, karstik, volkanik ve set gölü süreçleri görülebilir.", "Kıyıların şekli ulaşımı, liman gelişimini ve yerleşmeyi etkiler."),
            "Akarsu sorularında 'rejim–debî–havza–enerji' kelimelerini ayrı ayrı değerlendir."
        ),
        StudyLesson(
            "İklim ve Bitki",
            "İklimi öğrenmenin en kolay yolu sıcaklık + yağış + yükselti + denize uzaklık ilişkisini kurmaktır.",
            listOf("Karadeniz kıyılarında yıl boyunca yağış daha düzenlidir.", "Akdeniz kıyılarında yazlar sıcak-kurak, kışlar ılık-yağışlıdır.", "İç ve Doğu Anadolu'da karasallık ve yükselti sıcaklık farklarını artırır.", "Bitki örtüsü iklimin doğal sonucudur; maki, orman ve bozkır bu bağlantıyla öğrenilir."),
            "Bir iklim özelliği verildiğinde önce yağış rejimini, sonra bitki örtüsünü tahmin et."
        ),
        StudyLesson(
            "Nüfus ve Yerleşme",
            "Nüfus dağılışını; yükselti, iklim, tarım, sanayi, ulaşım ve su kaynaklarının ortak sonucu olarak düşün.",
            listOf("Sanayi ve hizmetler gelişmiş merkezler nüfusu çeker.", "Yüksek ve engebeli alanlarda yerleşme seyrektir.", "Kırdan kente göç; eğitim, sağlık, istihdam ve yaşam koşullarıyla ilişkilidir.", "Nüfus yoğunluğu toplam nüfus değil, nüfusun alana dağılışını ifade eder."),
            "Yoğunluk sorularında 'nüfus / alan' ilişkisini unutma; büyük nüfus her zaman yoğunluk demek değildir."
        ),
        StudyLesson(
            "Tarım ve Hayvancılık",
            "Ürünleri tek başına ezberlemek yerine iklim, su, toprak ve pazar şartlarıyla eşleştir.",
            listOf("Tahıllar özellikle İç Anadolu gibi karasal alanlarda önemlidir.", "Çay ve fındık nemli Karadeniz koşullarıyla ilişkilidir.", "Zeytin ve turunçgiller ılıman kış isteyen Akdeniz-Ege çevresinde öne çıkar.", "Hayvancılığın türü; mera, bitki örtüsü ve ekonomik yapıyla bağlantılıdır."),
            "Ürün sorusunda ilk bakacağın ipucu çoğu zaman sıcaklık ve yağıştır."
        ),
        StudyLesson(
            "Maden ve Enerji",
            "Maden yataklarını; jeolojik yapı, kaynak türü ve enerji üretim yöntemiyle eşleştir.",
            listOf("Taş kömürü Zonguldak çevresinin klasik eşleştirmesidir.", "Linyit birçok bölgede termik santrallerin temel girdilerindendir.", "Hidroelektrik potansiyel; yükselti farkı, eğim ve akarsu rejimiyle ilgilidir.", "Güneş ve rüzgâr potansiyeli iklim ve yer şekilleriyle ilişkilidir."),
            "'Maden nerede çıkarılır?' ile 'enerji nerede üretilir?' sorularını birbirine karıştırma."
        ),
        StudyLesson(
            "Sanayi ve Ulaşım",
            "Sanayi merkezlerini; pazar, sermaye, ulaşım, iş gücü ve hammaddeye erişim üzerinden yorumla.",
            listOf("Marmara sanayide yoğunlaşan en önemli bölgedir.", "Limanlar dış ticareti ve kıyıdaki sanayi faaliyetlerini destekler.", "Ulaşım güzergâhları yer şekillerinden güçlü biçimde etkilenir.", "Sanayinin gelişmesi nüfus ve kentleşmeyi de hızlandırabilir."),
            "Sanayi sorularında sadece hammaddeyi değil; pazar + ulaşım + iş gücünü birlikte düşün."
        ),
        StudyLesson(
            "Turizm",
            "Turizmi kıyı, dağ, tarih ve kültür varlıklarının oluşturduğu farklı türler halinde sınıflandır.",
            listOf("Akdeniz ve Ege kıyılarında deniz turizmi öne çıkar.", "Uludağ ve Palandöken gibi yükselti alanları kış turizmi için uygundur.", "Kapadokya gibi doğal-kültürel alanlar alternatif turizmi destekler.", "Turizmin gelişiminde ulaşılabilirlik ve mevsim uzunluğu önemlidir."),
            "Soruda verilen turizm türünü önce doğal unsurla, sonra şehir/alanla eşleştir."
        ),
        StudyLesson(
            "Bölgeler",
            "7 bölgeyi ezberlemek yerine her bölge için bir 'imza' oluştur: iklim, yer şekli, tarım ve ekonomi.",
            listOf("Marmara: nüfus, sanayi, ticaret ve geçiş özellikleri.", "Ege: girintili-çıkıntılı kıyı, tarım ve limanlar.", "Akdeniz: turizm, seracılık, narenciye ve Toroslar.", "Karadeniz: dağlık kıyı, orman ve yıl içine yayılan yağış.", "İç Anadolu: karasal iklim, bozkır ve tahıl.", "Doğu Anadolu: yükselti, sert karasallık ve hayvancılık.", "Güneydoğu Anadolu: sıcak-karasal koşullar ve GAP ile değişen tarımsal yapı."),
            "Bölge sorularında tek özelliğe değil, üçlü kombinasyona bak: iklim + yer şekli + ekonomik faaliyet."
        ),
        StudyLesson(
            "Doğal Afetler",
            "Afetleri oluşum koşullarıyla birlikte öğrenirsen seçenekleri çok daha hızlı elersin.",
            listOf("Deprem, aktif fay hatları ve tektonik yapı ile ilişkilidir.", "Heyelan; eğim, yağış ve zemin koşullarının birlikte etkisiyle artar.", "Erozyon; bitki örtüsü zayıf, eğimli ve yanlış kullanılan alanlarda şiddetlenebilir.", "Çığ özellikle yüksek ve eğimli kar örtülü sahalarda görülür."),
            "Afet sorularında 'neden burada?' kısmını çözmeden şehir adı ezberleme."
        ),
        StudyLesson(
            "Harita Bilgisi",
            "Harita sorularında işlemden önce gösterim dilini çöz: ölçek, yön, yükselti ve semboller.",
            listOf("Büyük ölçek daha ayrıntılı gösterir ve küçültme oranı daha azdır.", "İzohipsler eş yükselti eğrileridir; sıklaşma eğimin arttığını gösterir.", "Profil, arazinin kesit görünüşünü anlamayı sağlar.", "Yön bulma, koordinat ve ölçek birlikte kullanılabilir."),
            "Ölçek büyüdükçe ayrıntı artar; payda küçülür. Bu ilişkiyi temel kural olarak aklında tut."
        )
    )

    private val byTitle = lessons.associateBy { it.title }

    fun forTopic(topic: Topic): StudyLesson = byTitle[topic.title] ?: StudyLesson(
        topic.title,
        topic.subtitle,
        topic.lessons.map { "$it konusunu temel kavramlarıyla öğren ve sorularla pekiştir." },
        "Önce kavramı anla, sonra aynı başlıktan en az 5 soru çöz."
    )
}

@Composable
fun LibraryStudyScreen(
    topic: Topic,
    onBack: () -> Unit,
    onQuiz: () -> Unit
) {
    val lesson = remember(topic.title) { LibraryStudyData.forTopic(topic) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LSBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, top = 6.dp, end = 16.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Geri", tint = LSDeep)
                    }
                    Column(Modifier.weight(1f)) {
                        Text("ÇALIŞMA", color = LSGreen, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.3.sp)
                        Text(topic.title, color = LSDeep, fontSize = 22.sp, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    Box(Modifier.clip(CircleShape).background(Color.White).padding(horizontal = 9.dp, vertical = 7.dp)) {
                        Text(topic.icon, fontSize = 18.sp)
                    }
                }
            }
            item {
                StudyCard(LSDeep) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(46.dp).clip(CircleShape).background(LSGreen.copy(alpha = .13f)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.AutoStories, null, tint = LSGreen)
                        }
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text("ÖNCE ANLA", color = LSMint, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                            Text(lesson.intro, color = Color.White, fontSize = 15.sp, lineHeight = 21.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
            item { Text("Temel Bilgiler", color = LSDeep, fontSize = 18.sp, fontWeight = FontWeight.Black) }
            items(lesson.keyPoints) { point ->
                StudyPoint(point)
            }
            item {
                StudyCard(LSGold) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.Lightbulb, null, tint = LSGold)
                        Spacer(Modifier.width(9.dp))
                        Column(Modifier.weight(1f)) {
                            Text("KPSS İPUCU", color = LSGold, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                            Text(lesson.examTip, color = LSDeep, fontSize = 13.sp, lineHeight = 19.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 4.dp))
                        }
                    }
                }
            }
            item {
                StudyCard(LSGreen) {
                    Text("Çalışma sırası", color = LSDeep, fontSize = 15.sp, fontWeight = FontWeight.Black)
                    Spacer(Modifier.height(8.dp))
                    Text("1. Temel bilgiyi oku  •  2. Kendin anlat  •  3. Soruyla pekiştir", color = LSMuted, fontSize = 12.sp, lineHeight = 18.sp)
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = onQuiz,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(15.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LSGreen)
                    ) {
                        Icon(Icons.Default.PlayArrow, null)
                        Spacer(Modifier.width(6.dp))
                        Text("Konuyu Test Et", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun StudyPoint(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(13.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(Modifier.size(28.dp).clip(CircleShape).background(LSGreen.copy(alpha = .11f)), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.CheckCircle, null, tint = LSGreen, modifier = Modifier.padding(6.dp))
        }
        Spacer(Modifier.width(10.dp))
        Text(text, color = LSDeep, fontSize = 13.sp, lineHeight = 19.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun StudyCard(accent: Color, content: @Composable ColumnScope.() -> Unit) {
    val dark = accent == LSDeep
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(if (dark) LSDeep2 else Color.White)
            .padding(16.dp),
        content = content
    )
}
