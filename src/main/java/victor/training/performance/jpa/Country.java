package victor.training.performance.jpa;

import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Data
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class Country {
   @Id
   private Long id;
   private String name;
   @ManyToOne
   private CountryRegion region;
   private String iso2Code;
   private String iso3Code;
   private String continent;

   protected Country() {
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
