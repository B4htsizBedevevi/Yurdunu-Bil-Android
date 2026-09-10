package tr.yurdunubil.app

import android.content.Context
import android.content.SharedPreferences

/**
 * Single source of truth for local user preferences.
 * SharedPreferences survives normal APK updates; migrations keep older keys compatible.
 */
object YBPreferences {
    const val FILE_NAME = "yurdunu_bil_native"

    const val DARK_MODE = "dark_mode"
    const val LEGACY_DARK_THEME = "dark_theme"
    const val NOTIFICATIONS_ENABLED = "notifications_enabled"
    const val INTRO_SEEN = "welcome_seen"

    fun get(context: Context): SharedPreferences {
        val prefs = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        migrate(context, prefs)
        return prefs
    }

    private fun migrate(context: Context, prefs: SharedPreferences) {
        // Older V4 builds used dark_theme. Never throw away a user's choice during migration.
        if (!prefs.contains(DARK_MODE) && prefs.contains(LEGACY_DARK_THEME)) {
            prefs.edit()
                .putBoolean(DARK_MODE, prefs.getBoolean(LEGACY_DARK_THEME, false))
                .apply()
        }

        // The first-run screen originally used a separate preference file. Bring it into
        // the unified store so existing testers do not see the welcome screen again.
        if (!prefs.contains(INTRO_SEEN)) {
            val legacy = context.getSharedPreferences("yurdunu_bil_launch", Context.MODE_PRIVATE)
            if (legacy.getBoolean(INTRO_SEEN, false)) {
                prefs.edit().putBoolean(INTRO_SEEN, true).apply()
            }
        }
    }

    fun setDarkMode(prefs: SharedPreferences, enabled: Boolean) {
        prefs.edit()
            .putBoolean(DARK_MODE, enabled)
            .putBoolean(LEGACY_DARK_THEME, enabled)
            .apply()
    }

    fun markIntroSeen(context: Context) {
        get(context).edit().putBoolean(INTRO_SEEN, true).apply()
    }

    fun hasSeenIntro(context: Context): Boolean = get(context).getBoolean(INTRO_SEEN, false)
}
