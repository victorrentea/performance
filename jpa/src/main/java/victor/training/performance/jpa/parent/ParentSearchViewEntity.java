package victor.training.performance.jpa.parent;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PARENT_SEARCH_VIEW")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParentSearchViewEntity {
   @Id
   private Long id;
   private String name;
   private String childrenNames;

}
