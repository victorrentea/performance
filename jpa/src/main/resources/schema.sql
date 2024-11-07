drop table PARENT_VIEW; -- drop the table created by spring.jpa.hibernate.ddl-auto=create

create or replace view PARENT_VIEW as
select p.ID,
       P.NAME,
       nvl(STRING_AGG(c.NAME, ',') within group
           (order by c.name asc), '') children_names
from PARENT P
         left join CHILD C on P.ID = C.PARENT_ID
group by p.ID, P.NAME;

insert into SCOPE values ( 100, 'GLOBAL');
insert into SCOPE values ( 101, 'REGION');
insert into SCOPE values ( 102, 'COUNTRY');
insert into SCOPE values ( 103, 'AREA');
insert into SCOPE values ( 104, 'CITY');