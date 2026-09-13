-- Arena status contract hardening.
-- public.arena_matches accepts 'active'; all match lifecycle RPCs use 'active'.
create or replace function public.mark_arena_ready(p_match_id uuid)
returns void
language plpgsql
security definer
set search_path to 'public','pg_temp'
set statement_timeout to '5s'
as $function$
declare v_count int; v_seconds int; v_mode text;
begin
  if auth.uid() is null then raise exception 'not_authenticated'; end if;
  update public.arena_players set ready=true, connected=true, updated_at=now()
  where match_id=p_match_id and user_id=auth.uid();
  if not found then raise exception 'not_match_member'; end if;

  select count(*) into v_count from public.arena_players where match_id=p_match_id and ready=true;
  select mode into v_mode from public.arena_matches where id=p_match_id;
  v_seconds:=case v_mode when 'speed' then 9 when 'master' then 18 else 15 end;

  if v_count=2 then
    update public.arena_matches
    set status='active', started_at=coalesce(started_at,now()), current_round=1, updated_at=now()
    where id=p_match_id and status in ('waiting','ready');
    update public.arena_match_questions
    set answer_deadline_at=now()+make_interval(secs=>v_seconds)
    where match_id=p_match_id and round_no=1;
  else
    update public.arena_matches set status='ready',updated_at=now()
    where id=p_match_id and status='waiting';
  end if;
end;
$function$;

create or replace function public.try_match_arena(p_mode text default 'duel')
returns public.arena_matches
language plpgsql
security definer
set search_path to 'public','pg_temp'
set statement_timeout to '5s'
as $function$
declare v_me public.arena_queue; v_other public.arena_queue; v_match public.arena_matches;
v_mode text; v_rounds int;
begin
  if auth.uid() is null then raise exception 'not_authenticated'; end if;
  v_mode:=case p_mode when 'region-arena' then 'region' when 'master-arena' then 'master' else p_mode end;
  if v_mode not in ('duel','region','speed','master') then raise exception 'invalid_mode'; end if;

  select * into v_match from public.arena_matches m
  where (m.host_id=auth.uid() or m.guest_id=auth.uid())
    and m.mode=v_mode and m.status in ('waiting','ready','active')
  order by m.updated_at desc limit 1;
  if found then return v_match; end if;

  select * into v_me from public.arena_queue where user_id=auth.uid();
  if not found then raise exception 'not_queued'; end if;
  if v_me.mode<>v_mode then raise exception 'queue_mode_mismatch'; end if;

  delete from public.arena_queue where expires_at<now();
  select * into v_other from public.arena_queue q
  where q.user_id<>auth.uid() and q.mode=v_mode and abs(q.rating-v_me.rating)<=250
  order by abs(q.rating-v_me.rating),q.queued_at limit 1 for update skip locked;
  if not found then return null; end if;

  v_rounds:=case v_mode when 'speed' then 12 when 'master' then 15 else 10 end;
  insert into public.arena_matches(mode,status,host_id,guest_id,room_code,total_rounds)
  values(v_mode,'waiting',auth.uid(),v_other.user_id,upper(substr(encode(gen_random_bytes(6),'hex'),1,8)),v_rounds)
  returning * into v_match;

  insert into public.arena_players(match_id,user_id,slot)
  values(v_match.id,auth.uid(),1),(v_match.id,v_other.user_id,2);

  insert into public.arena_match_questions(match_id,round_no,question_id,question_topic,question_payload)
  select v_match.id,row_number() over(order by random()),q.id,q.topic,
         q.question_payload - 'correctIndex' - 'correct_index' - 'explanation'
  from (
    select * from public.question_bank
    where active
      and (v_mode in ('duel','speed','master')
       or (v_mode='region' and topic in ('Bölgeler','Tarım','İklim ve Bitki Örtüsü')))
    order by random() limit v_rounds
  ) q;

  if (select count(*) from public.arena_match_questions where match_id=v_match.id)<v_rounds then
    delete from public.arena_match_questions where match_id=v_match.id;
    delete from public.arena_players where match_id=v_match.id;
    delete from public.arena_matches where id=v_match.id;
    raise exception 'insufficient_shared_questions';
  end if;

  delete from public.arena_queue where user_id in(auth.uid(),v_other.user_id);
  return v_match;
end;
$function$;

create or replace function public.advance_arena_round(p_match_id uuid)
returns void
language plpgsql
security definer
set search_path to 'public','pg_temp'
set statement_timeout to '5s'
as $function$
declare m public.arena_matches; q public.arena_match_questions; v_seconds int;
begin
  if auth.uid() is null then raise exception 'not_authenticated'; end if;
  select * into m from public.arena_matches where id=p_match_id
    and(host_id=auth.uid() or guest_id=auth.uid()) for update;
  if not found then raise exception 'not_match_member'; end if;
  if m.status<>'active' then return; end if;

  select * into q from public.arena_match_questions where match_id=p_match_id and round_no=m.current_round;
  if q.answer_deadline_at is not null and now()<q.answer_deadline_at
    and(select count(*) from public.arena_answers where match_id=p_match_id and round_no=m.current_round)<2 then return; end if;

  if m.current_round>=m.total_rounds then perform public.finish_arena_match(p_match_id); return; end if;

  v_seconds:=case m.mode when 'speed' then 9 when 'master' then 18 else 15 end;
  update public.arena_matches set current_round=current_round+1,updated_at=now()
  where id=p_match_id and status='active';
  update public.arena_match_questions
  set answer_deadline_at=now()+make_interval(secs=>v_seconds)
  where match_id=p_match_id and round_no=m.current_round+1;
end;
$function$;

create or replace function public.record_arena_answer(
  p_match_id uuid,p_round_no integer,p_selected_index integer,p_response_ms integer default null
)
returns jsonb
language plpgsql
security definer
set search_path to 'public','pg_temp'
set statement_timeout to '5s'
as $function$
declare v_question public.arena_match_questions; v_match public.arena_matches;
v_correct boolean; v_score_delta int; v_answer_count int;
begin
  if auth.uid() is null then raise exception 'not_authenticated'; end if;
  if p_match_id is null or p_round_no<=0 or p_selected_index is null or p_selected_index<0 or p_selected_index>4
    then raise exception 'invalid_answer'; end if;
  if p_response_ms is not null and(p_response_ms<0 or p_response_ms>600000)
    then raise exception 'invalid_response_time'; end if;

  select * into v_match from public.arena_matches
  where id=p_match_id and(host_id=auth.uid() or guest_id=auth.uid());
  if not found then raise exception 'not_match_member'; end if;
  if v_match.status not in('ready','active') then raise exception 'match_not_active'; end if;
  if p_round_no<>v_match.current_round then raise exception 'invalid_round'; end if;
  if p_round_no>v_match.total_rounds then raise exception 'round_out_of_range'; end if;

  select * into v_question from public.arena_match_questions where match_id=p_match_id and round_no=p_round_no;
  if not found then raise exception 'question_not_found'; end if;
  if v_question.answer_deadline_at is not null and now()>v_question.answer_deadline_at
    then raise exception 'answer_window_expired'; end if;
  if exists(select 1 from public.arena_answers where match_id=p_match_id and round_no=p_round_no and user_id=auth.uid())
    then raise exception 'duplicate_answer'; end if;

  v_correct:=coalesce((v_question.question_payload->>'correctIndex')::integer=p_selected_index,false);
  v_score_delta:=case when v_correct then greatest(1,1000-coalesce(p_response_ms,1000)/20) else 0 end;

  insert into public.arena_answers(match_id,round_no,user_id,selected_index,is_correct,response_ms)
  values(p_match_id,p_round_no,auth.uid(),p_selected_index,v_correct,p_response_ms);

  update public.arena_players
  set score=score+v_score_delta,
      correct_answers=correct_answers+case when v_correct then 1 else 0 end,
      updated_at=now()
  where match_id=p_match_id and user_id=auth.uid();

  select count(*) into v_answer_count from public.arena_answers where match_id=p_match_id and round_no=p_round_no;
  if v_answer_count=2 then
    if p_round_no=v_match.total_rounds then
      perform public.finish_arena_match(p_match_id);
    else
      update public.arena_matches set current_round=p_round_no+1,status='active',updated_at=now()
      where id=p_match_id and status='active';
      update public.arena_match_questions
      set answer_deadline_at=now()+make_interval(secs=>case v_match.mode when 'speed' then 9 when 'master' then 18 else 15 end)
      where match_id=p_match_id and round_no=p_round_no+1;
    end if;
  end if;

  return jsonb_build_object('correct',v_correct,'score_delta',v_score_delta,'round',p_round_no);
end;
$function$;

revoke execute on function public.mark_arena_ready(uuid) from public;
revoke execute on function public.try_match_arena(text) from public;
revoke execute on function public.advance_arena_round(uuid) from public;
revoke execute on function public.record_arena_answer(uuid,integer,integer,integer) from public;
grant execute on function public.mark_arena_ready(uuid) to authenticated;
grant execute on function public.try_match_arena(text) to authenticated;
grant execute on function public.advance_arena_round(uuid) to authenticated;
grant execute on function public.record_arena_answer(uuid,integer,integer,integer) to authenticated;
