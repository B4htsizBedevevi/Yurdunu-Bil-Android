package tr.yurdunubil.app

data class DeepSection(val title: String, val body: String, val bullets: List<String> = emptyList())
data class DeepLesson(
    val title: String,
    val subtitle: String,
    val icon: String,
    val summary: String,
    val sections: List<DeepSection>,
    val examTrap: String,
    val recall: List<String>
)

object DeepLibrary {
    private val lessons = listOf(
        DeepLesson(
            "Coğrafi Konum", "Mutlak, özel ve jeopolitik konumu birlikte oku", "map",
            "Türkiye'nin harita üzerindeki yeri; iklimini, ulaşımını, tarımını, komşuluk ilişkilerini ve stratejik önemini aynı anda etkiler.",
            listOf(
                DeepSection("Matematik konum", "Türkiye 36°–42° Kuzey paralelleri ile 26°–45° Doğu meridyenleri arasındadır. Kuzey yarım kürede ve orta kuşakta bulunması mevsimlerin belirgin yaşanmasını sağlar.", listOf("Enlem; sıcaklık, güneş açısı ve gece-gündüz sürelerini etkiler.", "Boylam; yerel saat farklarının temelidir.", "Doğuya gidildikçe yerel saat ilerler.")),
                DeepSection("Özel konum", "Denizlerle çevrili olmak, üç tarafının denizlerle çevrili olması, Asya ile Avrupa arasında köprü oluşturması ve boğazlara sahip olması Türkiye'nin özel konum özellikleridir.", listOf("Kıyıların iç kesimlere göre daha ılıman olması", "Ulaşım ve ticaret yollarının kesişmesi", "Farklı iklim ve ekonomik faaliyetlerin görülmesi")),
                DeepSection("Jeopolitik önem", "İstanbul ve Çanakkale boğazları Karadeniz'i Akdeniz sistemine bağlayan stratejik geçişlerdir. Türkiye'nin çevresindeki enerji ve ticaret bölgeleriyle bağlantısı da önemini artırır.", listOf("Boğazlar", "Kıtalar arası geçiş", "Enerji ve ticaret güzergâhları")),
                DeepSection("Sınavda nasıl düşünülür?", "Soruda bir sonuç veriliyorsa bunun enlemden mi, boylamdan mı, yoksa özel konumdan mı kaynaklandığını ayır.", listOf("Sıcaklık → enlem", "Yerel saat → boylam", "Ulaşım/strateji → özel konum"))
            ),
            "Gece-gündüz süresi ve sıcaklık sorularında boylamı değil enlemi; yerel saat sorularında enlemi değil boylamı temel al.",
            listOf("Türkiye hangi yarım kürelerdedir?", "Yerel saat farkı hangi konumla ilgilidir?", "Boğazların önemi hangi konum türüyle açıklanır?")
        ),
        DeepLesson(
            "Yer Şekilleri", "Dağ, ova, plato ve vadileri oluşumlarıyla öğren", "mountain",
            "Türkiye genç oluşumlu ve engebeli bir ülkedir. Bu nedenle yükselti, eğim ve dağların uzanışı iklimden ulaşıma kadar birçok konuyu etkiler.",
            listOf(
                DeepSection("Dağların uzanışı", "Kuzey Anadolu Dağları ve Toroslar genel olarak doğu-batı doğrultusunda uzanır. Bu uzanış kıyı ile iç kesimler arasındaki ulaşımı ve iklim geçişlerini etkiler.", listOf("Karadeniz ve Akdeniz kıyılarında dağlar kıyıya yakın uzanır.", "Batı Anadolu'da dağlar çoğunlukla denize dik uzanır.", "Doğu Anadolu'da yükselti genel olarak fazladır.")),
                DeepSection("Ovalar", "Akarsuların taşıdığı alüvyonların birikmesiyle oluşan delta ovaları kıyılarda; tektonik ovalar ise fay hatları boyunca görülebilir.", listOf("Çukurova → Seyhan-Ceyhan", "Bafra → Kızılırmak", "Çarşamba → Yeşilırmak")),
                DeepSection("Platolar", "Akarsular tarafından yarılmış yüksek düzlüklerdir. İç Anadolu ve Güneydoğu Anadolu'da geniş alanlar kaplar.", listOf("Plato = yüksek düzlük", "Tarım ve hayvancılık birlikte görülebilir", "Kuraklık ve karasallık etkisi önemlidir")),
                DeepSection("Karstik şekiller", "Kalker, jips ve kaya tuzu gibi çözünebilen kayaçların bulunduğu alanlarda lapya, dolin, uvala, polye, obruk ve mağara gibi şekiller gelişir.", listOf("Toroslar karstik şekiller açısından zengindir.", "Obruklar çözünme ve çökmenin birlikte görülebildiği şekillerdir."))
            ),
            "Dağların kıyıya paralel veya dik uzanışını sadece ezberleme; bunun yağış, ulaşım ve kıyı-iç kesim bağlantısına etkisini de kur.",
            listOf("Türkiye'de dağların genel uzanış yönü nedir?", "Delta ovası için hangi şartlar gerekir?", "Karstik şekiller hangi kayaçlarla ilişkilidir?")
        ),
        DeepLesson(
            "Su Varlığı", "Akarsu, göl, havza ve kıyıları neden-sonuçla öğren", "water",
            "Türkiye'nin akarsuları çoğunlukla kısa boylu, eğimleri fazla ve rejimleri düzensizdir. Su varlığını iklim, yer şekilleri ve kayaç yapısıyla birlikte değerlendirmek gerekir.",
            listOf(
                DeepSection("Akarsular", "Yağışın mevsimsel dağılışı ve kar erimeleri akım düzenini etkiler. Dağlık yapı ve yükselti hidroelektrik potansiyeli artırabilir.", listOf("Karadeniz akarsuları yağış nedeniyle yıl boyunca su taşıyabilir.", "Doğu Anadolu'da kar erimeleri ilkbahar akımını artırabilir.", "Eğim arttıkça aşındırma ve enerji potansiyeli artar.")),
                DeepSection("Havzalar", "Suların aynı göl, deniz veya okyanusa ulaştığı alan havzadır. Çevresine kapalı olan alanlar kapalı havza olarak adlandırılır.", listOf("Tuz Gölü çevresi kapalı havzaya örnektir.", "Karstik alanlarda yer altı drenajı da önem kazanır.")),
                DeepSection("Göller", "Göller oluşumlarına göre tektonik, volkanik, karstik, buzul, set ve karma kökenli olabilir. Türkiye'de göl çeşitliliği yüksektir.", listOf("Van Gölü → volkanik set", "Tuz Gölü → tektonik kökenli kapalı havza", "Beyşehir → karstik ve tektonik etkilerin görüldüğü alan")),
                DeepSection("Kıyı ve denizler", "Kıyı çizgisinin şekli; dağların uzanışı, aşındırma-biriktirme ve akarsu faaliyetleriyle değişir. Ege kıyılarında girinti-çıkıntı fazladır.", listOf("Boyuna kıyı → dağlar kıyıya paralel", "Enine kıyı → dağlar denize dik", "Delta oluşumu için sığ kıta sahanlığı önemlidir"))
            ),
            "Bir akarsuyun uzunluğu veya su miktarı tek başına enerji potansiyelini belirlemez; eğim ve düşü farkını da düşün.",
            listOf("Kapalı havza nedir?", "Akarsu rejimini hangi faktörler belirler?", "Delta oluşumu hangi kıyılarda kolaylaşır?")
        ),
        DeepLesson(
            "İklim ve Bitki", "Sıcaklık, yağış ve doğal bitki örtüsünü bağla", "climate",
            "Türkiye'de iklim çeşitliliğinin temelinde enlem, yükselti, denizellik-karasallık, dağların uzanışı ve bakı bulunur.",
            listOf(
                DeepSection("Akdeniz iklimi", "Yazlar sıcak ve kurak, kışlar ılık ve yağışlıdır. Doğal bitki örtüsü makidir. Kıyı kuşağında turunçgil, zeytin ve seracılık gelişebilir.", listOf("Maki → yaz kuraklığına uyum", "Kıyıda kış sıcaklıkları daha yüksektir", "Toroslar iç kesime geçişi etkiler")),
                DeepSection("Karadeniz iklimi", "Her mevsim yağış görülür ve doğal bitki örtüsü ormandır. Dağların kıyıya yakın ve paralel uzanması kıyı ile iç kesim arasında belirgin fark oluşturur.", listOf("Nemlilik yüksek", "Ormanlar geniş yer kaplar", "Çay ve fındık için uygun koşullar")),
                DeepSection("Karasal iklim", "İç kesimlerde deniz etkisinin azalmasıyla yıllık sıcaklık farkı artar. Bozkır, karasal koşullarda yaygın doğal bitki örtüsüdür.", listOf("İç Anadolu → bozkır", "Doğu Anadolu → yükselti + sert karasallık", "Yaz-kış sıcaklık farkı kıyılardan fazladır")),
                DeepSection("Bakı ve yükselti", "Kuzey yarım kürede güneye bakan yamaçlar güneşi daha dik alır. Yükselti arttıkça sıcaklık genel olarak azalır.", listOf("Bakı tarım ürünlerinin olgunlaşmasını etkiler.", "Aynı enlemde yükseltisi fazla olan yer daha soğuk olabilir."))
            ),
            "Bitki örtüsünü doğrudan ezberlemek yerine önce sıcaklık ve yağış koşulunu tahmin et; sonra bitkiyi çıkar.",
            listOf("Akdeniz bitkisinin adı nedir?", "Karadeniz'de yağış neden fazladır?", "Yükselti sıcaklığı nasıl etkiler?")
        ),
        DeepLesson(
            "Nüfus ve Yerleşme", "Dağılış, yoğunluk, göç ve şehirleşmeyi yorumla", "population",
            "Nüfusun dağılışı doğal ve beşerî faktörlerin ortak sonucudur. İklim, yükselti ve yer şekillerinin yanında sanayi, ulaşım, tarım ve hizmetler de belirleyicidir.",
            listOf(
                DeepSection("Nüfus dağılışı", "Marmara, Ege kıyıları ve büyük ulaşım-sanayi merkezlerinde nüfus daha yoğundur. Yüksek ve engebeli sahalarda nüfus genellikle seyrektir.", listOf("Sanayi → iş gücü çeker", "Ulaşım → yerleşmeyi destekler", "İklim → yaşama koşullarını etkiler")),
                DeepSection("Yoğunluk", "Aritmetik nüfus yoğunluğu toplam nüfusun yüzölçümüne bölünmesiyle bulunur. Nüfusu fazla olan her alanın yoğunluğu da fazla olmak zorunda değildir.", listOf("Yoğunluk = nüfus / alan", "Yüzölçümü büyükse yoğunluk düşebilir")),
                DeepSection("Göç", "İş, eğitim, sağlık ve güvenlik gibi nedenlerle gerçekleşen göçler nüfusun dağılışını değiştirir. Kırdan kente göç şehirleşmeyi hızlandırır.", listOf("İtici faktör → işsizlik gibi nedenler", "Çekici faktör → iş ve eğitim olanakları")),
                DeepSection("Yerleşme", "Kırsal yerleşmeler tarım ve hayvancılıkla; kentsel yerleşmeler sanayi, hizmet ve ticaretle daha güçlü ilişki kurar.", listOf("Toplu yerleşme → suyun sınırlı olduğu alanlar", "Dağınık yerleşme → yağışlı ve parçalı araziler"))
            ),
            "Nüfus miktarı ile nüfus yoğunluğunu birbirine karıştırma; yoğunluk alanı da hesaba katar.",
            listOf("Yoğunluk nasıl hesaplanır?", "Göçün iki temel nedeni nedir?", "Dağınık yerleşme nerelerde yaygındır?")
        ),
        DeepLesson(
            "Tarım ve Hayvancılık", "Ürünü iklim, su, toprak ve pazarla eşleştir", "agriculture",
            "Tarım sorularında tek bir ezber yerine ürünün istediği sıcaklık, yağış, don koşulu, sulama ve pazar yakınlığını birlikte düşünmek en güvenli yöntemdir.",
            listOf(
                DeepSection("Tahıllar", "Buğday ve arpa karasal koşullara uyumlu ürünlerdir. İç Anadolu başta olmak üzere geniş tarım alanlarında yetiştirilir.", listOf("Buğday → İç Anadolu ile güçlü eşleştirme", "Sulama arttıkça ürün çeşitliliği artabilir")),
                DeepSection("Endüstri bitkileri", "Sanayiye hammadde sağlayan şeker pancarı, pamuk, tütün ve ayçiçeği gibi ürünlerin dağılışında iklim ve işleme tesislerine yakınlık önemlidir.", listOf("Pamuk → sıcaklık ve uzun yetişme dönemi", "Şeker pancarı → sulama ve fabrikaya yakınlık")),
                DeepSection("Meyve ve özel ürünler", "Çay ve fındık nemli Karadeniz; zeytin, üzüm ve turunçgil ılıman kış koşullarıyla ilişkilidir.", listOf("Çay → Doğu Karadeniz", "Fındık → Karadeniz", "Zeytin → Ege ve Akdeniz kıyıları")),
                DeepSection("Hayvancılık", "Mera varlığı, bitki örtüsü, iklim ve pazar koşulları hayvancılığın türünü etkiler. Büyükbaş daha nemli-çayırlık alanlarla, küçükbaş daha kurak ve bozkır alanlarla ilişkilendirilebilir.", listOf("Arıcılık → çiçek çeşitliliği", "Küçükbaş → bozkır ve maki alanları"))
            ),
            "Bir ürünün en fazla üretildiği yer ile ülke genelindeki tek yetişme alanını aynı şey sanma.",
            listOf("Çay hangi iklim koşulunu ister?", "Buğdayın güçlü bölgesel eşleştirmesi nedir?", "Hayvancılıkta mera neden önemlidir?")
        ),
        DeepLesson(
            "Maden ve Enerji", "Kaynak, çıkarım alanı ve enerji üretimini ayır", "mine",
            "Madenlerin dağılışı jeolojik yapı ile; enerji üretimi ise kaynağın yanında su, ulaşım, tüketim merkezi ve tesis koşullarıyla ilişkilidir.",
            listOf(
                DeepSection("Kömür ve petrol", "Taş kömürü Zonguldak çevresiyle, linyit ise Türkiye'nin farklı bölgelerindeki termik santrallerle ilişkilidir. Petrol üretiminde Güneydoğu Anadolu öne çıkar.", listOf("Taş kömürü → Zonguldak", "Petrol → Batman çevresi", "Linyit → çok sayıda havza")),
                DeepSection("Metal madenleri", "Demir, bakır, krom, bor ve boksit gibi kaynaklar farklı jeolojik koşullarda bulunur. Sınavlarda maden-merkez eşleştirmesi sık kullanılır.", listOf("Bor → Eskişehir-Kütahya-Balıkesir kuşağı", "Bakır → Doğu Karadeniz ve Doğu Anadolu'da önemli sahalar")),
                DeepSection("Hidroelektrik", "Akarsuyun debisi, eğimi ve yükselti farkı hidroelektrik potansiyeli belirler. Doğu ve Güneydoğu Anadolu'daki büyük projeler bu açıdan önemlidir.", listOf("Baraj → enerji + sulama + taşkın kontrolü", "Eğim ve düşü farkı enerji potansiyelini artırır")),
                DeepSection("Yenilenebilir enerji", "Güneş, rüzgâr, jeotermal ve biyokütle kaynaklarının potansiyeli bölgesel doğal koşullara göre değişir.", listOf("Jeotermal → Batı Anadolu", "Güneş → özellikle güney ve iç kesimler", "Rüzgâr → Ege kıyıları başta olmak üzere uygun koridorlar"))
            ),
            "Madenin çıkarıldığı yer, işlendiği sanayi merkezi ve elektriğin üretildiği tesis aynı yer olmak zorunda değildir.",
            listOf("Taş kömürü hangi merkezle eşleşir?", "HES potansiyelini ne artırır?", "Jeotermal enerji hangi bölgede yoğundur?")
        ),
        DeepLesson(
            "Sanayi ve Ulaşım", "Üretimin neden belirli yerlerde toplandığını çöz", "factory",
            "Sanayi; hammadde, enerji, sermaye, iş gücü, pazar ve ulaşımın birlikte değerlendirilmesiyle açıklanır. Türkiye'de Marmara bu faktörlerin çoğunu bir arada bulundurur.",
            listOf(
                DeepSection("Sanayi faktörleri", "Bir fabrikanın yer seçiminde hammaddeye, pazara ve ulaşım ağlarına erişim kadar enerji ve iş gücü de önemlidir.", listOf("Hammadde", "Pazar", "Ulaşım", "Enerji", "Sermaye ve iş gücü")),
                DeepSection("Bölgesel dağılış", "Marmara'da sanayi ve hizmetler yoğunlaşmıştır. Ege, Akdeniz, İç Anadolu ve diğer bölgelerde de farklı sektörlerin geliştiği merkezler vardır.", listOf("İstanbul-Kocaeli-Bursa çevresi → yoğun sanayi", "İzmir → liman, ticaret ve sanayi", "Gaziantep → sanayi ve ticaret")),
                DeepSection("Ulaşım", "Dağların uzanışı ve yükselti karayolu ve demiryolu güzergâhlarını etkiler. Boğazlar ve limanlar uluslararası taşımacılıkta stratejik rol oynar.", listOf("Liman → deniz ticareti", "Geçit → karayolu bağlantısı", "Boğaz → transit geçiş")),
                DeepSection("Ticaret", "Nüfus, üretim, ulaşım ve limanlar ticaretin gelişmesini destekler. İhracat ve ithalatın gerçekleşmesinde limanların bağlantı gücü önemlidir.", listOf("İç kesim üretimi → limanlara bağlantı ihtiyacı", "Büyük şehirler → geniş pazar"))
            ),
            "Bir sanayi tesisinin yerini sadece hammaddeyle açıklama; pazar, ulaşım ve iş gücünü de kontrol et.",
            listOf("Sanayi neden Marmara'da yoğunlaşır?", "Limanın ekonomiye katkısı nedir?", "Ulaşımı yer şekilleri nasıl etkiler?")
        ),
        DeepLesson(
            "Turizm", "Doğal ve kültürel çekicilikleri turizm türleriyle eşleştir", "tourism",
            "Türkiye'nin kıyıları, dağları, jeolojik şekilleri ve tarihî mirası farklı turizm türlerinin gelişmesini sağlar. Mevsim ve ulaşılabilirlik de turizm talebini etkiler.",
            listOf(
                DeepSection("Kıyı turizmi", "Akdeniz ve Ege kıyılarında yaz turizmi gelişmiştir. Uzun yaz dönemi, sıcak denizler ve kıyı tesisleri önemlidir.", listOf("Antalya → kıyı turizmi", "Muğla → koylar ve kıyı turizmi", "İzmir çevresi → kıyı + kültür")),
                DeepSection("Kış turizmi", "Yükselti ve kar kalınlığı kış turizminin temel doğal koşullarıdır. Uludağ, Palandöken ve Erciyes gibi merkezler örnektir.", listOf("Uludağ → Bursa", "Palandöken → Erzurum", "Erciyes → Kayseri")),
                DeepSection("Kültür turizmi", "Tarihî kentler, antik kalıntılar, müzeler ve inanç merkezleri kültür turizmini destekler.", listOf("Kapadokya → doğal + kültürel", "Efes → arkeolojik miras", "İstanbul → çok katmanlı tarih")),
                DeepSection("Turizmde sürdürülebilirlik", "Doğal alanların korunması, taşıma kapasitesi ve yerel ekonominin desteklenmesi uzun vadeli turizm için önemlidir.", listOf("Çevre korunması", "Yerel ürün ve istihdam", "Aşırı yapılaşmanın kontrolü"))
            ),
            "Turizm merkezini soruda verilen doğal veya kültürel ipucundan çıkar; şehir adını tek başına ezberlemeye çalışma.",
            listOf("Kış turizminin temel şartı nedir?", "Kapadokya hangi turizm türlerini birleştirir?", "Akdeniz kıyılarında turizm neden uzundur?")
        ),
        DeepLesson(
            "Bölgeler", "7 bölgeyi tek tek değil karşılaştırmalı öğren", "region",
            "Bölge sorularında en güçlü yöntem; iklim + yer şekli + tarım + nüfus + ekonomik faaliyetleri aynı tablo içinde karşılaştırmaktır.",
            listOf(
                DeepSection("Marmara", "Nüfus, sanayi, ticaret ve ulaşım bakımından Türkiye'nin en yoğun bölgesidir. Avrupa-Asya geçişi ve boğazlar önemlidir.", listOf("Sanayi", "Ticaret", "Yoğun nüfus", "Boğazlar")),
                DeepSection("Ege ve Akdeniz", "Ege'de dağların denize dik uzanışı ve tarım; Akdeniz'de kıyı turizmi, seracılık ve Toroslar belirgindir.", listOf("Ege → zeytin, üzüm, tütün", "Akdeniz → turunçgil, seracılık, turizm")),
                DeepSection("Karadeniz", "Yağışlı iklim, ormanlar ve dağların kıyıya paralel uzanışı belirgindir. Çay ve fındık önemli tarım ürünleridir.", listOf("Yağış", "Orman", "Çay", "Fındık")),
                DeepSection("İç Anadolu", "Karasal iklim, bozkır, tahıl tarımı ve geniş platolarla tanınır. Ankara ve Konya gibi merkezler önemlidir.", listOf("Buğday", "Bozkır", "Karasal iklim", "Geniş düzlükler")),
                DeepSection("Doğu ve Güneydoğu Anadolu", "Doğu Anadolu'da yükselti ve sert karasallık; Güneydoğu Anadolu'da daha sıcak koşullar ve GAP ile sulama-tarım dönüşümü öne çıkar.", listOf("Doğu → yüksek plato, hayvancılık, kış turizmi", "Güneydoğu → GAP, pamuk, tarla tarımı"))
            ),
            "Bölge sorularında tek bir özelliğe bakarak karar verme; en az iki doğal ve bir beşerî ipucunu birlikte doğrula.",
            listOf("Marmara'nın ekonomik imzası nedir?", "Karadeniz'in tarım ürünlerinden ikisini söyle.", "GAP hangi bölgede tarımı değiştirir?")
        ),
        DeepLesson(
            "Doğal Afetler", "Afetin nedenini bul, sonra dağılışını yorumla", "danger",
            "Doğal afetler rastgele değildir. Faylar, eğim, yağış, zemin, bitki örtüsü ve kar koşulları afet riskinin dağılışını belirler.",
            listOf(
                DeepSection("Deprem", "Türkiye Alp-Himalaya deprem kuşağında yer alır. Kuzey Anadolu, Doğu Anadolu ve Batı Anadolu'daki fay sistemleri farklı risk alanları oluşturur.", listOf("Fay → kırılma ve sarsıntı", "Zemin koşulları hasarı etkileyebilir", "Deprem riski ile bina güvenliği birlikte değerlendirilmelidir")),
                DeepSection("Heyelan", "Eğimli arazide suya doygun zeminlerin kütle halinde hareketidir. Yağışlı ve eğimli sahalarda risk artar.", listOf("Doğu Karadeniz → uygun koşullar", "Eğim + yağış + zemin birlikte düşünülür")),
                DeepSection("Erozyon", "Toprağın su ve rüzgâr tarafından taşınmasıdır. Bitki örtüsünün zayıf olduğu, eğimli veya yanlış kullanılan arazilerde artabilir.", listOf("Bitki örtüsü koruyucudur", "Aşırı otlatma ve yanlış tarım riski artırabilir")),
                DeepSection("Sel ve çığ", "Kısa sürede aşırı yağış sel riskini; eğimli yamaçlarda biriken kar çığ riskini artırabilir.", listOf("Sel → yağış + drenaj", "Çığ → kar örtüsü + eğim + meteorolojik koşullar"))
            ),
            "Afet sorularında şehir ezberinden önce oluşum koşulunu düşün. Aynı şehirde birden fazla afet riski bulunabilir.",
            listOf("Heyelanı artıran üç koşul nedir?", "Erozyonu hangi uygulamalar artırır?", "Çığ için hangi iki temel doğal koşul gerekir?")
        ),
        DeepLesson(
            "Harita Bilgisi", "Ölçek, izohips ve yön bilgisini soru çözümüne dönüştür", "map",
            "Harita bir ezber aracı değil, ölçü ve sembollerle bilgi çıkarma aracıdır. Ölçek ve izohips bilgisi oturduğunda birçok coğrafya sorusu kolaylaşır.",
            listOf(
                DeepSection("Ölçek", "Haritadaki uzunluğun gerçekteki uzunluğa oranıdır. Büyük ölçekli haritalar daha küçük alanı daha ayrıntılı gösterir.", listOf("Büyük ölçek → ayrıntı fazla", "Büyük ölçek → gösterilen alan küçük", "Ölçek paydası küçüldükçe ölçek büyür")),
                DeepSection("İzohips", "Aynı yükselti değerine sahip noktaları birleştiren eğrilerdir. Eğrilerin sıklaşması eğimin arttığını gösterir.", listOf("Kapalı eğriler → tepe veya çukur olabilir", "İzohips aralığı sabittir", "Deniz seviyesi 0 metre kabul edilir")),
                DeepSection("Profil", "Bir doğrultu boyunca arazinin yükselti değişimini kesit şeklinde göstermektir. Profil sorularında önce başlangıç-bitiş noktalarının yükselti ilişkisi bulunur.", listOf("Yükselti değişimini gösterir", "Eğim karşılaştırmasına yardım eder")),
                DeepSection("Yön ve koordinat", "Paralel ve meridyenler konum belirlemede kullanılır. Pusula yönleri ve koordinatlar birlikte okunabilir.", listOf("Kuzey-güney → enlem", "Doğu-batı → boylam", "Yerel saat → meridyen"))
            ),
            "Büyük ölçek 'daha büyük harita' demek değildir; daha fazla ayrıntı ve daha küçük gösterilen alan demektir.",
            listOf("Büyük ölçekli haritanın üç özelliğini söyle.", "İzohipsler sıklaşırsa ne olur?", "Yerel saat hangi çizgilerle ilgilidir?")
        )
    )

    private val byTitle = lessons.associateBy { it.title }
    fun forTopic(topic: Topic): DeepLesson = byTitle[topic.title] ?: DeepLesson(
        topic.title, topic.subtitle, topic.icon, topic.subtitle,
        listOf(DeepSection("Temel kavramlar", topic.subtitle, topic.lessons)),
        "Önce kavramları ayır, sonra soru çöz.",
        topic.lessons.take(3)
    )
}
