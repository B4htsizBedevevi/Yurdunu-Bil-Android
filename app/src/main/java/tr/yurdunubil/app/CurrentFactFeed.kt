package tr.yurdunubil.app

data class CurrentFact(
    val title: String,
    val value: String,
    val detail: String,
    val source: String,
    val year: String,
    val icon: String
)

object CurrentFactFeed {
    val all = listOf(
        CurrentFact("En büyük yüz ölçümü", "Konya", "Türkiye'nin yüz ölçümü bakımından en büyük ili Konya'dır. 2025 ADNKS bülteninde Konya bu özellikle birlikte veriliyor.", "TÜİK", "2025", "🗺️"),
        CurrentFact("En düşük nüfus yoğunluğu", "Tunceli • 11 kişi/km²", "2025 ADNKS sonuçlarında nüfus yoğunluğu en düşük il Tunceli oldu.", "TÜİK", "2025", "👥"),
        CurrentFact("Bal üretiminde lider", "Ordu • 13.001 ton", "2024 yılında Türkiye toplam bal üretiminin %13,6'sı Ordu'da gerçekleşti ve il ilk sırada yer aldı.", "Tarım ve Orman Bakanlığı", "2024", "🍯"),
        CurrentFact("Türkiye bal üretimi", "95.492 ton", "2024 yılında Türkiye'nin bal üretimi yaklaşık 95,5 bin ton olarak gerçekleşti.", "Tarım ve Orman Bakanlığı", "2024", "🐝"),
        CurrentFact("Borun önemli merkezi", "Eskişehir • Kırka", "Kırka, Türkiye'nin başlıca bor yataklarından biridir; bor alanları Batı Anadolu'da yoğunlaşır.", "Enerji ve Tabii Kaynaklar Bakanlığı", "2026", "⛏️"),
        CurrentFact("Bor üretimindeki konum", "Dünya lideri", "Enerji Bakanlığına göre Türkiye 2024'te dünya bor üretiminin %48'ini karşıladı.", "Enerji ve Tabii Kaynaklar Bakanlığı", "2026", "⚡"),
        CurrentFact("Taş kömürü", "Zonguldak", "Zonguldak çevresi Türkiye'de taş kömürü ile özdeşleşen başlıca sahadır.", "KPSS temel bilgi", "2026", "⛏️"),
        CurrentFact("Petrol", "Batman", "Batman ve çevresi Türkiye'nin önemli petrol üretim alanlarından biridir.", "KPSS temel bilgi", "2026", "🛢️"),
        CurrentFact("En yoğun nüfuslu bölge", "Marmara", "Sanayi, hizmet, ticaret ve ulaşımın yoğunluğu Marmara'da nüfus yoğunluğunu artırır.", "KPSS temel bilgi", "2026", "🏙️"),
        CurrentFact("En büyük coğrafi bölge", "Doğu Anadolu", "Yüz ölçümü bakımından Türkiye'nin en büyük coğrafi bölgesidir.", "KPSS temel bilgi", "2026", "⛰️"),
        CurrentFact("İç göç", "2,475 milyon kişi", "2025'te iller arası göç eden kişi sayısı 2 milyon 475 bin 19 oldu.", "TÜİK", "2025", "🚆"),
        CurrentFact("En fazla iç göç alan il", "İstanbul • 329.912 kişi", "2025'te iller arası göçte en fazla göç alan il İstanbul oldu.", "TÜİK", "2025", "📍")
    )
}
