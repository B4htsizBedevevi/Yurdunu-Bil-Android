package tr.yurdunubil.app

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView

/**
 * Minimal native diagnostic launcher.
 * No Compose, Supabase, notifications, or other app code is touched.
 */
class ModernLaunchActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(TextView(this).apply {
            text = "BOOT OK\n\nYurdunu Bil\nnative startup test"
            textSize = 24f
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.rgb(6, 20, 15))
            gravity = Gravity.CENTER
            setPadding(32, 32, 32, 32)
        })
    }
}
