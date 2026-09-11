-- Arena release hardening
-- 1) Let the second queued player discover the live match after the queue rows
--    are consumed by the first successful matcher.
-- 2) Keep match-control RPCs callable by authenticated users only.

create or replace function public.try_match_arena(p_mode text default 'duel')
returns public.arena_matches
language plpgsql
security definer
set search_path to 'public', 'pg_temp'
set statement_timeout to '5s'
as $function$
declare
  v_me public.arena_queue;
  v_other public.arena_queue;
  v_match public.arena_matches;
  v_mode text;
  v_rounds int;
begin
  if auth.uid() is null then raise exception 'not_authenticated'; end if;

  v_mode := case p_mode
    when 'region-arena' then 'region'
    when 'master-arena' then 'master'
    else p_mode
  end;
  if v_mode not in ('duel','region','speed','master') then raise exception 'invalid_mode'; end if;

  -- A matched guest no longer has a queue row. Return the live match so the
  -- second device can discover it after the host creates it.
  select * into v_match
  from public.arena_matches m
  where (m.host_id=auth.uid() or m.guest_id=auth.uid())
    and m.mode=v_mode
    and m.status in ('waiting','ready','playing')
  order by m.updated_at desc
  limit 1;
  if found then return v_match; end if;

  select * into v_me from public.arena_queue where user_id=auth.uid();
  if not found then raise exception 'not_queued'; end if;
  if v_me.mode<>v_mode then raise exception 'queue_mode_mismatch'; end if;

  delete from public.arena_queue where expires_at<now();
  select * into v_other
  from public.arena_queue q
  where q.user_id<>auth.uid()
    and q.mode=v_mode
    and abs(q.rating-v_me.rating)<=250
  order by abs(q.rating-v_me.rating),q.queued_at
  limit 1 for update skip locked;
  if not found then return null; end if;

  v_rounds:=case v_mode when 'speed' then 12 when 'master' then 15 else 10 end;

  insert into public.arena_matches(mode,status,host_id,guest_id,room_code,total_rounds)
  values(v_mode,'waiting',auth.uid(),v_other.user_id,
         upper(substr(encode(gen_random_bytes(6),'hex'),1,8)),v_rounds)
  returning * into v_match;

  insert into public.arena_players(match_id,user_id,slot)
  values(v_match.id,auth.uid(),1),(v_match.id,v_other.user_id,2);

  insert into public.arena_match_questions(match_id,round_no,question_id,question_topic,question_payload)
  select v_match.id,
         row_number() over(order by random()),
         q.id,
         q.topic,
         q.question_payload - 'correctIndex' - 'correct_index' - 'explanation'
  from (
    select * from public.question_bank
    where active
      and (
        v_mode in ('duel','speed','master')
        or (v_mode='region' and topic in ('Bölgeler','Tarım','İklim ve Bitki Örtüsü'))
      )
    order by random()
    limit v_rounds
  ) q;

  if (select count(*) from public.arena_match_questions where match_id=v_match.id) < v_rounds then
    delete from public.arena_match_questions where match_id=v_match.id;
    delete from public.arena_players where match_id=v_match.id;
    delete from public.arena_matches where id=v_match.id;
    raise exception 'insufficient_shared_questions';
  end if;

  delete from public.arena_queue where user_id in(auth.uid(),v_other.user_id);
  return v_match;
end;
$function$;

revoke execute on function public.advance_arena_round(uuid) from public;
revoke execute on function public.finish_arena_match(uuid) from public;
grant execute on function public.advance_arena_round(uuid) to authenticated;
grant execute on function public.finish_arena_match(uuid) to authenticated;
