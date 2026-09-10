package tr.yurdunubil.app

import android.content.SharedPreferences
import java.time.LocalDate

/** Centralized, local-only progress bookkeeping for stable V2. */
object ProgressTracker {
    fun recordQuiz(
        prefs: SharedPreferences,
        mode: SharedGameMode,
        total: Int,
        correct: Int,
        wrong: Int
    ): Int {
        val safeTotal = total.coerceAtLeast(0)
        val safeCorrect = correct.coerceIn(0, safeTotal)
        val safeWrong = wrong.coerceIn(0, safeTotal - safeCorrect)

        // XP reflects actual performance: wrong/blank answers never grant
        // the mode's full reward. Perfect runs receive a small bonus.
        val participationXp = safeCorrect * 10
        val perfectBonus = if (safeTotal > 0 && safeCorrect == safeTotal) 30 else 0

        val today = LocalDate.now()
        val todayKey = today.toString()
        val lastDate = prefs.getString("streak_last_date", null)
        val oldStreak = prefs.getInt("streak", 0)
        val newStreak = when {
            lastDate == todayKey -> oldStreak.coerceAtLeast(1)
            lastDate == today.minusDays(1).toString() -> oldStreak + 1
            else -> 1
        }

        // The daily challenge gets a one-time completion bonus. Replaying it
        // on the same day still counts toward statistics, but cannot farm XP.
        val isDaily = mode.id == SharedQuestionPool.dailyMode().id
        val dailyAlreadyDone = prefs.getString("daily_completed_date", null) == todayKey
        val dailyBonus = if (isDaily && !dailyAlreadyDone && safeTotal > 0) 50 else 0
        val earnedXp = participationXp + perfectBonus + dailyBonus

        val editor = prefs.edit()
            .putInt("solved", prefs.getInt("solved", 0) + safeTotal)
            .putInt("correct", prefs.getInt("correct", 0) + safeCorrect)
            .putInt("wrong", prefs.getInt("wrong", 0) + safeWrong)
            .putInt("xp", prefs.getInt("xp", 0) + earnedXp)
            .putInt("streak", newStreak)
            .putString("streak_last_date", todayKey)
            .putString("last_activity", "${mode.title} • $safeCorrect/$safeTotal • +$earnedXp XP")

        if (isDaily && !dailyAlreadyDone && safeTotal > 0) {
            editor.putString("daily_completed_date", todayKey)
        }

        editor.apply()
        return earnedXp
    }
}
