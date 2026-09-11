import { createClient } from "https://esm.sh/@supabase/supabase-js@2";

const corsHeaders = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Headers": "authorization, x-client-info, apikey, content-type",
  "Access-Control-Allow-Methods": "POST, OPTIONS",
};

function json(data: unknown, status = 200) {
  return new Response(JSON.stringify(data), {
    status,
    headers: { ...corsHeaders, "content-type": "application/json" },
  });
}

Deno.serve(async (req) => {
  if (req.method === "OPTIONS") return new Response("ok", { headers: corsHeaders });
  if (req.method !== "POST") return json({ error: "method_not_allowed" }, 405);

  const authHeader = req.headers.get("Authorization");
  if (!authHeader?.startsWith("Bearer ")) return json({ error: "unauthorized" }, 401);

  const supabaseUrl = Deno.env.get("SUPABASE_URL");
  const serviceKey = Deno.env.get("SUPABASE_SERVICE_ROLE_KEY");
  if (!supabaseUrl || !serviceKey) return json({ error: "server_not_configured" }, 500);

  const admin = createClient(supabaseUrl, serviceKey, {
    auth: { autoRefreshToken: false, persistSession: false },
  });

  const token = authHeader.slice(7);
  const { data: { user }, error: userError } = await admin.auth.getUser(token);
  if (userError || !user) return json({ error: "unauthorized" }, 401);

  const uid = user.id;

  const campaignCleanup = await admin.from("notification_campaigns").delete().eq("created_by", uid);
  if (campaignCleanup.error) return json({ error: "account_cleanup_failed" }, 500);

  const guestWinnerCleanup = await admin
    .from("arena_matches")
    .update({ guest_id: null, winner_id: null })
    .or("host_id.eq." + uid + ",guest_id.eq." + uid + ",winner_id.eq." + uid);
  if (guestWinnerCleanup.error) return json({ error: "account_cleanup_failed" }, 500);

  const eventCleanup = await admin.from("arena_events").update({ actor_id: null }).eq("actor_id", uid);
  if (eventCleanup.error) return json({ error: "account_cleanup_failed" }, 500);

  const { error: deleteError } = await admin.auth.admin.deleteUser(uid);
  if (deleteError) return json({ error: "account_delete_failed" }, 500);

  return json({ deleted: true });
});