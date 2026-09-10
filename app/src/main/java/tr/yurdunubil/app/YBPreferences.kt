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
        migrate(prefs)
        return prefs
    }

    fun migrate(prefs: SharedPreferences) {
        // Older V4 builds used dark_theme. Never throw away a user's choice during migration.
        if (!prefs.contains(DARK_MODE) && prefs.contains(LEGACY_DARK_THEME)) {
            prefs.edit()
                .putBoolean(DARK_MODE, prefs.getBoolean(LEGACY_DARK_THEME, false))
                .apply()
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
