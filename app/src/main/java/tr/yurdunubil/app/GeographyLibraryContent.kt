package tr.yurdunubil.app

data class LearningNote(
    val title: String,
    val summary: String,
    val keyPoints: List<String>,
    val examTip: String
)

/** Compact, exam-oriented notes used to enrich the geography library. */
object GeographyLibraryContent {
    val notes = listOf(
        LearningNote("Coğrafi Konum", "Türkiye'nin matematik ve özel konumunun sonuçlarını ilişkilendir.", listOf("36–42° Kuzey enlemleri", "26–45° Doğu boylamları", "Asya-Avrupa geçiş alanı", "Boğazlar ve ulaşım koridorları"), "Konum sorularında verilen özelliğin matematik mi özel konum mu olduğunu önce ayır."),
        LearningNote("Yer Şekilleri", "Dağ, ova, plato ve kıyı şekillerini bölgesel özelliklerle birlikte öğren.", listOf("Kuzey Anadolu Dağları", "Toroslar", "Platoların yaygınlığı", "Kıyıların iç kesimlerle ilişkisi"), "Dağların uzanış yönü; iklim, ulaşım ve kıyı-iç kesim farklarını aynı anda etkileyebilir."),
        LearningNote("İklim ve Bitki Örtüsü", "Türkiye'deki üç temel iklim tipini yağış ve sıcaklık özellikleriyle karşılaştır.", listOf("Akdeniz iklimi ve maki", "Karadeniz iklimi ve gür ormanlar", "Karasal iklim ve bozkır", "Yükselti-sıcaklık ilişkisi"), "Bitki örtüsünü tek başına ezberlemek yerine yağış ve sıcaklık şartından çıkar."),
        LearningNote("Su Varlığı", "Akarsu, göl ve havzaları kaynakları ve döküldükleri denizlerle eşleştir.", listOf("Kızılırmak", "Fırat-Dicle", "Van Gölü", "Kapalı havza örnekleri"), "Akarsularda kaynak-dökülüş yönünü harita üzerinde düşün; sadece isim ezberleme."),
        LearningNote("Nüfus ve Yerleşme", "Nüfus yoğunluğunu doğal ve beşerî faktörlerin birlikte sonucu olarak değerlendir.", listOf("Marmara yoğunluğu", "Kıyıların çekiciliği", "Sanayi ve hizmetler", "İç göç"), "Nüfus sorularında 'neden yoğun?' sorusuna ekonomi + ulaşım + doğal koşullar üçlüsüyle yaklaş."),
        LearningNote("Tarım", "Ürünleri iklim, sulama ve bölge şartlarıyla ilişkilendir.", listOf("Çay–Doğu Karadeniz", "Pamuk–Güneydoğu", "Fındık–Karadeniz", "Buğday–İç Anadolu"), "Ürün sorularında önce iklim isteğini belirle, sonra bölgeyi seç."),
        LearningNote("Maden ve Enerji", "Madenleri çıkarıldığı merkezlerle ve enerji türleriyle eşleştir.", listOf("Bor–Kırka", "Taş kömürü–Zonguldak", "Petrol–Batman", "Hidroelektrik–akarsu/eğim"), "Maden sorularında şehir + maden ikilisini kart gibi çalış."),
        LearningNote("Turizm", "Doğal, tarihî ve kış turizmi örneklerini haritada konumlandır.", listOf("Pamukkale–Denizli", "Kapadokya–Nevşehir çevresi", "Efes–İzmir", "Uludağ–Bursa"), "Turizm sorusunda yer şekli veya tarihî eser ipucunu şehirle eşleştir."),
        LearningNote("Harita Bilgisi", "Ölçek ve izohips sorularında görsel mantığı kullan.", listOf("Büyük ölçek = fazla ayrıntı", "Küçük ölçek = geniş alan", "Sık izohips = fazla eğim", "Kuzey oku = yön"), "İzohips aralığı ve sıkışıklığını birlikte okuyarak yükselti değişimini yorumla."),
        LearningNote("Doğal Afetler", "Afetleri oluşum koşullarıyla eşleştir.", listOf("Deprem–aktif fay", "Heyelan–eğim + yağış", "Sel–şiddetli yağış + drenaj", "Çığ–eğim + kar örtüsü"), "Afet sorularında olayın kendisinden çok oluşum şartlarını sorgula."),
        LearningNote("Sanayi ve Ulaşım", "Sanayi dağılışını pazar, ulaşım, sermaye ve hammadde ile ilişkilendir.", listOf("Marmara sanayi yoğunluğu", "Limanların ticarete etkisi", "Boğazların stratejik konumu", "Ulaşım koridorları"), "Sanayi sorularında tek faktöre takılma; pazar + ulaşım + iş gücü birlikte düşünülür."),
        LearningNote("Bölgeler", "Yedi bölgeyi tek tek ezberlemek yerine ayırt edici özellikleriyle karşılaştır.", listOf("Doğu Anadolu–yükselti", "Marmara–sanayi", "Karadeniz–yağış", "Güneydoğu–GAP ve tarım"), "Bölge sorularında en ayırt edici özelliği bul; bütün özelliklerin aynı anda verilmesini bekleme.")
    )
}
