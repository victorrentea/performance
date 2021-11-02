package victor.training.performance.spring.caching;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class Site implements Serializable {
   @Id
   @GeneratedValue
   private Long id;
   private String name;

   public Site() {}
   public Site(String name) {
      this.name = name;
   }

   public String getName() {
      return name;
   }

   public Long getId() {
      return id;
   }
}
