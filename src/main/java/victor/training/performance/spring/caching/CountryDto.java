package victor.training.performance.spring.caching;

public class CountryDto {
   public Long id;
   public String name;

   public CountryDto(Country country) {
      id = country.getId();
      name = country.getName();
   }
}
