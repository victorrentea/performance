package victor.training.performance.jpa;

import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

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

   // protected allows Hibernate to subclass your Country to (a proxy)
   // and then put this proxy into the field of the Parent.
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
