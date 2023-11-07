package victor.training.performance.jpa.uber;

import javax.persistence.Entity;
import javax.persistence.Id;

// Possible values: see ScopeEnum
@Entity
public class Scope {
   @Id
   private Long id;
   private String name;

   private Scope() {
   }

   public Scope(Long id, String name) {
      this.id = id;
      this.name = name;
   }
}
