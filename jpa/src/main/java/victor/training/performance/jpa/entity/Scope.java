package victor.training.performance.jpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Scope { // old-school static 'referential' data -> prefer enum ScopeEnum
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
