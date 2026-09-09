package tr.yurdunubil.app

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat

/** Production entry point for the current V4 UI. */
class RetentionMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        runCatching {
            setContent {
                runCatching {
                    YurdunuBilMainV4()
                }.getOrElse { error ->
                    StartupFailureScreen(error)
                }
            }
        }.onFailure { error ->
            Toast.makeText(this, "Yurdunu Bil başlatılamadı: ${error.javaClass.simpleName}", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}

@androidx.compose.runtime.Composable
private fun StartupFailureScreen(error: Throwable) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Yurdunu Bil açılırken bir sorun oluştu.", style = MaterialTheme.typography.titleMedium)
        Text(
            "Uygulama güvenli moda geçemedi. ${error.javaClass.simpleName}",
            style = MaterialTheme.typography.bodySmall
        )
    }
}
