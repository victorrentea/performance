package victor.training.performance.jpa;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Subselect;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Subselect("select\n" +
           "    parent0_.id        as id,\n" +
           "    parent0_.name        as name,\n" +
           "    STRING_AGG(c.NAME, ',') within group (order by c.name asc) children_names\n" +
           "from parent parent0_\n" +
           "    left outer join child c on parent0_.id = c.parent_id\n" +
           "group by parent0_.name")
public class ParentSearchSubselect {
   @Id
   private Long id;
   private String name;
   private String childrenNames;

}
