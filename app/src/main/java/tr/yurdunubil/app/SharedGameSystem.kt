package tr.yurdunubil.app

import java.time.LocalDate
import java.util.Random

data class SharedGameMode(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: String,
    val questions: Int,
    val seconds: Int,
    val rewardXp: Int,
    val topics: Set<String> = emptySet(),
    val arena: Boolean = false
)

object SharedQuestionPool {
    val all: List<Question> = (FullQuestionBank.all + ExpansionQuestionBank.all + NextQuestionBank.all)
        .distinctBy { it.id }

    fun pick(mode: SharedGameMode, seed: Long = System.currentTimeMillis()): List<Question> {
        val source = if (mode.topics.isEmpty()) all else all.filter { it.topic in mode.topics }
        val safeSource = if (source.size >= mode.questions) source else all
        return safeSource.shuffled(Random(seed)).take(mode.questions.coerceAtMost(safeSource.size))
    }

    fun topicForLibrary(title: String): Set<String> = when (title) {
        "Coğrafi Konum" -> setOf("Coğrafi Konum")
        "Yer Şekilleri" -> setOf("Yer Şekilleri", "Jeolojik Yapı", "Kıyı Tipleri")
        "Su Varlığı" -> setOf("Su Varlığı")
        "İklim ve Bitki" -> setOf("İklim ve Bitki Örtüsü")
        "Nüfus ve Yerleşme" -> setOf("Nüfus ve Yerleşme")
        "Tarım ve Hayvancılık" -> setOf("Tarım", "Ekonomik Coğrafya", "Hayvancılık")
        "Maden ve Enerji" -> setOf("Maden ve Enerji")
        "Sanayi ve Ulaşım" -> setOf("Sanayi ve Ulaşım")
        "Turizm" -> setOf("Turizm")
        "Bölgeler" -> setOf("Bölgeler")
        "Doğal Afetler" -> setOf("Doğal Afetler")
        "Toprak ve Bitki" -> setOf("Toprak", "İklim ve Bitki Örtüsü")
        "Su, Toprak ve Bitki Bağlantısı" -> setOf("Su Varlığı", "Toprak", "İklim ve Bitki Örtüsü")
        "Harita Bilgisi" -> setOf("Harita Bilgisi")
        else -> emptySet()
    }

    fun dailyMode(): SharedGameMode = SharedGameModes.daily(LocalDate.now())
}

object SharedGameModes {
    val quick = SharedGameMode("quick", "Hızlı 10", "10 KPSS tipi soru • hızını test et", "⚡", 10, 120, 100)
    val map = SharedGameMode("map", "Harita Avı", "İl • bölge • yer şekli bağlantılarını çöz", "🗺️", 8, 120, 80, setOf("Coğrafi Konum", "Yer Şekilleri", "Bölgeler", "Su Varlığı", "Maden ve Enerji"))
    val regions = SharedGameMode("regions", "Bölge Avı", "Bölgelerden gelen sorularla seri yap", "🧭", 10, 150, 120, setOf("Bölgeler", "Tarım", "İklim ve Bitki Örtüsü", "Nüfus ve Yerleşme"))
    val mines = SharedGameMode("mines", "Maden Avı", "Maden • merkez • enerji eşleştir", "⛏️", 10, 150, 120, setOf("Maden ve Enerji"))
    val agriculture = SharedGameMode("agriculture", "Tarım Avı", "Ürünleri iklim ve bölgeyle eşleştir", "🌾", 10, 150, 120, setOf("Tarım", "Ekonomik Coğrafya", "Hayvancılık"))
    val chain = SharedGameMode("chain", "Bilgi Zinciri", "Arka arkaya doğru cevaplarla çarpanı artır", "🔥", 12, 180, 160)
    val master = SharedGameMode("master", "Türkiye Ustası", "Karışık, zorlayıcı KPSS coğrafya turu", "🏆", 18, 240, 250)
    val duel = SharedGameMode("duel", "1v1 Bilgi Düellosu", "Aynı soru havuzunda rakibinden hızlı ol", "⚔️", 10, 150, 200, arena = true)
    val speedArena = SharedGameMode("speed", "Hız Arenası", "Süre bitmeden en yüksek skoru yap", "⚡", 12, 90, 220, arena = true)
    val regionArena = SharedGameMode("region-arena", "Bölge Savaşı", "Seçilen bölge uzmanlığını puana çevir", "🗺️", 10, 150, 220, setOf("Bölgeler", "Tarım", "İklim ve Bitki Örtüsü"), arena = true)
    val hardArena = SharedGameMode("master-arena", "Türkiye Ustası Arena", "Zor karışık sorularla lig puanı kovala", "🏆", 15, 180, 300, arena = true)

    fun daily(date: LocalDate): SharedGameMode = when (date.dayOfYear % 4) {
        0 -> quick
        1 -> regions
        2 -> agriculture
        else -> chain
    }

    val games = listOf(map, quick, regions, mines, agriculture, chain, master)
    val arenaModes = listOf(duel, regionArena, speedArena, hardArena)

    fun eventForToday(): SharedGameMode = when (LocalDate.now().dayOfYear % 4) {
        0 -> map
        1 -> mines
        2 -> regions
        else -> agriculture
    }
}
