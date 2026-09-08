package tr.yurdunubil.app

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.FlowType
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.functions.Functions
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime

/**
 * Single Supabase client for the native Android app.
 *
 * Lazily creating the client prevents network/client initialization from being
 * part of Android activity startup. The publishable key is safe to ship in a
 * client application; authorization remains enforced by Supabase Auth + RLS.
 */
object SupabaseClientProvider {
    val client: io.github.jan.supabase.SupabaseClient by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_PUBLISHABLE_KEY
        ) {
            install(Auth) {
                // PKCE is required for secure mobile OAuth / email-link recovery flows.
                flowType = FlowType.PKCE
                scheme = "yurdunubil"
                host = "auth"
            }
            install(Postgrest)
            install(Realtime)
            install(Functions)
        }
    }
}
