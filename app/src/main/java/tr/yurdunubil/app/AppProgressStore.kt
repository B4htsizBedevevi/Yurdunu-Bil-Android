package tr.yurdunubil.app

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.yurdunuBilStore by preferencesDataStore("yurdunu_bil_progress_v3")

/** Local-first state. The UI never depends on a network round-trip to save progress. */
class AppProgressStore(private val context: Context) {
    private object K {
        val solved = intPreferencesKey("solved")
        val correct = intPreferencesKey("correct")
        val wrong = intPreferencesKey("wrong")
        val xp = intPreferencesKey("xp")
        val streak = intPreferencesKey("streak")
        val lastDay = longPreferencesKey("last_day")
        val todaySolved = intPreferencesKey("today_solved")
        val todayCorrect = intPreferencesKey("today_correct")
        val todayDay = longPreferencesKey("today_day")
        val wrongIds = stringSetPreferencesKey("wrong_ids")
        val masteredIds = stringSetPreferencesKey("mastered_ids")
        val seenIds = stringSetPreferencesKey("seen_ids")
        val topicCorrect = stringPreferencesKey("topic_correct")
        val topicWrong = stringPreferencesKey("topic_wrong")
    }

    data class Snapshot(
        val solved: Int = 0,
        val correct: Int = 0,
        val wrong: Int = 0,
        val xp: Int = 0,
        val streak: Int = 0,
        val todaySolved: Int = 0,
        val todayCorrect: Int = 0,
        val wrongIds: Set<Int> = emptySet(),
        val masteredIds: Set<Int> = emptySet(),
        val seenIds: Set<Int> = emptySet(),
        val topicCorrect: Map<String, Int> = emptyMap(),
        val topicWrong: Map<String, Int> = emptyMap()
    )

    val snapshot: Flow<Snapshot> = context.yurdunuBilStore.data.map { p ->
        val today = System.currentTimeMillis() / 86_400_000L
        val savedToday = p[K.todayDay] ?: -1L
        Snapshot(
            solved = p[K.solved] ?: 0,
            correct = p[K.correct] ?: 0,
            wrong = p[K.wrong] ?: 0,
            xp = p[K.xp] ?: 0,
            streak = p[K.streak] ?: 0,
            todaySolved = if (savedToday == today) p[K.todaySolved] ?: 0 else 0,
            todayCorrect = if (savedToday == today) p[K.todayCorrect] ?: 0 else 0,
            wrongIds = (p[K.wrongIds] ?: emptySet()).mapNotNull(String::toIntOrNull).toSet(),
            masteredIds = (p[K.masteredIds] ?: emptySet()).mapNotNull(String::toIntOrNull).toSet(),
            seenIds = (p[K.seenIds] ?: emptySet()).mapNotNull(String::toIntOrNull).toSet(),
            topicCorrect = decodeCounts(p[K.topicCorrect]),
            topicWrong = decodeCounts(p[K.topicWrong])
        )
    }

    suspend fun record(question: Question, isCorrect: Boolean) {
        context.yurdunuBilStore.edit { p ->
            val today = System.currentTimeMillis() / 86_400_000L
            val currentWrong = (p[K.wrongIds] ?: emptySet()).toMutableSet()
            val mastered = (p[K.masteredIds] ?: emptySet()).toMutableSet()
            val seen = (p[K.seenIds] ?: emptySet()).toMutableSet()
            val correctByTopic = decodeCounts(p[K.topicCorrect]).toMutableMap()
            val wrongByTopic = decodeCounts(p[K.topicWrong]).toMutableMap()
            val id = question.id.toString()
            seen += id
            if (isCorrect) {
                currentWrong.remove(id)
                correctByTopic[question.topic] = (correctByTopic[question.topic] ?: 0) + 1
                val previousCorrect = correctByTopic[question.topic] ?: 0
                if (previousCorrect >= 3) mastered += id
            } else {
                currentWrong += id
                wrongByTopic[question.topic] = (wrongByTopic[question.topic] ?: 0) + 1
                mastered.remove(id)
            }
            val previousToday = if ((p[K.todayDay] ?: -1L) == today) p[K.todaySolved] ?: 0 else 0
            val previousTodayCorrect = if ((p[K.todayDay] ?: -1L) == today) p[K.todayCorrect] ?: 0 else 0
            val newToday = previousToday + 1
            val milestoneBonus = when (newToday) {
                5 -> 10
                10 -> 25
                20 -> 50
                else -> 0
            }
            p[K.solved] = (p[K.solved] ?: 0) + 1
            p[K.correct] = (p[K.correct] ?: 0) + if (isCorrect) 1 else 0
            p[K.wrong] = (p[K.wrong] ?: 0) + if (isCorrect) 0 else 1
            p[K.xp] = (p[K.xp] ?: 0) + if (isCorrect) 10 else 2 + milestoneBonus
            p[K.todaySolved] = newToday
            p[K.todayCorrect] = previousTodayCorrect + if (isCorrect) 1 else 0
            p[K.todayDay] = today
            p[K.wrongIds] = currentWrong
            p[K.masteredIds] = mastered
            p[K.seenIds] = seen
            p[K.topicCorrect] = encodeCounts(correctByTopic)
            p[K.topicWrong] = encodeCounts(wrongByTopic)
            updateStreakInPreferences(p, today)
        }
    }

    suspend fun updateStreak() {
        context.yurdunuBilStore.edit { p ->
            val today = System.currentTimeMillis() / 86_400_000L
            updateStreakInPreferences(p, today)
        }
    }

    private fun updateStreakInPreferences(p: MutablePreferences, today: Long) {
        val last = p[K.lastDay] ?: -2L
        p[K.streak] = when {
            last == today -> p[K.streak] ?: 0
            last == today - 1L -> (p[K.streak] ?: 0) + 1
            else -> 1
        }
        p[K.lastDay] = today
    }

    suspend fun clearWrong(questionId: Int) {
        context.yurdunuBilStore.edit { p ->
            p[K.wrongIds] = (p[K.wrongIds] ?: emptySet()) - questionId.toString()
        }
    }

    private fun encodeCounts(map: Map<String, Int>): String = map.entries.joinToString("|") { "${it.key.replace("|", " ")}=${it.value}" }
    private fun decodeCounts(raw: String?): Map<String, Int> = raw.orEmpty().split("|").mapNotNull {
        val i = it.lastIndexOf('=')
        if (i <= 0) null else it.substring(0, i) to (it.substring(i + 1).toIntOrNull() ?: 0)
    }.toMap()
}