-- every time
drop table tmp_admin_to_delete;

-- every time
create table tmp_admin_to_delete (
  select aid from HA_ADMIN a
  join SUBSCRIBERS_SERVICES ss on (ss.date_expire < '2014-01-01' and service_name = 'catchup' and a.subscriber_id = ss.subscriber_id)
  join SUBSCRIBERS s on (s.id = a.subscriber_id and s.type='ST')
  where a.aid not in (2,5,6,13,71,216,7108,18921,18922,18923) limit 1000
);

-- 1st time
create table HA_ADMIN_DELETED (
  select * from HA_ADMIN a where a.aid in (select aid from tmp_admin_to_delete)
);

-- 2nd+ times
insert into HA_ADMIN_DELETED (
  select * from HA_ADMIN a where a.aid in (select aid from tmp_admin_to_delete)
);

-- every time
delete from HA_ADMIN where aid in (
  select aid from tmp_admin_to_delete
);