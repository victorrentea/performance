drop table PARENT_SEARCH; -- only required because of spring.jpa.hibernate.ddl-auto=create

-- if the view syntax is incorrect the DB rejects it
create or replace view parent_search as
select p.ID, P.NAME, nvl(STRING_AGG(c.NAME, ',') within group (order by c.name asc), '') children_names
from PARENT P
         left join CHILD C on P.ID = C.PARENT_ID
group by p.ID, P.NAME;