package victor.training.performance.jpa.entity;

import lombok.Getter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
@Immutable // hibernate will never update it
@Subselect("""
  select 
    P.ID, 
    P.NAME,
    nvl(STRING_AGG(c.NAME, ',') within group (order by c.name asc), '') as children_names
  from PARENT_______P
  left join CHILD C on P.ID = C.PARENT_ID
  group by p.ID, P.NAME
  """) // errors in syntax are detected at runtime
@Getter
public class ParentSubselect {
   @Id
   private Long id;
   private String name;
   private String childrenNames;

}
