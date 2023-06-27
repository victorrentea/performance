package victor.training.performance.jpa;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Table(name = "PARENT_SEARCH") // @Entity mapat pe VIEW. RUPE
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
//@NamedNativeQuery(query = "SELECT sql :param1")
//@NamedQuery(name = "ParentSearchView.q1", "jqpl")
public class ParentSearchView {
   @Id
   private Long id;
   private String name;
   private String childrenNames;

}
