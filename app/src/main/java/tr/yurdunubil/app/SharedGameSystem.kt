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

/** Single source of truth. The question bank is initialized lazily so startup never loads every question at launch. */
object SharedQuestionPool {
    /**
     * Startup-safe lazy pool. Each bank is isolated so one malformed optional bank
     * cannot terminate the whole application during the first frame.
     */
    val all: List<Question> by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        buildList {
            addBankSafely { FullQuestionBank.all }
            addBankSafely { ExpansionQuestionBank.all }
            addBankSafely { UnifiedQuestionBank.all }
            addBankSafely { MegaQuestionBank.all }
        }.distinctBy { it.id }
    }

    private fun MutableList<Question>.addBankSafely(loader: () -> List<Question>) {
        runCatching { loader() }.onSuccess { addAll(it) }
    }

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
        "Tarım ve Hayvancılık" -> setOf("Tarım", "Ekonomik Coğrafya", "Hayvancılık", "Tarım ve Hayvancılık")
        "Maden ve Enerji" -> setOf("Maden ve Enerji")
        "Sanayi ve Ulaşım" -> setOf("Sanayi ve Ulaşım")
        "Turizm" -> setOf("Turizm")
        "Bölgeler" -> setOf("Bölgeler")
        "Doğal Afetler" -> setOf("Doğal Afetler")
        "Toprak ve Bitki" -> setOf("Toprak", "Toprak ve Bitki", "İklim ve Bitki Örtüsü")
        "Su, Toprak ve Bitki Bağlantısı" -> setOf("Su Varlığı", "Toprak", "İklim ve Bitki Örtüsü")
        "Harita Bilgisi" -> setOf("Harita Bilgisi")
        "Ticaret ve Ulaşım" -> setOf("Ticaret ve Ulaşım", "Sanayi ve Ulaşım")
        else -> emptySet()
    }

    fun dailyMode(): SharedGameMode = SharedGameModes.daily(LocalDate.now())
}

object SharedGameModes {
    val quick = SharedGameMode("quick", "Hızlı 10", "10 KPSS tipi soru • hızını test et", "⚡", 10, 120, 100)
    val map = SharedGameMode("map", "Harita Avı", "İl • bölge • yer şekli bağlantılarını çöz", "🗺️", 8, 120, 80, setOf("Coğrafi Konum", "Yer Şekilleri", "Bölgeler", "Su Varlığı", "Maden ve Enerji"))
    val regions = SharedGameMode("regions", "Bölge Avı", "Bölgelerden gelen sorularla seri yap", "🧭", 10, 150, 120, setOf("Bölgeler", "Tarım", "İklim ve Bitki Örtüsü", "Nüfus ve Yerleşme"))
    val mines = SharedGameMode("mines", "Maden Avı", "Maden • merkez • enerji eşleştir", "⛏️", 10, 150, 120, setOf("Maden ve Enerji"))
    val agriculture = SharedGameMode("agriculture", "Tarım Avı", "Ürünleri iklim ve bölgeyle eşleştir", "🌾", 10, 150, 120, setOf("Tarım", "Ekonomik Coğrafya", "Hayvancılık", "Tarım ve Hayvancılık"))
    val relief = SharedGameMode("relief", "Şekil Avı", "Vadi • delta • karst • buzul şekillerini yakala", "⛰️", 12, 180, 150, setOf("Yer Şekilleri", "Kıyı Tipleri"))
    val climate = SharedGameMode("climate", "İklim Dedektifi", "Basınç • sıcaklık • yağış bağlantılarını çöz", "🌦️", 12, 180, 150, setOf("İklim ve Bitki Örtüsü"))
    val population = SharedGameMode("population", "Nüfus Radarı", "Yoğunluk • göç • nüfus özelliklerini bul", "👥", 10, 150, 130, setOf("Nüfus ve Yerleşme"))
    val trade = SharedGameMode("trade", "Türkiye Ticaret Rotası", "İthalat • ihracat • transit ticareti çöz", "🚚", 10, 150, 130, setOf("Sanayi ve Ulaşım", "Ticaret ve Ulaşım"))
    val atlas = SharedGameMode("atlas", "Atlas Sprint", "Harita ve mekân bilgisini 15 soruda hızlandır", "🗺️", 15, 180, 190)
    val chain = SharedGameMode("chain", "Bilgi Zinciri", "Arka arkaya doğru cevaplarla çarpanı artır", "🔥", 12, 180, 160)
    val master = SharedGameMode("master", "Türkiye Ustası", "Karışık, zorlayıcı KPSS coğrafya turu", "🏆", 18, 240, 250)
    val duel = SharedGameMode("duel", "1v1 Bilgi Düellosu", "Aynı ortak havuzdan rakibinden hızlı ol", "⚔️", 10, 150, 200, arena = true)
    val speedArena = SharedGameMode("speed", "Hız Arenası", "Ortak havuzdan süre bitmeden en yüksek skoru yap", "⚡", 12, 90, 220, arena = true)
    val regionArena = SharedGameMode("region-arena", "Bölge Savaşı", "Seçilen bölge uzmanlığını puana çevir", "🗺️", 10, 150, 220, setOf("Bölgeler", "Tarım", "İklim ve Bitki Örtüsü"), arena = true)
    val hardArena = SharedGameMode("master-arena", "Türkiye Ustası Arena", "Ortak havuzdan zor karışık sorularla lig puanı kovala", "🏆", 15, 180, 300, arena = true)

    fun daily(date: LocalDate): SharedGameMode = when (date.dayOfYear % 8) {
        0 -> quick
        1 -> relief
        2 -> climate
        3 -> regions
        4 -> agriculture
        5 -> population
        6 -> atlas
        else -> master
    }

    val games = listOf(map, quick, relief, climate, regions, agriculture, mines, population, trade, atlas, chain, master)
    val arenaModes = listOf(duel, regionArena, speedArena, hardArena)

    fun eventForToday(): SharedGameMode = when (LocalDate.now().dayOfYear % 6) {
        0 -> relief
        1 -> mines
        2 -> regions
        3 -> climate
        4 -> agriculture
        else -> atlas
    }
}
