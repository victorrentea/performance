package victor.training.performance.jpa;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Cache(region = "countryCache", usage = CacheConcurrencyStrategy.READ_ONLY)
public class Country {
   @Id
   private Long id;
   private String name;
   private String region;
   private String iso2Code;
   private String iso3Code;
   private String continent;

   private Country() {
   }

   public Country(Long id, String name) {
      this.id = id;
      this.name = name;
   }
   public Country(String name) {
      this.name = name;
   }

   public Long getId() {
      return id;
   }

   public String getName() {
      return name;
   }
}
