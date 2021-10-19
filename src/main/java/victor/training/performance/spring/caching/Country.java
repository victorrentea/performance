package victor.training.performance.spring.caching;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Country {
   @Id
   @GeneratedValue
   private Long id;
   private String name;

   public Country() {}
   public Country(String name) {
      this.name = name;
   }

   public String getName() {
      return name;
   }

   public Long getId() {
      return id;
   }
}
