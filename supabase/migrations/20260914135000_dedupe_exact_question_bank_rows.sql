-- Keep one canonical server-side copy of exact duplicate question content.
-- These six rows were exact duplicates of an older canonical row.
update public.question_bank
set active=false
where id in ('geo-001','geo-002','geo-003','geo-005','geo-006','geo-008')
  and active=true;