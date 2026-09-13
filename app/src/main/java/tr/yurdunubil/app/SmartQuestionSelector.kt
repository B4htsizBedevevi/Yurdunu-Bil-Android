package tr.yurdunubil.app

import android.content.SharedPreferences
import kotlin.math.max

/** Picks practice questions using each user's local topic performance. */
object SmartQuestionSelector {
    private fun key(topic: String, suffix: String) =
        "topic_" + suffix + "_" + topic.lowercase().replace(Regex("[^a-z0-9çğıöşü]+"), "_").trim('_')

    fun record(prefs: SharedPreferences, topic: String, correct: Boolean) {
        val attemptsKey = key(topic, "attempts")
        val correctKey = key(topic, "correct")
        val attempts = prefs.getInt(attemptsKey, 0) + 1
        val correctCount = prefs.getInt(correctKey, 0) + if (correct) 1 else 0
        prefs.edit()
            .putInt(attemptsKey, attempts)
            .putInt(correctKey, correctCount)
            .apply()
    }

    fun accuracy(prefs: SharedPreferences, topic: String): Float {
        val attempts = prefs.getInt(key(topic, "attempts"), 0)
        if (attempts == 0) return 0.5f
        return prefs.getInt(key(topic, "correct"), 0).toFloat() / max(1, attempts)
    }

    private const val RECENT_KEY = "recent_question_ids"
    private const val RECENT_LIMIT = 30

    fun remember(prefs: SharedPreferences, questions: Collection<Question>) {
        val old = prefs.getString(RECENT_KEY, "")?.split(",")?.mapNotNull { it.toIntOrNull() }.orEmpty()
        val merged = (questions.map { it.id } + old).distinct().take(RECENT_LIMIT)
        prefs.edit().putString(RECENT_KEY, merged.joinToString(",")).apply()
    }

    fun pick(mode: SharedGameMode, prefs: SharedPreferences, seed: Long = System.currentTimeMillis()): List<Question> {
        val base = SharedQuestionPool.pick(mode, seed)
        if (base.size <= 1) return base
        val recent = prefs.getString(RECENT_KEY, "")?.split(",")?.mapNotNull { it.toIntOrNull() }?.toSet().orEmpty()
        val fresh = base.filterNot { it.id in recent }
        val candidate = if (fresh.size >= base.size / 2) fresh + base.filter { it.id in recent }.take(base.size - fresh.size) else base
        val ranked = candidate.sortedWith(compareBy<Question> { accuracy(prefs, it.topic) }.thenBy { it.id })
        val weakCount = max(1, ranked.size * 2 / 3)
        val weakFirst = ranked.take(weakCount)
        val rest = ranked.drop(weakCount).shuffled(java.util.Random(seed xor 0x5EEDL))
        return (weakFirst + rest).take(base.size).shuffled(java.util.Random(seed + 17))
    }

    fun weakestTopic(prefs: SharedPreferences, questions: Collection<Question>): String? =
        questions.map { it.topic }.distinct().minByOrNull { accuracy(prefs, it) }
}
