package tr.yurdunubil.app

/** Official first-party sources used to verify current-information questions. Last reviewed: 14 Sep 2026. */
object CurrentKnowledgeSources {
    data class Source(val name: String, val url: String, val scope: String)

    val all = listOf(
        Source("TÜİK — ADNKS 2025","https://data.tuik.gov.tr/Bulten/Index?p=Adrese-Dayali-Nufus-Kayit-Sistemi-Sonuclari-2025-53899","2025 nüfusu, artış hızı, yoğunluk, yabancı nüfus"),
        Source("TÜİK — İç Göç 2025","https://veriportali.tuik.gov.tr/Bulten/Index?dil=1&p=%C4%B0%C3%A7-G%C3%B6%C3%A7-%C4%B0statistikleri-2025-58139","iller arası göç, nedenler, yaş grupları"),
        Source("TÜİK — Uluslararası Göç 2025","https://veriportali.tuik.gov.tr/tr/press/58140/metadata","uluslararası göç"),
        Source("TÜİK — Bitkisel Üretim 1. Tahmin 2026","https://veriportali.tuik.gov.tr/tr/press/58012/metadata","2026 tarım tahminleri"),
        Source("TÜİK — İşgücü Haziran 2026","https://veriportali.tuik.gov.tr/tr/press/57986/metadata","işgücü ve genç işsizlik"),
        Source("TÜİK — GSYH II. Çeyrek 2026","https://veriportali.tuik.gov.tr/tr/press/58211/metadata","2026 büyüme"),
        Source("TÜİK — Turizm II. Çeyrek 2026","https://veriportali.tuik.gov.tr/Bulten/Index?dil=1&p=Turizm-%C4%B0statistikleri-II.%C3%87eyrek%3A-Nisan-Haziran%2C-2026-58143","turizm geliri ve ziyaretçi"),
        Source("TÜİK — TÜFE Ağustos 2026","https://veriportali.tuik.gov.tr/tr/press/58290/metadata","2026 Ağustos TÜFE"),
        Source("Enerji ve Tabii Kaynaklar Bakanlığı — Elektrik","https://enerji.gov.tr/haber-detay?id=31910","2026 Temmuz kurulu güç"),
        Source("Meteoroloji Genel Müdürlüğü — Aylık Sıcaklık Analizi","https://www.mgm.gov.tr/veridegerlendirme/sicaklik-analizi.aspx?ay=agustos","2026 Ağustos sıcaklık"),
        Source("Tarım ve Orman Bakanlığı — Arıcılık İstatistikleri","https://arastirma.tarimorman.gov.tr/aricilik/Link/2/Aricilik-Istatistikleri","2025 arıcılık"),
        Source("Harita Genel Müdürlüğü — İl/İlçe Yüz Ölçümleri","https://www.harita.gov.tr/il-ve-ilce-yuzolcumleri","il ve ilçe yüzölçümleri")
    )
}
