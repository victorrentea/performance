select parent0_.id as id1_3_, parent0_.name as name2_3_
from parent parent0_
;
-- lazy load:
select children0_.parent_id as parent_i3_0_0_,
       children0_.id        as id1_0_0_,
       children0_.id        as id1_0_1_,
       children0_.name      as name2_0_1_,
       children0_.parent_id as parent_i3_0_1_
from child children0_
where children0_.parent_id = 1;


-- findOne cu EAGER
select parent0_.id          as id1_3_0_,
       parent0_.name        as name2_3_0_,
       children1_.parent_id as parent_i3_0_1_,
       children1_.id        as id1_0_1_,
       children1_.id        as id1_0_2_,
       children1_.name      as name2_0_2_,
       children1_.parent_id as parent_i3_0_2_
from parent parent0_
         left outer join child children1_ on parent0_.id = children1_.parent_id
where parent0_.id = ?;


-- findOne cu EAGER pe 2 copii
select parent0_.id          as id1_3_0_,
       parent0_.name        as name2_3_0_,
       children1_.parent_id as parent_i3_0_1_,
       children1_.id        as id1_0_1_,
       children1_.id        as id1_0_2_,
       children1_.name      as name2_0_2_,
       children1_.parent_id as parent_i3_0_2_,
       phones2_.phones_id   as phones_i3_4_3_,
       phones2_.id          as id1_4_3_,
       phones2_.id          as id1_4_4_,
       phones2_.value       as value2_4_4_
from parent parent0_
         left outer join child children1_ on parent0_.id = children1_.parent_id
         left outer join phone phones2_ on parent0_.id = phones2_.phones_id
where parent0_.id = 1
;
------ LEFT JOIN FETCH

select parent0_.id          as id1_3_0_,
       children1_.id        as id1_0_1_,
       parent0_.name        as name2_3_0_,
       children1_.name      as name2_0_1_,
       children1_.parent_id as parent_i3_0_1_,
       children1_.parent_id as parent_i3_0_0__,
       children1_.id        as id1_0_0__
from parent parent0_
         left outer join child children1_ on parent0_.id = children1_.parent_id
;

select parent0_.id          as id1_3_0_,
       children1_.id        as id1_0_1_,
       parent0_.name        as name2_3_0_,
       phones2_.parent_id   as parent_i1_4_1__,
       phones2_.value       as value2_4_1__,
       children1_.name      as name2_0_1_,
       children1_.parent_id as parent_i3_0_1_,
       children1_.parent_id as parent_i3_0_0__,
       children1_.id        as id1_0_0__,
       phones2_.parent_id   as parent_i1_4_1__,
       phones2_.value       as value2_4_1__
from parent parent0_
         left outer join child children1_ on parent0_.id = children1_.parent_id
         left outer join parent_phones phones2_ on parent0_.id = phones2_.parent_id;
;
----- UBER ENTITY

select uberentity0_.id                   as id1_6_0_,
       uberentity0_.cnp                  as cnp2_6_0_,
       uberentity0_.created_by_id        as created_9_6_0_,
       uberentity0_.first_name           as first_na3_6_0_,
       uberentity0_.fiscal_country_id    as fiscal_10_6_0_,
       uberentity0_.iban_code            as iban_cod4_6_0_,
       uberentity0_.invoicing_country_id as invoici11_6_0_,
       uberentity0_.last_name            as last_nam5_6_0_,
       uberentity0_.name                 as name6_6_0_,
       uberentity0_.nationality_id       as nationa12_6_0_,
       uberentity0_.origin_country_id    as origin_13_6_0_,
       uberentity0_.passport_number      as passport7_6_0_,
       uberentity0_.scope_id             as scope_i14_6_0_,
       uberentity0_.ssn                  as ssn8_6_0_,
       user1_.id                         as id1_7_1_,
       user1_.name                       as name2_7_1_,
       country2_.id                      as id1_1_2_,
       country2_.continent               as continen2_1_2_,
       country2_.name                    as name3_1_2_,
       country2_.population              as populati4_1_2_,
       country2_.region                  as region5_1_2_,
       country3_.id                      as id1_1_3_,
       country3_.continent               as continen2_1_3_,
       country3_.name                    as name3_1_3_,
       country3_.population              as populati4_1_3_,
       country3_.region                  as region5_1_3_,
       country4_.id                      as id1_1_4_,
       country4_.continent               as continen2_1_4_,
       country4_.name                    as name3_1_4_,
       country4_.population              as populati4_1_4_,
       country4_.region                  as region5_1_4_,
       country5_.id                      as id1_1_5_,
       country5_.continent               as continen2_1_5_,
       country5_.name                    as name3_1_5_,
       country5_.population              as populati4_1_5_,
       country5_.region                  as region5_1_5_,
       scope6_.id                        as id1_5_6_,
       scope6_.name                      as name2_5_6_
from uber_entity uberentity0_
         left outer join user user1_ on uberentity0_.created_by_id = user1_.id
         left outer join country country2_ on uberentity0_.fiscal_country_id = country2_.id
         left outer join country country3_ on uberentity0_.invoicing_country_id = country3_.id
         left outer join country country4_ on uberentity0_.nationality_id = country4_.id
         left outer join country country5_ on uberentity0_.origin_country_id = country5_.id
         left outer join scope scope6_ on uberentity0_.scope_id = scope6_.id
where uberentity0_.id = ?
;

---- uber entity cu Long FK

select uberentity0_.id                   as id1_6_0_,
       uberentity0_.cnp                  as cnp2_6_0_,
       uberentity0_.created_by_id        as created14_6_0_,
       uberentity0_.first_name           as first_na3_6_0_,
       uberentity0_.fiscal_country_id    as fiscal_c4_6_0_,
       uberentity0_.iban_code            as iban_cod5_6_0_,
       uberentity0_.invoicing_country_id as invoicin6_6_0_,
       uberentity0_.last_name            as last_nam7_6_0_,
       uberentity0_.name                 as name8_6_0_,
       uberentity0_.nationality_id       as national9_6_0_,
       uberentity0_.origin_country_id    as origin_10_6_0_,
       uberentity0_.passport_number      as passpor11_6_0_,
       uberentity0_.scope_id             as scope_i12_6_0_,
       uberentity0_.ssn                  as ssn13_6_0_,
       user1_.id                         as id1_7_1_,
       user1_.name                       as name2_7_1_
from uber_entity uberentity0_
         left outer join user user1_ on uberentity0_.created_by_id = user1_.id
where uberentity0_.id = ?;

--- invoicing e manytoone(LAZY)
|
select uberentity0_.id                   as id1_6_0_,
       uberentity0_.cnp                  as cnp2_6_0_,
       uberentity0_.created_by_id        as created13_6_0_,
       uberentity0_.first_name           as first_na3_6_0_,
       uberentity0_.fiscal_country_id    as fiscal_c4_6_0_,
       uberentity0_.iban_code            as iban_cod5_6_0_,
       uberentity0_.invoicing_country_id as invoici14_6_0_,
       uberentity0_.last_name            as last_nam6_6_0_,
       uberentity0_.name                 as name7_6_0_,
       uberentity0_.nationality_id       as national8_6_0_,
       uberentity0_.origin_country_id    as origin_c9_6_0_,
       uberentity0_.passport_number      as passpor10_6_0_,
       uberentity0_.scope_id             as scope_i11_6_0_,
       uberentity0_.ssn                  as ssn12_6_0_,
       user1_.id                         as id1_7_1_,
       user1_.name                       as name2_7_1_
from uber_entity uberentity0_
         left outer join user user1_ on uberentity0_.created_by_id = user1_.id
where uberentity0_.id = ?
;
--- query dinamic initial care selecteaza entitati JPA intregi

select uberentity0_.id                   as id1_8_,
       uberentity0_.cnp                  as cnp2_8_,
       uberentity0_.created_by_id        as created14_8_,
       uberentity0_.cv                   as cv3_8_,
       uberentity0_.first_name           as first_na4_8_,
       uberentity0_.fiscal_country_id    as fiscal_c5_8_,
       uberentity0_.iban_code            as iban_cod6_8_,
       uberentity0_.invoicing_country_id as invoici15_8_,
       uberentity0_.last_name            as last_nam7_8_,
       uberentity0_.name                 as name8_8_,
       uberentity0_.nationality_id       as national9_8_,
       uberentity0_.origin_country_id    as origin_10_8_,
       uberentity0_.passport_number      as passpor11_8_,
       uberentity0_.scope_id             as scope_i12_8_,
       uberentity0_.ssn                  as ssn13_8_
from uber_entity uberentity0_
where 1 = 1;;

------------------------------

select uberentity0_.id as col_0_0_, uberentity0_.name as col_1_0_
from uber_entity uberentity0_
where 1 = 1;
----

select uberentity0_.id as col_0_0_,
       uberentity0_.name as col_1_0_,
       country1_.name as col_2_0_
from uber_entity uberentity0_
         inner join country country1_ ON uberentity0_.invoicing_country_id = country1_.id
where 1 = 1;

