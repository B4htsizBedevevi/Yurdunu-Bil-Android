package tr.yurdunubil.app

data class Question(
    val id: Int,
    val subject: String,
    val topic: String,
    val text: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

data class QuizResult(
    val correct: Int,
    val wrong: Int,
    val blank: Int,
    val xp: Int
) {
    val total: Int get() = correct + wrong + blank
}

class QuizEngine(private val questions: List<Question>) {
    private var index = 0
    private var correct = 0
    private var wrong = 0
    private var blank = 0

    val current: Question get() = questions[index]
    val currentIndex: Int get() = index
    val size: Int get() = questions.size

    fun answer(optionIndex: Int?) {
        if (optionIndex == null) blank++
        else if (optionIndex == current.correctIndex) correct++
        else wrong++
        if (index < questions.lastIndex) index++
    }

    fun result(): QuizResult = QuizResult(correct, wrong, blank, correct * 10)
}

object SampleQuestions {
    val all = listOf(
        Question(1, "Coğrafya", "Türkiye'nin Coğrafi Konumu", "Türkiye'nin üç tarafının denizlerle çevrili olması aşağıdakilerden hangisini doğrudan etkiler?", listOf("Kıyı turizmi potansiyelini", "Meridyen sayısını", "Matematik konumunu", "Yükselti ortalamasını", "Yerel saat farkını"), 0, "Denizlerle çevrili olmak kıyıların uzunluğu, iklim ve kıyı turizmi gibi özellikleri doğrudan etkiler."),
        Question(2, "Coğrafya", "Dağlar", "Türkiye'de dağların genel olarak doğu-batı doğrultusunda uzanmasının sonuçlarından biri aşağıdakilerden hangisidir?", listOf("Kuzey-güney yönlü ulaşımın bazı yerlerde zorlaşması", "Yerel saat farkının artması", "Güneş ışınlarının geliş açısının değişmesi", "Meridyen sayısının artması", "Gece-gündüz sürelerinin eşitlenmesi"), 0, "Kuzey Anadolu ve Toros dağlarının uzanışı, kıyı ile iç kesimler arasındaki ulaşımı bazı geçitlerde zorlaştırır."),
        Question(3, "Coğrafya", "Akarsular", "Türkiye akarsularının rejimlerinin genellikle düzensiz olmasının temel nedeni aşağıdakilerden hangisidir?", listOf("Yağışın yıl içine düzensiz dağılması", "Ülkenin üç tarafının denizlerle çevrili olması", "Meridyen farkının fazla olması", "Nüfusun kıyılarda yoğunlaşması", "Maden çeşitliliğinin fazla olması"), 0, "Türkiye'de yağışın mevsimlere göre değişmesi ve kar erimeleri akarsu debilerinin yıl içinde dalgalanmasına neden olur."),
        Question(4, "Coğrafya", "Bölgeler", "Türkiye'nin en fazla yağış alan bölgesi aşağıdakilerden hangisidir?", listOf("Karadeniz", "İç Anadolu", "Güneydoğu Anadolu", "Doğu Anadolu", "Marmara"), 0, "Karadeniz kıyıları, özellikle Doğu Karadeniz, yıl boyunca nemli hava ve orografik yağışların etkisiyle çok yağış alır."),
        Question(5, "Coğrafya", "Madenler", "Türkiye'de bor minerallerinin önemli rezervlere sahip olduğu alanlardan biri aşağıdakilerden hangisidir?", listOf("Eskişehir-Kırka", "Rize-Hopa", "Mersin-Silifke", "Şanlıurfa-Harran", "Muğla-Bodrum"), 0, "Eskişehir Kırka, Türkiye'nin önemli bor yataklarından biridir."
        )
    )
}
