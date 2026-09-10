package tr.yurdunubil.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize

/** Production entry point for the refreshed V4 experience. */
class RetentionMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Box(modifier = androidx.compose.ui.Modifier.fillMaxSize()) {
                YurdunuBilMainV4()
                SocialFloatingEntry()
            }
            if (!NotificationHelper.canNotify(this)) {
                getSharedPreferences("yurdunu_bil_native", MODE_PRIVATE).edit()
                    .putBoolean("notifications_enabled", false)
                    .apply()
                NotificationHelper.cancelDaily(this)
            }
        }
    }
}
