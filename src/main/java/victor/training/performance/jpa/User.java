package victor.training.performance.jpa;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class User {
   @Id
   private Long id;
   private String name;

   private User() {
   }

   public User(Long id, String name) {
      this.id = id;
      this.name = name;
   }
}
