package victor.training.performance.jpa.parent;

import lombok.Getter;
import org.hibernate.annotations.Immutable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Immutable
@Getter
public class ParentView {
   @Id
   private Long id;
   private String name;
   private String childrenNames;
}
