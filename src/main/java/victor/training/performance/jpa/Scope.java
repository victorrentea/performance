package victor.training.performance.jpa;

import javax.persistence.Entity;
import javax.persistence.Id;

// Possible values: see ScopeEnum
@Entity
public class Scope {
   @Id
   private Long id;
   private String name;

   protected Scope() {
   }

   public Scope(Long id, String name) {
      this.id = id;
      this.name = name;
   }
}
