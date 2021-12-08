drop table PARENT_SEARCH; -- only required because of spring.jpa.hibernate.ddl-auto=create

create or replace view parent_search as
select p.ID, P.NAME, STRING_AGG(c.NAME, ',') within group (order by c.name asc) children_names
from PARENT P
         Inner join CHILD C on P.ID = C.PARENT_ID
group by p.ID, P.NAME;