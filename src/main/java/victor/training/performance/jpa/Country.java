package victor.training.performance.jpa;

import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Data
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@SequenceGenerator(name = "country_seq")
public class Country {
   @Id
   @GeneratedValue(generator = "country_seq")
   private Long id;
   private String name;
   private String region;
   private String iso2Code;
   private String iso3Code;
   private String continent;
//   @ManyToOne
//   private Region regio;;

   private Country() {
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
