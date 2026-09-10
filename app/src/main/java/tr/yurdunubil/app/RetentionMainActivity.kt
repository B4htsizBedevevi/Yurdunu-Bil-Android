package tr.yurdunubil.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier

/** Production entry point for the refreshed V4 experience. */
class RetentionMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!NotificationHelper.canNotify(this)) {
            getSharedPreferences("yurdunu_bil_native", MODE_PRIVATE).edit()
                .putBoolean("notifications_enabled", false)
                .apply()
            NotificationHelper.cancelDaily(this)
        }
        setContent {
            Box(modifier = Modifier.fillMaxSize()) {
                YurdunuBilMainV4()
                SocialFloatingEntry()
            }
        }
    }
}
