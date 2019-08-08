
parents = SELECT * FROM Parent;

for (p in parents) {
    SELECT * FROM CHILD WHERE CHILD.PARENT_ID = ${p.id}
}

// JPA style:
for (Parent p : parentRepo.findAll()) { // 1 QUERY care da (eg) 1000 randuri
    for (Child c : p.getChildren()) { // Lazy LOAD: 1000 query-uri fara sa atingi vreun REPO (asta e horror)
        sysout(c);
    }
}

--- alta idee

1 SELECT p.*, c.*, c2. from PARENT p
    JOIN child c on c.parent_id=p.id
    JOIN child2 c2 on c2.parent_id=p.id -- NU : explozie cardinala!!
    JOIN child3 c3 on c3.parent_id=p.id  -- NU : explozie cardinala!!

p.id,   p.name, c.id, c.name
1       a,b,c,d,e,f       1       x
1       a,b,c,d,e,f       2       x1
1       a,b,c,d,e,f       3       x2
1       a,b,c,d,e,f       4       x34
1       a,b,c,d,e,f       5       x5
1       a,b,c,d,e,f       6       x6

2       a1,b1,c1,d1,e1,f1 10      xa223


--------------------
ASA DA: x

1 parents = SELECT * FROM Parent;
2 SELECT * FROM CHILD WHERE
      CHILD.PARENT_ID IN (1,23,52,63,3123, .... 1000) OR
      CHILD.PARENT_ID IN (1001, 1002...) OR s.a.m.d.

NU TE DU IN FOR pe un sistem extern / scump !