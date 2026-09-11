package tr.yurdunubil.app

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat

/** Production entry point for the refreshed V4 experience. */
class RetentionMainActivity : ComponentActivity() {
    private val themePrefs by lazy { getSharedPreferences("yurdunu_bil_native", MODE_PRIVATE) }
    private val themeListener = android.content.SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == "dark_theme") applySystemBars(themePrefs.getBoolean("dark_theme", false))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        themePrefs.registerOnSharedPreferenceChangeListener(themeListener)
        applySystemBars(themePrefs.getBoolean("dark_theme", false))

        if (!NotificationHelper.canNotify(this)) {
            themePrefs.edit()
                .putBoolean("notifications_enabled", false)
                .apply()
            NotificationHelper.cancelDaily(this)
        }
        setContent {
            Box(modifier = Modifier.fillMaxSize()) {
                YurdunuBilMainV4()
                SmartStudyFloatingEntry()
                SocialFloatingEntry()
                AdminFloatingEntry()
            }
        }
    }

    override fun onDestroy() {
        themePrefs.unregisterOnSharedPreferenceChangeListener(themeListener)
        super.onDestroy()
    }

    private fun applySystemBars(dark: Boolean) {
        window.statusBarColor = Color.parseColor(if (dark) "#06140F" else "#F3F7F5")
        window.navigationBarColor = Color.parseColor(if (dark) "#06140F" else "#F3F7F5")
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = !dark
            isAppearanceLightNavigationBars = !dark
        }
    }
}
