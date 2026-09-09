package tr.yurdunubil.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Minimal Compose diagnostic launcher.
 * No Supabase, app navigation, notifications, V2/V4 UI, or custom theme is touched.
 */
class ModernLaunchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeBootProbe()
        }
    }
}

@Composable
private fun ComposeBootProbe() {
    Text(
        text = "COMPOSE OK\n\nYurdunu Bil\nCompose startup test",
        color = Color.White,
        fontSize = 24.sp,
        fontWeight = FontWeight.Normal
    )
}
