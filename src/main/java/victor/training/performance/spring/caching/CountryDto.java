package victor.training.performance.spring.caching;

public class CountryDto {
   public Long id;
   public String name;

   public CountryDto(Site site) {
      id = site.getId();
      name = site.getName();
   }
}
