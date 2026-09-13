-- Yurdunu Bil: question bank hardening
-- Mirrors the production Supabase change applied on 2026-09-13.

alter table public.question_bank
  add column if not exists difficulty text not null default 'medium'
    check (difficulty in ('easy','medium','hard')),
  add column if not exists is_current boolean not null default false,
  add column if not exists source_url text,
  add column if not exists source_note text,
  add column if not exists verified_at timestamptz;

create index if not exists question_bank_active_difficulty_idx
  on public.question_bank (active, difficulty);

create index if not exists question_bank_current_idx
  on public.question_bank (is_current, active);

drop policy if exists "question_bank_authenticated_read" on public.question_bank;
create policy "question_bank_authenticated_read"
  on public.question_bank
  for select
  to authenticated
  using (active = true);

-- Question content is managed as controlled seed/admin data; clients receive
-- active questions only and cannot mutate the bank directly.
