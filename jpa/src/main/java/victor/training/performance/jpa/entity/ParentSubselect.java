package victor.training.performance.jpa.entity;

import lombok.Getter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
@Immutable // hibernate will never update it
@Subselect("select\n" +
           "  P.ID,\n" +
           "  P.NAME,\n" +
           "  nvl(STRING_AGG(c.NAME, ',') within group (order by c.name asc), '') as children_names\n" +
           "from PARENT P " +
           "left join CHILD C on P.ID = C.PARENT_ID\n" +
           "group by p.ID, P.NAME\n")
@Getter
public class ParentSubselect {
   @Id
   private Long id;
   private String name;
   private String childrenNames;

}
