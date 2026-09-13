package tr.yurdunubil.app

import android.content.SharedPreferences
import java.time.Instant
import java.time.temporal.ChronoUnit

/** Local wrong-answer notebook + spaced-repetition memory for each question. */
object StudyMemory {
    private const val PREFIX = "memory_"
    private fun key(id: Int, suffix: String) = PREFIX + id + "_" + suffix

    fun record(prefs: SharedPreferences, question: Question, correct: Boolean) = recordId(prefs, question.id, correct)

    fun recordId(prefs: SharedPreferences, id: Int, correct: Boolean) {
        val now = Instant.now()
        val attempts = prefs.getInt(key(id, "attempts"), 0) + 1
        val wrongs = prefs.getInt(key(id, "wrongs"), 0) + if (correct) 0 else 1
        val oldLevel = prefs.getInt(key(id, "level"), 0)
        val level = if (correct) (oldLevel + 1).coerceAtMost(5) else 0
        val days = if (!correct) 0L else listOf(1L, 3L, 7L, 14L, 30L)[level.coerceIn(1, 5) - 1]
        val next = if (days == 0L) now.toEpochMilli() else now.plus(days, ChronoUnit.DAYS).toEpochMilli()
        prefs.edit()
            .putInt(key(id, "attempts"), attempts)
            .putInt(key(id, "wrongs"), wrongs)
            .putInt(key(id, "level"), level)
            .putLong(key(id, "last"), now.toEpochMilli())
            .putLong(key(id, "next"), next)
            .apply()
    }

    fun isWrong(prefs: SharedPreferences, id: Int): Boolean = prefs.getInt(key(id, "wrongs"), 0) > 0
    fun due(prefs: SharedPreferences, id: Int, now: Long = System.currentTimeMillis()): Boolean = prefs.getLong(key(id, "next"), 0L) <= now
    fun level(prefs: SharedPreferences, id: Int): Int = prefs.getInt(key(id, "level"), 0)
    fun nextReview(prefs: SharedPreferences, id: Int): Long = prefs.getLong(key(id, "next"), 0L)
    fun wrongIds(prefs: SharedPreferences, questions: Collection<Question>): Set<Int> = questions.map { it.id }.filter { isWrong(prefs, it) }.toSet()
}
