package tr.yurdunubil.app

import java.time.LocalDate
import java.util.Locale
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
    val subtopics: Set<String> = emptySet(),
    val arena: Boolean = false
)

/** Fine-grained tags are derived from the same Question objects used by every game mode. */
object QuestionTaxonomy {
    private fun haystack(q: Question): String =
        (q.topic + " " + q.text + " " + q.options.joinToString(" ")).lowercase(Locale("tr", "TR"))

    fun tags(q: Question): Set<String> {
        val h = haystack(q)
        val result = mutableSetOf<String>()
        when (q.topic) {
            "Yer Şekilleri", "Jeolojik Yapı", "Kıyı Tipleri" -> {
                result += "Yer Şekilleri"
                if (listOf("ova", "ovası", "oval", "delta").any(h::contains)) result += "Ovalar"
                if (listOf("plato", "platolar").any(h::contains)) result += "Platolar"
                if (listOf("dağ", "dağlar", "dağlık", "zirve").any(h::contains)) result += "Dağlar"
                if (listOf("vadi", "kanyon", "şelale").any(h::contains)) result += "Vadiler"
                if (listOf("karst", "kalker", "mağara", "obruk", "traverten", "jips").any(h::contains)) result += "Karstik Şekiller"
                if (listOf("kıyı", "ria", "lagün", "delta kıyı", "falez", "tombolo").any(h::contains)) result += "Kıyı Şekilleri"
                if (listOf("volkan", "lav", "tüf", "krater").any(h::contains)) result += "Volkanik Şekiller"
                if (listOf("buzul", "sirk", "moren", "buzullaş").any(h::contains)) result += "Buzul Şekilleri"
            }
            "Su Varlığı" -> {
                result += "Su Varlığı"
                if (listOf("akarsu", "nehir", "ırmak", "çay", "dere", "havza", "rejim").any(h::contains)) result += "Akarsular"
                if (listOf("göl", "göller", "gölü").any(h::contains)) result += "Göller"
                if (listOf("baraj", "hidroelektrik", "hidroelektrik", "hes").any(h::contains)) result += "Su Enerjisi"
                if (listOf("kaynak", "yeraltı su", "karstik su").any(h::contains)) result += "Yeraltı Suları"
            }
            "Tarım", "Ekonomik Coğrafya" -> {
                result += "Tarım"
                if (listOf("buğday", "arpa", "mısır", "pirinç", "tahıl").any(h::contains)) result += "Tahıllar"
                if (listOf("pamuk", "tütün", "şeker pancarı", "ayçiçeği").any(h::contains)) result += "Endüstri Bitkileri"
                if (listOf("zeytin", "çay", "fındık", "turunçgil", "üzüm", "incir").any(h::contains)) result += "Bahçe Bitkileri"
                if (listOf("hayvancılık", "mera", "büyükbaş", "küçükbaş").any(h::contains)) result += "Hayvancılık"
            }
            "İklim ve Bitki Örtüsü" -> {
                result += "İklim ve Bitki Örtüsü"
                if (listOf("akdeniz", "maki").any(h::contains)) result += "Akdeniz İklimi"
                if (listOf("karadeniz", "nemli", "yağışlı", "orman").any(h::contains)) result += "Karadeniz İklimi"
                if (listOf("karasal", "bozkır", "kuraklık").any(h::contains)) result += "Karasal İklim"
                if (listOf("sıcaklık", "izoterm", "yükselti").any(h::contains)) result += "Sıcaklık"
            }
            "Nüfus ve Yerleşme" -> {
                result += "Nüfus ve Yerleşme"
                if (listOf("nüfus", "yoğunluk", "nüfus yoğunluğu").any(h::contains)) result += "Nüfus"
                if (listOf("göç", "göçmen", "kırsal", "kent").any(h::contains)) result += "Göç ve Yerleşme"
            }
            "Maden ve Enerji" -> result += "Maden ve Enerji"
            "Sanayi ve Ulaşım" -> result += "Sanayi ve Ulaşım"
            "Turizm" -> result += "Turizm"
            "Bölgeler" -> result += "Bölgeler"
            "Harita Bilgisi" -> result += "Harita Bilgisi"
            "Doğal Afetler" -> result += "Doğal Afetler"
            "Coğrafi Konum" -> result += "Coğrafi Konum"
        }
        return result
    }

    fun matches(q: Question, requested: Set<String>): Boolean =
        requested.isEmpty() || tags(q).any(requested::contains)
}

object SharedQuestionPool {
    /** One source for tests, normal games, events and Arena. */
    val all: List<Question> = (FullQuestionBank.all + ExpansionQuestionBank.all + MegaQuestionBank.all)
        .distinctBy { it.id }

    private fun shuffleOptions(question: Question, seed: Long): Question {
        val pairs = question.options.mapIndexed { index, option -> index to option }
            .shuffled(Random(seed xor question.id.toLong()))
        val correctIndex = pairs.indexOfFirst { it.first == question.correctIndex }
        return question.copy(options = pairs.map { it.second }, correctIndex = correctIndex)
    }

    fun pick(mode: SharedGameMode, seed: Long = System.currentTimeMillis()): List<Question> {
        val topicSource = if (mode.topics.isEmpty()) all else all.filter { it.topic in mode.topics }
        val source = if (mode.subtopics.isEmpty()) topicSource
        else topicSource.filter { QuestionTaxonomy.matches(it, mode.subtopics) }
        val fallback = if (source.isEmpty()) topicSource else source
        val count = mode.questions.coerceAtMost(fallback.size)
        return fallback.distinctBy { it.id }.shuffled(Random(seed)).take(count).map { shuffleOptions(it, seed) }
    }

    fun topicForLibrary(title: String): Set<String> = when (title) {
        "Coğrafi Konum" -> setOf("Coğrafi Konum")
        "Yer Şekilleri" -> setOf("Yer Şekilleri", "Jeolojik Yapı", "Kıyı Tipleri")
        "Su Varlığı" -> setOf("Su Varlığı")
        "İklim ve Bitki" -> setOf("İklim ve Bitki Örtüsü")
        "Nüfus ve Yerleşme" -> setOf("Nüfus ve Yerleşme")
        "Tarım ve Hayvancılık" -> setOf("Tarım", "Ekonomik Coğrafya")
        "Maden ve Enerji" -> setOf("Maden ve Enerji")
        "Sanayi ve Ulaşım" -> setOf("Sanayi ve Ulaşım")
        "Turizm" -> setOf("Turizm")
        "Bölgeler" -> setOf("Bölgeler")
        "Doğal Afetler" -> setOf("Doğal Afetler")
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
    val agriculture = SharedGameMode("agriculture", "Tarım Avı", "Ürünleri iklim ve bölgeyle eşleştir", "🌾", 10, 150, 120, setOf("Tarım", "Ekonomik Coğrafya"))
    val climate = SharedGameMode("climate", "İklim Avı", "İklim • bitki • sıcaklık ilişkilerini yakala", "🌦️", 10, 150, 120, setOf("İklim ve Bitki Örtüsü"))
    val water = SharedGameMode("water", "Su Varlığı Avı", "Akarsu • göl • baraj sorularında hızlan", "💧", 10, 150, 120, setOf("Su Varlığı"))
    val population = SharedGameMode("population", "Nüfus Avı", "Nüfus ve yerleşme ilişkilerini çöz", "👥", 10, 150, 120, setOf("Nüfus ve Yerleşme"))
    val chain = SharedGameMode("chain", "Bilgi Zinciri", "Arka arkaya doğru cevaplarla çarpanı artır", "🔥", 12, 180, 160)
    val master = SharedGameMode("master", "Türkiye Ustası", "Karışık, zorlayıcı KPSS coğrafya turu", "🏆", 18, 240, 250)
    val duel = SharedGameMode("duel", "1v1 Bilgi Düellosu", "Aynı soru havuzunda rakibinden hızlı ol", "⚔️", 10, 150, 200, arena = true)
    val speedArena = SharedGameMode("speed", "Hız Arenası", "Süre bitmeden en yüksek skoru yap", "⚡", 12, 90, 220, arena = true)
    val regionArena = SharedGameMode("region-arena", "Bölge Savaşı", "Seçilen bölge uzmanlığını puana çevir", "🗺️", 10, 150, 220, setOf("Bölgeler", "Tarım", "İklim ve Bitki Örtüsü"), arena = true)
    val hardArena = SharedGameMode("master-arena", "Türkiye Ustası Arena", "Zor karışık sorularla lig puanı kovala", "🏆", 15, 180, 300, arena = true)

    fun daily(date: LocalDate): SharedGameMode = when (date.dayOfYear % 7) {
        0 -> quick; 1 -> regions; 2 -> agriculture; 3 -> climate; 4 -> water; 5 -> population; else -> chain
    }

    val games = listOf(map, quick, regions, mines, agriculture, climate, water, population, chain, master)
    val arenaModes = listOf(duel, regionArena, speedArena, hardArena)

    fun eventForToday(): SharedGameMode = when (LocalDate.now().dayOfYear % 4) {
        0 -> map; 1 -> mines; 2 -> regions; else -> agriculture
    }
}
