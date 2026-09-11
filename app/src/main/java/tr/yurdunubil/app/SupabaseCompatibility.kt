package tr.yurdunubil.app

import android.content.Context
import android.content.Intent as AndroidIntent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth as supabaseAuth
import io.github.jan.supabase.postgrest.postgrest as supabasePostgrest

typealias Intent = AndroidIntent

/**
 * Compatibility aliases for older Yurdunu Bil call sites that relied on
 * package-wide Supabase extensions without importing them explicitly.
 */
val SupabaseClient.auth get() = this.supabaseAuth
val SupabaseClient.postgrest get() = this.supabasePostgrest

/** Compatibility overload retained for FCM/automation callers with custom metadata. */
fun NotificationHelper.showAnnouncement(
    context: Context,
    title: String,
    body: String,
    notificationId: Int,
    action: String
) = showAnnouncement(context, title, body)
