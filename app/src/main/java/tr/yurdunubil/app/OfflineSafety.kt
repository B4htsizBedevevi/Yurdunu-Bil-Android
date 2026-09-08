package tr.yurdunubil.app

/** Defensive helpers used by the native app to keep malformed content from reaching the quiz UI. */
object OfflineSafety {
    fun safeQuestions(source: List<Question>, limit: Int = 20): List<Question> = source
        .asSequence()
        .filter { it.options.size >= 2 }
        .filter { it.correctIndex in it.options.indices }
        .filter { it.text.isNotBlank() && it.explanation.isNotBlank() }
        .distinctBy { it.id }
        .shuffled()
        .take(limit)
        .toList()

    fun safeTopic(topic: String?): String = topic?.trim()?.takeIf { it.isNotEmpty() } ?: "Genel Coğrafya"
}
