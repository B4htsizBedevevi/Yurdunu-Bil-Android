package tr.yurdunubil.app

data class LibraryFact(val title: String, val body: String)
data class LibraryTopic(val title: String, val icon: String, val summary: String, val cards: List<LibraryFact>)

object LibraryContent {
    val topics = listOf(
        LibraryTopic("Coğrafi Konum", "📍", "Mutlak konum, özel konum ve jeopolitik sonuçları birlikte öğren.", listOf(
            LibraryFact("Matematik konum", "Türkiye 36°-42° kuzey paralelleri ile 26°-45° doğu meridyenleri arasında yer alır."),
            LibraryFact("Enlem etkisi", "Kuzey-güney doğrultusunda sıcaklık ve güneş ışınlarının geliş açısı değişir."),
            LibraryFact("Boylam etkisi", "Doğu-batı yönünde yerel saat farkı oluşur."),
            LibraryFact("Özel konum", "Kıtalar, denizler, boğazlar ve ulaşım koridorları Türkiye'nin özel konumunu güçlendirir."),
            LibraryFact("KPSS dikkat", "Yerel saat farkını enlemle değil boylamla; sıcaklık farklarını ise yalnızca boylamla açıklama.")
        )),
        LibraryTopic("Yer Şekilleri", "⛰️", "Dağ, ova, plato, vadi ve kıyı şekillerini neden-sonuç ilişkisiyle öğren.", listOf(
            LibraryFact("Yükselti", "Türkiye'nin ortalama yükseltisi batıdan doğuya genel olarak artar."),
            LibraryFact("Kuzey ve güney dağları", "Kuzey Anadolu Dağları ve Toroslar birçok kesimde kıyıya paralel uzanır."),
            LibraryFact("Ege kıyıları", "Ege'de dağların kıyıya dik uzanması kıyı-iç kesim ulaşımını kolaylaştırır."),
            LibraryFact("Platolar", "İç kesimlerde geniş plato alanları yaygındır ve hayvancılık ile tarım faaliyetlerini etkiler."),
            LibraryFact("Karstik şekiller", "Kalkerli arazilerde mağara, obruk, dolin ve traverten gibi karstik şekiller gelişebilir.")
        )),
        LibraryTopic("Su Varlığı", "🌊", "Akarsular, göller, havzalar ve kıyıların sınavda nasıl yorumlandığını öğren.", listOf(
            LibraryFact("Akarsu rejimi", "Yağışın mevsimlere göre değişmesi Türkiye akarsularının çoğunda rejimin düzensiz olmasına yol açar."),
            LibraryFact("Kızılırmak", "Türkiye sınırları içinde doğup yine Türkiye'den denize ulaşan en uzun akarsudur."),
            LibraryFact("Fırat ve Dicle", "Güneydoğu Anadolu'da sulama ve hidroelektrik açısından önemlidir."),
            LibraryFact("Göller", "Tektonik, karstik, volkanik ve buzul gibi farklı oluşum tipleri Türkiye'de görülür."),
            LibraryFact("Kapalı havza", "Bazı havzalarda sular denize ulaşmadan göl veya çanaklarda sonlanabilir.")
        )),
        LibraryTopic("İklim ve Bitki Örtüsü", "🌦️", "İklim tiplerini sıcaklık-yağış-bitki üçlüsüyle bağla.", listOf(
            LibraryFact("Akdeniz iklimi", "Yazlar sıcak ve kurak, kışlar ılık ve yağışlıdır; maki yaygındır."),
            LibraryFact("Karadeniz iklimi", "Yağış yıl içine daha düzenli yayılır; kıyıda doğal bitki örtüsü gürdür."),
            LibraryFact("Karasal iklim", "Yıllık sıcaklık farkları daha belirgindir; iç kesimlerde bozkır yaygındır."),
            LibraryFact("Yükselti", "Yükselti arttıkça sıcaklık genel olarak azalır ve bitki kuşakları değişebilir."),
            LibraryFact("KPSS bağlantısı", "Bir iklim sorusunda yalnız sıcaklığa değil yağışın mevsim içindeki dağılışına da bak.")
        )),
        LibraryTopic("Nüfus ve Yerleşme", "👥", "Nüfusun neden bazı alanlarda yoğun, bazılarında seyrek olduğunu çöz.", listOf(
            LibraryFact("Yoğun nüfus", "Marmara; sanayi, ticaret, ulaşım ve hizmetlerin yoğunluğu nedeniyle öne çıkar."),
            LibraryFact("Seyrek nüfus", "Yüksek, engebeli ve ulaşımı zor alanlarda nüfus yoğunluğu genellikle daha düşüktür."),
            LibraryFact("Göç", "İş, eğitim, sağlık ve hizmet olanakları iç göçte önemli çekim faktörleridir."),
            LibraryFact("Kentleşme", "Kentlere göç konut, ulaşım ve altyapı talebini artırabilir."),
            LibraryFact("Güncel veri", "2025 ADNKS'de nüfus yoğunluğu en düşük il Tunceli, 11 kişi/km² olarak açıklandı.")
        )),
        LibraryTopic("Tarım ve Hayvancılık", "🌾", "Ürünü ezberlemek yerine iklim, toprak, sulama ve pazar bağlantısını kur.", listOf(
            LibraryFact("Tahıllar", "Buğday ve arpa farklı iç kesimlerde geniş alanlarda yetiştirilebilir."),
            LibraryFact("Pamuk", "Sıcaklık ve sulama koşullarının uygun olduğu Güneydoğu ve Akdeniz alanlarında önemlidir."),
            LibraryFact("Çay", "Nemli ve yağışlı koşullar nedeniyle Doğu Karadeniz'de yoğunlaşır."),
            LibraryFact("Zeytin", "Kışların ılıman geçtiği Ege ve Marmara'nın güney kesimleri öne çıkar."),
            LibraryFact("Arıcılık", "Bitki çeşitliliği ve çiçeklenme dönemleri arıcılık için önemlidir; Ordu bal üretiminde son yıllarda öne çıkan illerdendir.")
        )),
        LibraryTopic("Maden ve Enerji", "⛏️", "Maden-merkez eşleştirmelerini harita mantığıyla öğren.", listOf(
            LibraryFact("Bor", "Eskişehir-Kırka, Balıkesir-Bigadiç, Bursa-Kestelek ve Kütahya-Emet başlıca bor alanları arasındadır."),
            LibraryFact("Taş kömürü", "Zonguldak çevresi taş kömürü ile özdeşleşmiştir."),
            LibraryFact("Petrol", "Batman ve çevresi Türkiye'nin önemli petrol üretim alanlarındandır."),
            LibraryFact("Demir", "Sivas-Divriği önemli demir yataklarıyla bilinir."),
            LibraryFact("Güncel veri", "Enerji Bakanlığına göre Türkiye 2024'te dünya bor üretiminde lider konumunu korudu ve küresel üretimin %48'ini karşıladı.")
        )),
        LibraryTopic("Sanayi ve Ulaşım", "🏭", "Sanayinin ve ulaşımın yer seçimini pazar, ham madde, enerji ve ulaşım üzerinden yorumla.", listOf(
            LibraryFact("Marmara sanayisi", "Geniş pazar, ulaşım, sermaye ve iş gücü sanayinin yoğunlaşmasını destekler."),
            LibraryFact("Liman ve hinterland", "Bir limanın gerisindeki ekonomik alanın genişliği ticari önemini artırabilir."),
            LibraryFact("Boğazlar", "İstanbul ve Çanakkale Boğazları deniz ulaşımı ve stratejik geçiş açısından önemlidir."),
            LibraryFact("Demiryolları", "Engebeli ve yüksek alanlarda demiryolu yapımının maliyeti ve teknik zorluğu artabilir."),
            LibraryFact("Sanayi-ulaşım", "Sanayi merkezleri genellikle ham madde, enerji, pazar ve ulaşım ağlarıyla birlikte değerlendirilmelidir.")
        )),
        LibraryTopic("Turizm", "🏖️", "Doğal ve kültürel turizm merkezlerini oluşum nedenleriyle eşleştir.", listOf(
            LibraryFact("Kıyı turizmi", "Akdeniz ve Ege kıyılarında ılıman iklim ve uzun sezon etkilidir."),
            LibraryFact("Kapadokya", "Volkanik tüflerin aşınmasıyla oluşan peri bacaları önemli turizm değeridir."),
            LibraryFact("Pamukkale", "Travertenler ve termal kaynaklar Denizli'deki önemli doğal turizm unsurlarıdır."),
            LibraryFact("Efes", "İzmir'deki Efes Antik Kenti kültür ve tarih turizminin önemli merkezlerindendir."),
            LibraryFact("Kış turizmi", "Uludağ, Palandöken ve Erciyes gibi yüksek alanlar kış turizmiyle öne çıkar.")
        )),
        LibraryTopic("Bölgeler", "🗺️", "7 bölgeyi sadece isimle değil, ayırt edici özellikleriyle karşılaştır.", listOf(
            LibraryFact("Marmara", "Nüfus, sanayi, ticaret ve ulaşım yoğunluğu ile öne çıkar."),
            LibraryFact("Ege", "Dağların kıyıya dik uzanması ve kıyı-iç kesim bağlantılarıyla dikkat çeker."),
            LibraryFact("Akdeniz", "Kıyı turizmi, seracılık ve turunçgil üretimi önemlidir."),
            LibraryFact("Karadeniz", "Yağışlı iklim, ormanlar, çay ve fındık tarımı öne çıkar."),
            LibraryFact("Doğu Anadolu", "Yükselti fazladır; yüzölçümü bakımından Türkiye'nin en büyük coğrafi bölgesidir."),
            LibraryFact("Güneydoğu Anadolu", "GAP ve sulama yatırımları tarımsal üretimi önemli ölçüde etkiler."),
            LibraryFact("İç Anadolu", "Karasal iklim, bozkır ve tahıl tarımıyla öne çıkar.")
        )),
        LibraryTopic("Doğal Afetler", "⚠️", "Afetleri tek tek ezberlemek yerine oluşum koşullarını bağla.", listOf(
            LibraryFact("Deprem", "Aktif fay sistemleri Türkiye'nin deprem riskini artırır."),
            LibraryFact("Heyelan", "Eğim ve fazla yağış birlikte olduğunda risk artabilir."),
            LibraryFact("Erozyon", "Bitki örtüsünün zayıflaması ve yanlış arazi kullanımı toprağın taşınmasını hızlandırabilir."),
            LibraryFact("Çığ", "Yüksek ve eğimli alanlarda yoğun kar birikimi çığ riskini artırabilir."),
            LibraryFact("Sel", "Kısa sürede yoğun yağış, geçirimsiz yüzeyler ve yetersiz drenaj taşkın riskini artırabilir.")
        )),
        LibraryTopic("Harita Bilgisi", "🧭", "Ölçek, izohips ve harita yorumlama sorularında temel kuralları otomatikleştir.", listOf(
            LibraryFact("Büyük ölçek", "Paydası küçük olan harita daha ayrıntılıdır ve daha küçük alan gösterir."),
            LibraryFact("Küçük ölçek", "Paydası büyük olan harita daha geniş alanı daha az ayrıntıyla gösterir."),
            LibraryFact("İzohips", "Aynı yükseltideki noktaları birleştiren eğrilerdir."),
            LibraryFact("Eğim", "İzohipsler sıklaştıkça eğim artar; seyrekleştikçe eğim azalır."),
            LibraryFact("Profil", "İzohipslerden arazinin kesitini çıkarmaya yarar.")
        ))
    )
}
