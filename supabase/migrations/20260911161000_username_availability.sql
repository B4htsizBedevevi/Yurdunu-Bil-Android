-- Friendly username availability check used by onboarding.
create or replace function public.username_available(p_username text)
returns boolean
language sql
stable
security definer
set search_path = public, pg_temp
as $$
  select
    lower(trim(coalesce(p_username, ''))) ~ '^[a-z0-9_]{3,20}$'
    and not exists (
      select 1
      from public.profiles p
      where lower(p.username) = lower(trim(coalesce(p_username, '')))
        and p.id <> coalesce(
          auth.uid(),
          '00000000-0000-0000-0000-000000000000'::uuid
        )
    );
$$;

revoke all on function public.username_available(text) from public;
revoke all on function public.username_available(text) from anon;
grant execute on function public.username_available(text) to authenticated;
