package victor.training.performance.jpa;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "PARENT_SEARCH")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParentSearchView {
   @Id
   private Long id;
   private String name;
   @Column(name = "CHILDREN_NAMES")
   private String childrenNames;

}
