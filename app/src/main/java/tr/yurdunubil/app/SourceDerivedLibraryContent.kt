package tr.yurdunubil.app

/**
 * Exam-oriented study cards distilled from the user-provided geography sources.
 * Wording is original and reorganized for the app.
 */
object SourceDerivedLibraryContent {
    data class StudyCard(val title: String, val summary: String, val keyPoints: List<String>)

    val cards = listOf(
        StudyCard("Konumdan soru üret", "Matematik konum ile özel konumu ayır; sonra sonucu yorumla.", listOf(
            "36°-42° Kuzey ve 26°-45° Doğu koordinatları matematik konumdur.",
            "19 meridyen farkı doğu-batı arasında 76 dakikalık yerel saat farkı oluşturur.",
            "Asya-Avrupa geçişi, boğazlar ve enerji koridorları özel konum başlığına girer."
        )),
        StudyCard("Bakı + yükselti + denizellik", "Aynı enlemdeki merkezleri karşılaştırırken tek başına enleme takılma.", listOf(
            "Güney yamaçlar Türkiye'de genel olarak daha fazla ısınır.",
            "Yükselti arttıkça sıcaklık düşer; kıyı-iç kesim karşılaştırmalarında denizellik önemlidir.",
            "Karadeniz'de kuzey yamaçların ılımanlaşması denizden gelen nemli hava ile açıklanabilir."
        )),
        StudyCard("Yer şekli → oluşum süreci", "Şeklin adını ezberlemek yerine hangi dış kuvvetin ve hangi sürecin etkili olduğunu eşleştir.", listOf(
            "Akarsu: vadi, şelale, delta, menderes ve taraça gibi şekiller oluşturabilir.",
            "Rüzgâr: kurak ve bitki örtüsü zayıf alanlarda aşındırma ve biriktirme yapar.",
            "Karst: kalker, jips ve kaya tuzu gibi çözünebilen kayaçlarla ilişkilidir.",
            "Buzul: Türkiye'de yüksek dağlarda sınırlı alanlarda etkilidir."
        )),
        StudyCard("Karstik şekiller zinciri", "Karstik şekilleri oluşum sırasını düşünerek öğren.", listOf(
            "Lapya → küçük yüzey çözünme şekli.",
            "Dolinlerin birleşmesi → uvala; daha büyük karstik çukurluklar → polye.",
            "Yer altı boşluğunun çökmesi → obruk; yer altı suyu → mağara ve düden.",
            "Mağarada tavandan sarkan sarkıt, tabandan yükselen dikit; birleşmeleri sütun oluşturur.",
            "Kireçli suların yüzeyde birikmesi travertenleri oluşturabilir."
        )),
        StudyCard("İklim kartı", "Basınç merkezi + mevsim + hava özelliğini tek eşleştirmede düşün.", listOf(
            "Sibirya yüksek basıncı: kışın soğuk ve sert hava.",
            "İzlanda alçak basıncı: özellikle kışın ılık ve yağışlı hava koşullarını destekler.",
            "Asor yüksek basıncı: yazın Akdeniz çevresinde kuraklığı güçlendirir.",
            "Basra alçak basıncı: yazın özellikle Güneydoğu Anadolu'da sıcaklığı artırır."
        )),
        StudyCard("Su varlığı bağlantıları", "Akarsu sorularında kaynak, rejim, aşındırma-birikim ve kullanım alanını birlikte düşün.", listOf(
            "Yağışın mevsimsel değişimi akarsu rejiminin düzensizleşmesine neden olabilir.",
            "Delta ovalarında akarsuyun taşıdığı alüvyonlar kıyıda birikir.",
            "Barajlar sulama, enerji, içme suyu ve taşkın kontrolü amaçlarıyla kullanılabilir.",
            "Fırat ve Dicle Güneydoğu Anadolu'da sulama ve enerji açısından önemlidir."
        )),
        StudyCard("Nüfusun dağılışını çöz", "Nüfus sorularında doğal ve beşerî faktörleri aynı tabloda düşün.", listOf(
            "Doğal: iklim, yer şekilleri, su ve toprak.",
            "Beşerî: sanayi, tarım, turizm, ulaşım ve yer altı kaynakları.",
            "Yoğun nüfus çoğunlukla ekonomik çekim ve ulaşımın güçlü olduğu alanlarda görülür.",
            "Doğal nüfus artışında göç dikkate alınmaz; gerçek nüfus artışında göç de etkilidir."
        )),
        StudyCard("Tarım-hayvancılık ipuçları", "Ürünü doğrudan ezberlemek yerine iklim ve yetişme isteğiyle bağla.", listOf(
            "Çay: nemli ve yağışlı koşullarla ilişkilidir.",
            "Tiftik keçisi: Ankara çevresiyle ilişkilendirilir.",
            "İpekböcekçiliği: dut yaprağına dayalıdır.",
            "Tarım ürününün dağılışında sıcaklık, su ve yetişme dönemi belirleyicidir."
        )),
        StudyCard("Maden-enerji eşleştirme", "Merkez + kaynak + kullanım/taşıma mantığını birlikte kur.", listOf(
            "Kırka: bor.",
            "Zonguldak: taş kömürü.",
            "BTC ve Kerkük-Yumurtalık: petrol taşımacılığıyla ilişkilidir.",
            "TANAP: Azerbaycan doğal gazının Türkiye üzerinden Avrupa yönüne taşınmasında kullanılır."
        )),
        StudyCard("Ulaşım-ticaret", "Türkiye'nin konumunu ulaşım ağıyla birlikte yorumla.", listOf(
            "Asya-Avrupa arasında bulunmak transit ticaret açısından avantaj sağlar.",
            "Boğazlar denizler arasında doğal geçiş sağladığı için stratejiktir.",
            "İç ticarette üretim farklılıkları, nüfus dağılışı ve ulaşım olanakları önemlidir.",
            "İthalatın ihracattan fazla olması dış ticaret açığıdır."
        )),
        StudyCard("Turizm haritası", "Turizm türünü doğal veya kültürel çekicilikle eşleştir.", listOf(
            "Akdeniz-Ege kıyıları: yaz/kıyı turizmi.",
            "Uludağ, Erciyes, Palandöken gibi merkezler: kış turizmi.",
            "Mevlana, Sümela, Meryem Ana gibi alanlar: inanç turizmi.",
            "Termal turizm sıcak su kaynaklarıyla ilişkilidir."
        )),
        StudyCard("Harita ve izohips", "Haritada şekli gör, sonra yükselti ve eğim ilişkisini yorumla.", listOf(
            "İki meridyen arasındaki yerel saat farkı 4 dakikadır.",
            "İzohipslerde kapalı eğrilerin yükselti değerleri merkeze doğru artıyorsa tepe gösterilir.",
            "Küçük ölçek daha geniş alanı daha az ayrıntıyla gösterir.",
            "Türkiye'nin kuzey-güney koordinat farkı 6 paraleldir; yaklaşık 666 km kabul edilir."
        )),
        StudyCard("Afet düşünme modeli", "Afeti ezberlemek yerine tetikleyici koşulu bul.", listOf(
            "Eğim + suya doygunluk + zayıf tutunma heyelan riskini artırabilir.",
            "Eğimli yamaç + kar birikimi çığ riskini artırır.",
            "Kuraklık ve seyrek bitki örtüsü erozyon/rüzgâr aşındırması riskini artırabilir."
        ))
    )
}
