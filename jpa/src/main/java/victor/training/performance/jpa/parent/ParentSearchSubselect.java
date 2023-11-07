package victor.training.performance.jpa.parent;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Immutable // hibernate will never update it
@Subselect("""
              select 
                P.ID as id, 
                P.NAME as name,
                nvl(STRING_AGG(c.NAME, ',') within group (order by c.name asc), '') as children_names
              from PARENT P
              left join CHILD C on P.ID = C.PARENT_ID
              group by p.ID, P.NAME
              """)
@Getter
public class ParentSearchSubselect {
   @Id
   private Long id;
   private String name;
   private String childrenNames;

}
