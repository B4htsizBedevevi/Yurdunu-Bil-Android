package tr.yurdunubil.app

import java.util.Locale

/** Runtime/CI gate for the shared question catalogue. */
object QuestionBankValidator {
    data class Report(
        val total: Int,
        val duplicateIds: List<Int>,
        val duplicateContent: List<Int>,
        val duplicateOptions: List<Int>,
        val malformed: List<Int>
    ) {
        val isValid: Boolean
            get() = duplicateIds.isEmpty() && duplicateContent.isEmpty() &&
                duplicateOptions.isEmpty() && malformed.isEmpty()
    }

    private fun normalize(value: String): String =
        value.trim().lowercase(Locale("tr", "TR")).replace(Regex("\\s+"), " ")

    private fun contentSignature(q: Question): String {
        val options = q.options.map(::normalize).sorted().joinToString("||")
        val correct = q.options.getOrNull(q.correctIndex)?.let(::normalize).orEmpty()
        return normalize(q.text) + "##" + options + "##" + correct
    }

    fun validate(questions: List<Question>): Report {
        val seenIds = HashSet<Int>()
        val seenContent = HashSet<String>()
        val duplicateIds = mutableListOf<Int>()
        val duplicateContent = mutableListOf<Int>()
        val duplicateOptions = mutableListOf<Int>()
        val malformed = mutableListOf<Int>()

        questions.forEach { q ->
            if (!seenIds.add(q.id)) duplicateIds += q.id

            val signature = contentSignature(q)
            if (!seenContent.add(signature)) duplicateContent += q.id

            val normalizedOptions = q.options.map(::normalize)
            if (normalizedOptions.size != normalizedOptions.distinct().size) duplicateOptions += q.id

            if (q.subject.isBlank() || q.topic.isBlank() || q.text.isBlank() ||
                q.options.size !in 4..5 || q.options.any(String::isBlank) ||
                q.correctIndex !in q.options.indices || q.explanation.isBlank() ||
                q.options.map(::normalize).any(String::isBlank)) {
                malformed += q.id
            }
        }
        return Report(
            total = questions.size,
            duplicateIds = duplicateIds.distinct(),
            duplicateContent = duplicateContent.distinct(),
            duplicateOptions = duplicateOptions.distinct(),
            malformed = malformed.distinct()
        )
    }

    fun requireValid(questions: List<Question>) {
        val report = validate(questions)
        check(report.isValid) {
            "Invalid question bank: total=${report.total}, ids=${report.duplicateIds}, " +
                "content=${report.duplicateContent}, options=${report.duplicateOptions}, " +
                "malformed=${report.malformed}"
        }
    }
}