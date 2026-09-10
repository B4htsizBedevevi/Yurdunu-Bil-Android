package tr.yurdunubil.app

data class Topic(
    val title: String,
    val subtitle: String,
    val icon: String,
    val progress: Int,
    val lessons: List<String>
)

data class Province(
    val name: String,
    val region: String,
    val clue: String,
    val facts: List<String>
)

data class GameMode(val title: String, val subtitle: String, val icon: String, val colorKey: Int)

data class ArenaMode(val title: String, val subtitle: String, val reward: String, val icon: String)

object GeographyData {
    val topics = listOf(
        Topic("Coğrafi Konum", "Mutlak, özel ve jeopolitik konum", "map", 18, listOf("Matematik konum", "Özel konum", "Jeopolitik konum", "Sınırlar ve komşular", "Boğazlar")),
        Topic("Yer Şekilleri", "Dağlar, ovalar, platolar ve vadiler", "mountain", 12, listOf("Dağlar", "Platolar", "Ovalar", "Karstik şekiller", "Kıyı şekilleri")),
        Topic("Su Varlığı", "Akarsular, göller ve denizler", "water", 8, listOf("Akarsular", "Göller", "Denizler", "Kıyı tipleri", "Havzalar")),
        Topic("İklim ve Bitki", "Türkiye'nin iklim bölgeleri", "climate", 24, listOf("İklim elemanları", "Akdeniz iklimi", "Karadeniz iklimi", "Karasal iklim", "Bitki örtüsü")),
        Topic("Nüfus ve Yerleşme", "Nüfus dağılışı, göç ve yerleşme", "population", 5, listOf("Nüfus özellikleri", "Nüfus dağılışı", "Göçler", "Kır ve kent yerleşmeleri")),
        Topic("Tarım ve Hayvancılık", "Ürünlerin yetişme koşulları", "agriculture", 0, listOf("Tahıllar", "Endüstri bitkileri", "Meyvecilik", "Hayvancılık", "Bölgesel üretim")),
        Topic("Maden ve Enerji", "Madenler, santraller ve enerji", "mine", 0, listOf("Madenler", "Kömür", "Petrol", "Hidroelektrik", "Yenilenebilir enerji")),
        Topic("Sanayi ve Ulaşım", "Üretim, ulaşım ve ticaret", "factory", 0, listOf("Sanayi bölgeleri", "Ulaşım yolları", "Limanlar", "Ticaret", "Bölgesel kalkınma")),
        Topic("Turizm", "Türkiye'nin doğal ve kültürel turizmi", "tourism", 0, listOf("Kıyı turizmi", "Kış turizmi", "Kültür turizmi", "Doğal güzellikler")),
        Topic("Bölgeler", "7 bölgeyi karşılaştırarak öğren", "region", 0, listOf("Marmara", "Ege", "Akdeniz", "İç Anadolu", "Karadeniz", "Doğu Anadolu", "Güneydoğu Anadolu")),
        Topic("Doğal Afetler", "Deprem, heyelan, sel ve erozyon", "danger", 0, listOf("Deprem", "Heyelan", "Sel", "Erozyon", "Çığ")),
        Topic("Harita Bilgisi", "Haritayı okuyabil, bilgiyi bul", "map", 0, listOf("Ölçek", "Yön", "İzohips", "Profil", "Harita yorumlama"))
    )

    val provinces = listOf(
        Province("Adana", "Akdeniz", "Çukurova'nın güçlü tarım ve sanayi merkezi", listOf("Seyhan ve Ceyhan havzaları", "Pamuk ve turunçgil üretimi", "Akdeniz kıyısında")),
        Province("Ankara", "İç Anadolu", "Türkiye'nin başkenti ve İç Anadolu'nun merkezlerinden", listOf("Yönetim merkezi", "İç Anadolu'da", "Karasal iklim etkisi")),
        Province("Antalya", "Akdeniz", "Kıyı turizminin en önemli merkezlerinden", listOf("Toroslar ile kıyı arasında", "Turizm güçlü", "Seracılık yaygın")),
        Province("Bursa", "Marmara", "Uludağ ve sanayi-tarım birlikteliği", listOf("Uludağ", "Otomotiv sanayisi", "Marmara'nın güneyinde")),
        Province("Erzurum", "Doğu Anadolu", "Yüksek plato ve kış koşullarıyla öne çıkar", listOf("Yüksek yükselti", "Karasal iklim", "Kış turizmi")),
        Province("Gaziantep", "Güneydoğu Anadolu", "Sanayi, ticaret ve tarımın güçlü merkezlerinden", listOf("Fıstık üretimi", "Sanayi", "GAP etkisi")),
        Province("İstanbul", "Marmara", "Boğazlar, nüfus ve ticaretin kesişim noktası", listOf("Asya ve Avrupa'da toprakları var", "Boğazlar", "Türkiye'nin en büyük nüfus merkezi")),
        Province("İzmir", "Ege", "Ege'nin önemli liman ve ticaret merkezi", listOf("İzmir Körfezi", "Liman ve ticaret", "Zeytin ve üzüm üretimi")),
        Province("Konya", "İç Anadolu", "Geniş tarım alanları ve kapalı havza özellikleri", listOf("Tahıl üretimi", "Konya Ovası", "Karasal iklim")),
        Province("Mersin", "Akdeniz", "Liman, tarım ve seracılığın buluştuğu şehir", listOf("Mersin Limanı", "Narenciye", "Akdeniz kıyısı")),
        Province("Samsun", "Karadeniz", "Orta Karadeniz'in önemli liman ve tarım merkezi", listOf("Liman", "Bafra Ovası çevresi", "Karadeniz kıyısı")),
        Province("Trabzon", "Karadeniz", "Doğu Karadeniz'in kıyı ve ticaret merkezlerinden", listOf("Dağlık kıyı", "Çay ve fındık çevresi", "Liman")),
        Province("Şanlıurfa", "Güneydoğu Anadolu", "GAP ve sulama yatırımlarıyla tarımda öne çıkar", listOf("Harran Ovası", "GAP", "Pamuk ve tarla tarımı")),
        Province("Van", "Doğu Anadolu", "Van Gölü çevresinin en önemli merkezlerinden", listOf("Van Gölü", "Yüksek plato", "Karasal iklim")),
        Province("Zonguldak", "Karadeniz", "Taş kömürü ve madencilikle özdeşleşmiş şehir", listOf("Taş kömürü", "Karadeniz kıyısı", "Madencilik"))
    )

    val games = listOf(
        GameMode("Haritada Bul", "Verilen ili Türkiye haritasında yakala", "region", 0),
        GameMode("Hızlı 10", "10 soru • süreli coğrafya turu", "speed", 1),
        GameMode("Doğru mu Yanlış mı?", "Bilgiyi saniyeler içinde değerlendir", "quick", 2),
        GameMode("Bölge Avı", "İpuçlarından bölgeyi keşfet", "map", 3),
        GameMode("Eşleştir", "İl • ürün • maden • özellik eşleştir", "match", 4),
        GameMode("Zincir", "Arka arkaya doğru cevaplarla çarpanı büyüt", "🔥", 5)
    )

    val arena = listOf(
        ArenaMode("1v1 Bilgi Düellosu", "Rakibinden önce doğru cevabı bul", "+100 XP", "duel"),
        ArenaMode("Bölge Savaşı", "7 bölgeden birini seç, puanını savun", "+150 XP", "region"),
        ArenaMode("Hız Arenası", "60 saniyede en fazla doğru", "+200 XP", "speed"),
        ArenaMode("Türkiye Ustası", "Karışık harita ve bilgi soruları", "+250 XP", "master")
    )
}
