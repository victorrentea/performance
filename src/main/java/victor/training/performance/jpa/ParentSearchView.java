package victor.training.performance.jpa;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "PARENT_SEARCH")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Subselect("select p.ID, P.NAME,\n" +
           "       STRING_AGG(c.NAME, ',')\n" +
           "            within group (order by c.name asc) children_names\n" +
           "from PARENT P\n" +
           "         left join CHILD C on P.ID = C.PARENT_ID\n" +
           "group by p.ID, P.NAME")
@Immutable
public class ParentSearchView {
   @Id
   private Long id;
   private String name;
   private String childrenNames;

}
