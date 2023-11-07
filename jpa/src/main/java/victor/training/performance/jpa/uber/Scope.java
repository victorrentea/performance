package victor.training.performance.jpa.uber;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Scope { // old-school static 'referential' data - prefer enum ScopeEnum
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
