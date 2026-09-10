-- Keep onboarding writes behind one authenticated RPC.
drop function if exists public.complete_onboarding(text, text, text);

create or replace function public.complete_onboarding(
  p_username text,
  p_display_name text,
  p_avatar_id text
)
returns void
language plpgsql
security definer
set search_path = public, pg_temp
as $$
declare
  v_uid uuid := auth.uid();
  v_username text := lower(trim(coalesce(p_username, '')));
  v_display_name text := trim(coalesce(p_display_name, ''));
  v_avatar_id text := trim(coalesce(p_avatar_id, ''));
begin
  if v_uid is null then
    raise exception 'not_authenticated' using errcode = '42501';
  end if;

  if v_username !~ '^[a-z0-9_]{3,20}$' then
    raise exception 'invalid_username' using errcode = '22023';
  end if;

  if length(v_display_name) < 2 or length(v_display_name) > 40 then
    raise exception 'invalid_display_name' using errcode = '22023';
  end if;

  if v_avatar_id not in (
    'explorer','compass','mountain','flag','turkey','anchor','waves','water','forest',
    'park','cloud','sun','scholar','book','brain','science','light','bookmark','crown',
    'shield','rocket','sports','fire','bolt','face','cat','heart','diamond','home'
  ) then
    raise exception 'invalid_avatar' using errcode = '22023';
  end if;

  insert into public.profiles (
    id, username, display_name, avatar_id, onboarding_complete, updated_at
  )
  values (
    v_uid, v_username, v_display_name, v_avatar_id, true, now()
  )
  on conflict (id) do update
  set username = excluded.username,
      display_name = excluded.display_name,
      avatar_id = excluded.avatar_id,
      onboarding_complete = true,
      updated_at = now();

exception
  when unique_violation then
    raise exception 'username_taken' using errcode = '23505';
end;
$$;

revoke all on function public.complete_onboarding(text, text, text) from public;
revoke all on function public.complete_onboarding(text, text, text) from anon;
grant execute on function public.complete_onboarding(text, text, text) to authenticated;