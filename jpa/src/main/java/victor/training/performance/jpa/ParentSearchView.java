package victor.training.performance.jpa;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "PARENT_SEARCH") // @Entity mapat pe VIEW. RUPE
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParentSearchView {
   @Id
   private Long id;
   private String name;
   private String childrenNames;

}
