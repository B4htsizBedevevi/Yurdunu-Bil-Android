-- Keep notification RLS explicit without overlapping permissive SELECT policies.

drop policy if exists notification_automations_admin_write on public.notification_automations;
drop policy if exists notification_automations_admin_insert on public.notification_automations;
drop policy if exists notification_automations_admin_update on public.notification_automations;
drop policy if exists notification_automations_admin_delete on public.notification_automations;

create policy notification_automations_admin_insert
on public.notification_automations for insert to authenticated
with check (is_admin());

create policy notification_automations_admin_update
on public.notification_automations for update to authenticated
using (is_admin())
with check (is_admin());

create policy notification_automations_admin_delete
on public.notification_automations for delete to authenticated
using (is_admin());

drop policy if exists notification_templates_admin_read on public.notification_templates;
drop policy if exists notification_templates_admin_write on public.notification_templates;
drop policy if exists notification_templates_authenticated_read on public.notification_templates;

create policy notification_templates_authenticated_read
on public.notification_templates
for select to authenticated
using (active or is_admin());

create policy notification_templates_admin_insert
on public.notification_templates for insert to authenticated
with check (is_admin());

create policy notification_templates_admin_update
on public.notification_templates for update to authenticated
using (is_admin())
with check (is_admin());

create policy notification_templates_admin_delete
on public.notification_templates for delete to authenticated
using (is_admin());

create index if not exists notification_campaigns_created_by_idx
on public.notification_campaigns(created_by);
