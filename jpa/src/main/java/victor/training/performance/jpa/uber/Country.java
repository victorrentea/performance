package victor.training.performance.jpa.uber;

import lombok.Data;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Data
@Entity
//@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class Country {
   @Id
   private Long id;
   private String name;
   @ManyToOne(cascade = CascadeType.PERSIST) // for easier testing
   private CountryRegion region;
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
