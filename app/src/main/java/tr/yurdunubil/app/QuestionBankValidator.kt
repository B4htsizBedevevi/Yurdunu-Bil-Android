package tr.yurdunubil.app

/** Lightweight runtime validation for the shared question catalogue. */
object QuestionBankValidator {
    data class Report(
        val total: Int,
        val duplicateIds: List<Int>,
        val malformed: List<Int>
    ) {
        val isValid: Boolean get() = duplicateIds.isEmpty() && malformed.isEmpty()
    }

    fun validate(questions: List<Question>): Report {
        val seen = HashSet<Int>()
        val duplicateIds = mutableListOf<Int>()
        val malformed = mutableListOf<Int>()

        questions.forEach { q ->
            if (!seen.add(q.id)) duplicateIds += q.id
            if (q.text.isBlank() || q.options.size != 5 || q.options.any(String::isBlank) ||
                q.correctIndex !in q.options.indices || q.explanation.isBlank()) {
                malformed += q.id
            }
        }
        return Report(questions.size, duplicateIds.distinct(), malformed.distinct())
    }

    fun requireValid(questions: List<Question>) {
        val report = validate(questions)
        check(report.isValid) {
            "Invalid question bank: total=" + report.total + ", duplicates=" + report.duplicateIds + ", malformed=" + report.malformed
        }
    }
}