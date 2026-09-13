package tr.yurdunubil.app

data class CurrentSource(
    val name: String,
    val referencePeriod: String,
    val verifiedDate: String,
    val url: String,
    val note: String
)

/** Official sources used when refreshing the current-information library. */
object CurrentDataRegistry {
    val sources = listOf(
        CurrentSource("TÜİK • ADNKS Sonuçları", "2025", "2026-02-09", "https://data.tuik.gov.tr/Bulten/Index?p=Adrese-Dayali-Nufus-Kayit-Sonuclari-2025-53899", "Türkiye nüfusu, kent-kır, yoğunluk ve il karşılaştırmaları."),
        CurrentSource("TÜİK • İç Göç İstatistikleri", "2025", "2026-07-14", "https://veriportali.tuik.gov.tr/Bulten/Index?dil=1&p=İç-Göç-İstatistikleri-2025-58139", "İç göç sayısı, illerin aldığı/verdiği göç ve yaş/neden dağılımları."),
        CurrentSource("TÜİK • Uluslararası Göç İstatistikleri", "2025", "2026-06-24", "https://veriportali.tuik.gov.tr/tr/press/58140/metadata", "Türkiye'ye gelen ve Türkiye'den giden uluslararası göç."),
        CurrentSource("TÜİK • Bitkisel Üretim 1. Tahmini", "2026", "2026-05-21", "https://veriportali.tuik.gov.tr/tr/press/58012/metadata", "2026 tarla, tahıl, meyve ve belirli ürünler için ilk tahminler."),
        CurrentSource("MGM • Aylık Sıcaklık Analizi", "2026-08", "2026-09-10", "https://www.mgm.gov.tr/veridegerlendirme/sicaklik-analizi.aspx", "Ağustos 2026 sıcaklık ortalamaları ve ekstrem değerler."),
        CurrentSource("MGM • Aylık Yağış Raporu", "2026-08", "2026-09-10", "https://www.mgm.gov.tr/VERIDEGERLENDIRME/yagis-raporu.aspx?b=a", "Ağustos 2026 yağış toplamları ve normallerle karşılaştırma."),
        CurrentSource("Enerji ve Tabii Kaynaklar Bakanlığı • Elektrik", "2026-07", "2026-08-19", "https://enerji.gov.tr/bilgi-merkezi-enerji-elektrik", "Kurulu güç, kaynak payları, üretim ve santral sayıları.")
    )

    const val refreshRule = "Yeni resmi bülten yayımlandığında ilgili konu, ders özeti, CurrentFact ve soru havuzu birlikte güncellenmelidir."
}
