drop table PARENT_WITH_CHILDREN;

create or replace view PARENT_WITH_CHILDREN as
select
    parent0_.id        as id,
    parent0_.name        as name,
    STRING_AGG(c.NAME, ',') within group (order by c.name asc) children_names
from parent parent0_
    left outer join child c on parent0_.id = c.parent_id
group by parent0_.name;