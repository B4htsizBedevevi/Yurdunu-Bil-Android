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
        if (optionIndex == null) {
            blank++
        } else if (optionIndex == current.correctIndex) {
            correct++
        } else {
            wrong++
        }
        if (index < questions.lastIndex) index++
    }

    fun result(): QuizResult = QuizResult(
        correct = correct,
        wrong = wrong,
        blank = blank,
        xp = correct * 10
    )
}

object SampleQuestions {
    val all = listOf(
        Question(
            id = 1,
            subject = "Coğrafya",
            topic = "Türkiye'nin Coğrafi Konumu",
            text = "Türkiye'nin üç tarafının denizlerle çevrili olması aşağıdakilerden hangisini doğrudan etkiler?",
            options = listOf("Kıyı turizmi potansiyelini", "Meridyen sayısını", "Matematik konumunu", "Yükselti ortalamasını", "Yerel saat farkını"),
            correctIndex = 0,
            explanation = "Denizlerle çevrili olmak kıyıların uzunluğu ve kıyı turizmi gibi beşerî ve ekonomik özellikleri doğrudan etkiler."
        ),
        Question(
            id = 2,
            subject = "Tarih",
            topic = "Kurtuluş Savaşı",
            text = "Milli Mücadele döneminde ulusal egemenlik düşüncesini en açık biçimde yansıtan gelişme hangisidir?",
            options = listOf("Amasya Genelgesi", "Mudanya Ateşkesi", "Lozan Antlaşması", "Tekalif-i Milliye", "Gümrü Antlaşması"),
            correctIndex = 0,
            explanation = "Amasya Genelgesi'nde milletin bağımsızlığını yine milletin azim ve kararı kurtaracaktır anlayışı vurgulanmıştır."
        ),
        Question(
            id = 3,
            subject = "Türkçe",
            topic = "Cümlede Anlam",
            text = "Aşağıdaki cümlelerin hangisinde neden-sonuç ilişkisi vardır?",
            options = listOf("Sınavı kazanmak için düzenli çalışıyor.", "Yağmur yağdığı için maç ertelendi.", "Akşam seni arayacağım.", "Bu kitabı geçen hafta aldım.", "Daha sonra birlikte konuşuruz."),
            correctIndex = 1,
            explanation = "Maçın ertelenmesinin nedeni yağmurun yağmasıdır; 'için' burada neden anlamı vermektedir."
        )
    )
}
