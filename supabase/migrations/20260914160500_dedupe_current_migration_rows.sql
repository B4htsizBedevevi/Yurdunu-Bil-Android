-- Keep the canonical current-information rows and disable duplicate seed rows.
-- The active catalogue must contain one exact content signature per question.
update public.question_bank
set active = false
where id in ('11107', '11108')
  and active = true;
