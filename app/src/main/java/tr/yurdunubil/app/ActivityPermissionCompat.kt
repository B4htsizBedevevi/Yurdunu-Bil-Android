package tr.yurdunubil.app

import android.app.Activity

/** Lets permission requests compile safely when the Activity comes from a nullable Context cast. */
fun Activity?.requestPermissions(permissions: Array<String>, requestCode: Int) {
    this?.requestPermissions(permissions, requestCode)
}
