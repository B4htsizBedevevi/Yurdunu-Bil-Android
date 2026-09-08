package tr.yurdunubil.app

/** Lightweight catalog metadata for the question bank. */
data class TopicBankInsight(
    val topic: String,
    val questionCount: Int,
    val noteCount: Int,
    val difficultyMix: String
)

object QuestionBankInsights {
    fun build(): List<TopicBankInsight> {
        val questions = SharedQuestionPool.all
        return GeographyLibraryContent.notes.map { note ->
            val count = questions.count { it.topic == note.title || it.topic == topicAlias(note.title) }
            TopicBankInsight(note.title, count, 1, "Bilgi • yorum • ilişkilendirme")
        }
    }

    private fun topicAlias(title: String): String = when (title) {
        "Tarım ve Hayvancılık" -> "Tarım"
        "Toprak ve Bitki" -> "İklim ve Bitki Örtüsü"
        "Su, Toprak ve Bitki Bağlantısı" -> "Su Varlığı"
        else -> title
    }
}
