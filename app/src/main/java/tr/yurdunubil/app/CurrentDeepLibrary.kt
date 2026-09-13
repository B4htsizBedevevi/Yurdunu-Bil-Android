package tr.yurdunubil.app

/** Current-data study lessons paired with the verified 2025-2026 question bank. */
object CurrentDeepLibrary {
    val lessons = listOf(
        DeepLesson(
            "Güncel Türkiye","2025 ADNKS ve güncel ülke göstergeleri","pulse",
            "Güncel sorularda yıl ve veri kaynağı kritik. Bu ders, 2025 nüfus sonuçlarını temel alır ve rakamı kavramla birlikte öğretir.",
            listOf(
                DeepSection("2025 nüfusu","31 Aralık 2025 itibarıyla Türkiye nüfusu 86.092.168 kişidir.", listOf("Yıllık nüfus artış hızı: binde 5", "Erkek: %50,02 • Kadın: %49,98")),
                DeepSection("Kentleşme","İl ve ilçe merkezlerinde yaşayanların oranı %93,6'ya yükselmiştir.", listOf("Yoğun kent: %67,5", "Orta yoğun kent: %15,8", "Kır: %16,8")),
                DeepSection("İl karşılaştırması","İstanbul 15.754.053 kişiyle en kalabalık ildir; Ankara 5.910.320 ve İzmir 4.504.185 kişiyle onu izler.", listOf("İstanbul toplam nüfusun %18,3'ünü barındırır", "Nüfus yoğunluğu en düşük il: Tunceli, 11 kişi/km²", "Konya nüfus yoğunluğu: 59 kişi/km²")),
                DeepSection("Sınav taktiği","Güncel sayılarda 'nüfus' ile 'nüfus yoğunluğu'nu ayır ve referans yılını mutlaka kontrol et.", listOf("Miktar ≠ yoğunluk", "Veri yılı sorunun ayrılmaz parçasıdır", "Resmî kurum kaynağına bak"))
            ),
            "Güncel bir rakamı ezberlerken yılını ve hangi göstergiyi ifade ettiğini birlikte ezberle.",
            listOf("2025 Türkiye nüfusu kaçtır?","Nüfus yoğunluğu en düşük il hangisidir?","İl ve ilçe merkezlerinin oranı kaçtır?")
        ),
        DeepLesson(
            "Güncel Göç ve Demografi","2025 iç göç, dış göç ve yaş yapısı","population",
            "Göç soruları yalnızca sayı değil; yaş grubu, göç nedeni ve yönü üzerinden de sorulabilir.",
            listOf(
                DeepSection("İç göç","2025'te iller arasında 2.475.019 kişi göç etti; oran %2,87 oldu.", listOf("En fazla göç alan: İstanbul", "En fazla göç veren: İstanbul", "20-24 yaş grubu: 480.185 kişi")),
                DeepSection("Göç nedenleri","En önemli neden 564.114 kişiyle hanedeki fertlerden birine bağımlı göç oldu.", listOf("Daha iyi konut ve yaşam koşulları: 510.226", "Eğitim: 406.144")),
                DeepSection("Uluslararası göç","2025'te Türkiye'ye 393.829 kişi göç etti; Türkiye'den 403.216 kişi yurt dışına göç etti.", listOf("Gelenlerin 91.952'si Türk vatandaşı", "Gelenlerin 301.877'si yabancı uyruklu")),
                DeepSection("Genç nüfus","2025 sonunda 15-24 yaş genç nüfusu 12.708.348 kişi ve toplam nüfusun %14,8'i oldu.", listOf("Erkek genç nüfus %51,2", "Kadın genç nüfus %48,8"))
            ),
            "İç göç ile uluslararası göçü aynı gösterge sanma; soruda yön ve kapsamı mutlaka kontrol et.",
            listOf("2025 iç göç kaç kişidir?","En fazla göç alan il hangisidir?","Genç nüfus oranı nedir?")
        ),
        DeepLesson(
            "Güncel Tarım ve Üretim","2026 ilk tahminlerini coğrafi yorumla","agriculture",
            "2026 tarım ilk tahmini, bir önceki yılın üretim düşüşünü ve beklenen toparlanmayı karşılaştırmak için özellikle değerlidir.",
            listOf(
                DeepSection("Genel görünüm","2026 ilk tahmininde tarla ürünlerinin %12,6, meyve-içecek-baharat grubunun %57,8 artması beklenmektedir.", listOf("Tarla ürünleri: 75,4 milyon ton", "Sebzeler: 33,3 milyon ton", "Meyve-içecek-baharat: yaklaşık 31 milyon ton")),
                DeepSection("Tahıllar","2026 tahıl üretiminin %21,7 artarak 41,6 milyon ton olması beklenmektedir.", listOf("Buğday: 22,8 milyon ton", "Arpa: 9 milyon ton", "Mısır: 8 milyon ton ve %5,9 azalış")),
                DeepSection("Meyveler","Meyve grubunda özellikle 2025'teki sert düşüşlerin ardından 2026 için güçlü artışlar öngörülmüştür.", listOf("Kiraz: +%255,7", "Antep fıstığı: +%113,6", "Fındık: +%62,2", "Zeytin: +%55,7")),
                DeepSection("Sınav taktiği","Rakamı tek başına ezberlemek yerine 'önceki yıla göre artış/azalış' şeklinde öğren.", listOf("Mısır negatif ayrışıyor", "Meyve grubu güçlü toparlanıyor", "Tahılda buğday ve arpa öne çıkıyor"))
            ),
            "Tahmin ile gerçekleşmiş veriyi karıştırma; 2026 verileri burada ilk tahmindir ve sonraki bültende değişebilir.",
            listOf("2026 tahıl üretimi kaç milyon ton?","Mısır için tahmin nedir?","Fındıkta değişim oranı kaçtır?")
        ),
        DeepLesson(
            "Güncel İklim ve Çevre","2026 Ağustos sıcaklık ve yağış verileri","climate",
            "Güncel iklim sorularında normal değer, gerçekleşen değer ve bir önceki yıl arasındaki farkı okuyabilmek gerekir.",
            listOf(
                DeepSection("Sıcaklık","2026 Ağustos Türkiye ortalama sıcaklığı 25,7 °C oldu; 1991-2020 normali 25,1 °C idi.", listOf("Normalden +0,6 °C", "Son 56 yılın en sıcak 16. Ağustos'u")),
                DeepSection("Ekstremler","En düşük sıcaklık 4,5 °C ile Ardahan'da, en yüksek sıcaklık 46,7 °C ile Cizre'de ölçüldü.", listOf("Cizre → Güneydoğu Anadolu", "Ardahan → Doğu Anadolu")),
                DeepSection("Bölgesel ortalamalar","Ege 27,5 °C, Akdeniz 28,2 °C, Marmara 25,4 °C ortalamaya ulaştı.", listOf("Akdeniz en yüksek bölgesel ortalamalardan biri", "İç Anadolu Ağustos ortalaması: 23,0 °C")),
                DeepSection("Yağış","2026 Ağustos Türkiye ortalama yağışı 16,2 mm oldu; normal 14,8 mm idi.", listOf("Normale göre yaklaşık %10 artış", "2025 Ağustos'a göre %100'den fazla artış"))
            ),
            "Normale göre artış ile geçen yıla göre artışı karıştırma; iki farklı kıyaslama tabanı vardır.",
            listOf("Ağustos 2026 ortalama sıcaklığı nedir?","En yüksek sıcaklık nerede ölçüldü?","Yağış normale göre nasıl değişti?")
        ),
        DeepLesson(
            "Güncel Enerji ve Ulaşım","2026 enerji gücü ve ulaştırma göstergeleri","bolt",
            "Enerji sorularında toplam kurulu güç, kaynak payı ve üretim payını birbirinden ayırmak gerekir.",
            listOf(
                DeepSection("Kurulu güç","2026 Temmuz sonu itibarıyla Türkiye'nin elektrik kurulu gücü 126.476 MW'a ulaştı.", listOf("Yenilenebilir payı: %62,8", "Yerli kaynakların payı: %71,9")),
                DeepSection("Kaynak dağılımı","Kurulu güçte hidrolik %25,6; güneş %21,7; doğal gaz %19,6; kömür %17,4; rüzgâr %12,1 paya sahiptir.", listOf("Güneş kurulu gücü: 27.507 MW", "Rüzgâr kurulu gücü: 15.358 MW")),
                DeepSection("Santral sayısı","2026 Temmuz sonu itibarıyla lisanssızlar dâhil 43.553 elektrik üretim santrali vardır.", listOf("41.458 güneş santrali", "779 hidroelektrik santrali", "415 rüzgâr santrali")),
                DeepSection("Üretim","2025'te elektrik üretimi 362,9 TWh oldu; üretimde kömürün payı %33,6, güneşin %10,5 oldu.", listOf("Üretim payı ile kurulu güç payı aynı değildir", "Bu ayrım sınav sorusunun ana tuzağı olabilir"))
            ),
            "Kurulu güçteki oranı elektrik üretimindeki oranla karıştırma; ikisi farklı göstergelerdir.",
            listOf("2026 kurulu güç kaç MW?","Yenilenebilir payı nedir?","Kurulu güç ile üretim payı arasındaki fark nedir?")
        )
    )
}
