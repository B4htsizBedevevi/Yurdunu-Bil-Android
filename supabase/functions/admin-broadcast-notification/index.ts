import "jsr:@supabase/functions-js/edge-runtime.d.ts";
import { createClient } from "npm:@supabase/supabase-js@2";
import { importPKCS8, SignJWT } from "npm:jose@6";

const supabaseUrl = Deno.env.get("SUPABASE_URL")!;
const serviceRoleKey = Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!;
const firebaseRaw = Deno.env.get("FIREBASE_SERVICE_ACCOUNT_JSON");
const admin = createClient(supabaseUrl, serviceRoleKey, { auth: { persistSession: false } });

const json = (data: unknown, status = 200) =>
  new Response(JSON.stringify(data), { status, headers: { "content-type": "application/json" } });

async function firebaseAccessToken(sa: Record<string, string>) {
  const key = await importPKCS8(sa.private_key, "RS256");
  const now = Math.floor(Date.now() / 1000);
  const jwt = await new SignJWT({ scope: "https://www.googleapis.com/auth/firebase.messaging" })
    .setProtectedHeader({ alg: "RS256", typ: "JWT" })
    .setIssuer(sa.client_email).setSubject(sa.client_email)
    .setAudience("https://oauth2.googleapis.com/token")
    .setIssuedAt(now).setExpirationTime(now + 3600).sign(key);
  const r = await fetch("https://oauth2.googleapis.com/token", {
    method: "POST",
    headers: { "content-type": "application/x-www-form-urlencoded" },
    body: new URLSearchParams({ grant_type: "urn:ietf:params:oauth:grant-type:jwt-bearer", assertion: jwt })
  });
  if (!r.ok) throw new Error("oauth_token_failed");
  return (await r.json()).access_token as string;
}

Deno.serve(async (req) => {
  if (req.method !== "POST") return json({ error: "method_not_allowed" }, 405);
  const auth = req.headers.get("authorization") ?? "";
  if (!auth.startsWith("Bearer ")) return json({ error: "unauthorized" }, 401);

  const { data: authUser, error: authError } = await admin.auth.getUser(auth.slice(7));
  if (authError || !authUser.user) return json({ error: "unauthorized" }, 401);

  const { data: profile } = await admin.from("profiles").select("role").eq("id", authUser.user.id).maybeSingle();
  if (profile?.role !== "admin") return json({ error: "forbidden" }, 403);

  const payload = await req.json().catch(() => null) as { title?: string; body?: string; type?: string; data?: Record<string,string> } | null;
  const title = payload?.title?.trim();
  const body = payload?.body?.trim();
  if (!title || !body) return json({ error: "invalid_payload" }, 400);

  const { data: campaign, error: campaignError } = await admin
    .from("notification_campaigns")
    .insert({ title: title.slice(0,80), body: body.slice(0,220), type: payload?.type ?? "announcement", audience: "all", status: "sending", created_by: authUser.user.id })
    .select("id").single();
  if (campaignError) return json({ error: "campaign_create_failed", detail: campaignError.message }, 500);

  const { data: users, error: userError } = await admin.from("profiles").select("id");
  if (userError) return json({ error: "user_lookup_failed" }, 500);

  const rows = (users ?? []).map((u: {id:string}) => ({
    user_id: u.id, type: payload?.type ?? "announcement", title: title.slice(0,80),
    body: body.slice(0,220), data: payload?.data ?? {}
  }));

  if (rows.length) {
    const { error: notificationError } = await admin.from("notifications").insert(rows);
    if (notificationError) {
      await admin.from("notification_campaigns").update({ status: "failed" }).eq("id", campaign.id);
      return json({ error: "notification_insert_failed" }, 500);
    }
  }

  let sent = 0, failures = 0, deactivated = 0, pushConfigured = Boolean(firebaseRaw);
  if (firebaseRaw) {
    try {
      const sa = JSON.parse(firebaseRaw);
      const access = await firebaseAccessToken(sa);
      const { data: devices } = await admin.from("notification_devices").select("id,token").eq("active", true);
      const invalid: number[] = [];
      for (const d of devices ?? []) {
        try {
          const r = await fetch(`https://fcm.googleapis.com/v1/projects/${sa.project_id}/messages:send`, {
            method: "POST",
            headers: { "content-type": "application/json", authorization: `Bearer ${access}` },
            body: JSON.stringify({ message: { token: d.token, notification: { title: title.slice(0,80), body: body.slice(0,220) }, data: payload?.data ?? {}, android: { priority: "HIGH", notification: { channel_id: "announcements", sound: "default" } } } })
          });
          if (r.ok) sent++; else if (r.status === 400 || r.status === 404) invalid.push(d.id); else failures++;
        } catch { failures++; }
      }
      if (invalid.length) {
        await admin.from("notification_devices").update({ active: false, updated_at: new Date().toISOString() }).in("id", invalid);
        deactivated = invalid.length;
      }
    } catch {
      pushConfigured = false;
    }
  }

  await admin.from("notification_campaigns").update({ status: pushConfigured ? "sent" : "in_app_sent" }).eq("id", campaign.id);
  return json({ campaign_id: campaign.id, in_app_sent: rows.length, push_sent: sent, push_failures: failures, deactivated, push_configured: pushConfigured });
});
