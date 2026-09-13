package tr.yurdunubil.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/** Production entry point for the refreshed V4 experience. */
class RetentionMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("yurdunu_bil_native", MODE_PRIVATE)
        val firstRunTutorialSeen = prefs.getBoolean("first_run_tutorial_seen", false)

        if (!NotificationHelper.canNotify(this)) {
            prefs.edit().putBoolean("notifications_enabled", false).apply()
            NotificationHelper.cancelDaily(this)
        }
        MainScope().launch {
            if (NotificationHelper.isEnabled(this@RetentionMainActivity) && NotificationHelper.canNotify(this@RetentionMainActivity)) {
                NotificationHelper.registerCurrentToken(this@RetentionMainActivity)
                NotificationAutomation.syncAndSchedule(this@RetentionMainActivity)
            }
        }
        setContent {
            Box(modifier = Modifier.fillMaxSize()) {
                YurdunuBilMainV4()
                QuickActionsFloatingEntry()
            }
        }

        if (!firstRunTutorialSeen) {
            startActivity(Intent(this, FirstRunTutorialActivity::class.java))
        }
    }
}