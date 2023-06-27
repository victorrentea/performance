drop table PARENT_SEARCH; -- only required because of spring.jpa.hibernate.ddl-auto=create

create or replace view parent_search as
    -- ai liber la orice functii agregate, specifice DB tau⚠️, /*+ hinturi, FROM (SELECT ...) , UNION
    -- considerati ideea asta pentru query-ul de homepage/ alea foarte hot
    -- idee: pornesti in prod JFR 1 saptamana.
    -- te uiti in rezultat pe udne au stat threadurile HTTP blocate. Daca vezi vreun repo.metoda -> investighezi -> tunezi

    -- daca folosesti SQL non-ISO standard (eg CONNECT BY in Oracle, functii specifice lui PG)
    -- => query-ul tau nu va putea fi rulat pe o DB in mem (H2) ci doar pe un Ora-XE in Docker pe CI
select p.ID, P.NAME, nvl(STRING_AGG(c.NAME, ',') within group (order by c.name asc), '') children_names
from PARENT P
    left join CHILD C on P.ID = C.PARENT_ID
group by p.ID, P.NAME;