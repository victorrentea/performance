package victor.training.performance.jpa.parent;

import lombok.Getter;
import org.hibernate.annotations.Immutable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Immutable
@Getter
public class ParentView {
   @Id
   private Long id;
   private String name;
   private String childrenNames;
}
