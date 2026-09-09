package tr.yurdunubil.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

/**
 * Minimal Material3 diagnostic launcher.
 * No Supabase, app navigation, notifications, V2/V4 UI, or app-specific theme is touched.
 */
class ModernLaunchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = Color(0xFF35D07F),
                    background = Color(0xFF06140F),
                    surface = Color(0xFF0B1F17),
                    onBackground = Color.White,
                    onSurface = Color.White
                )
            ) {
                ComposeMaterialBootProbe()
            }
        }
    }
}

@Composable
private fun ComposeMaterialBootProbe() {
    Text(
        text = "MATERIAL OK\n\nYurdunu Bil\nMaterial3 startup test",
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = 24.sp
    )
}
