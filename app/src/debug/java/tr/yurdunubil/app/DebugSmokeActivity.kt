package tr.yurdunubil.app

import android.app.Activity
import android.os.Bundle

/** Debug-only entry point used by CI to exercise the real V4 activity without exporting it in production. */
class DebugSmokeActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(android.content.Intent(this, RetentionMainActivity::class.java))
        finish()
    }
}
