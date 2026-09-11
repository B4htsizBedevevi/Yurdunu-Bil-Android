-- Keep external account-deletion requests isolated and avoid unlimited duplicate pending rows.

create unique index if not exists account_deletion_requests_pending_email_uidx
on public.account_deletion_requests(lower(email))
where status = 'pending';

revoke all on table public.account_deletion_requests from anon, authenticated;
alter table public.account_deletion_requests enable row level security;

-- Security hardening for server-side Arena/account operations.
revoke execute on function public.advance_arena_round(uuid) from anon;
revoke execute on function public.finish_arena_match(uuid) from anon;
revoke execute on function public.is_admin() from anon;

grant execute on function public.advance_arena_round(uuid) to authenticated;
grant execute on function public.finish_arena_match(uuid) to authenticated;
grant execute on function public.is_admin() to authenticated;
