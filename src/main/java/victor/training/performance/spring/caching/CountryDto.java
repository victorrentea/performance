package victor.training.performance.spring.caching;

import java.io.Serializable;

public class CountryDto implements Serializable {
   public Long id;
   public String name;

   public CountryDto(Site site) {
      id = site.getId();
      name = site.getName();
   }
}
