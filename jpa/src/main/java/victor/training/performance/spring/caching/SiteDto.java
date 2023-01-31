package victor.training.performance.spring.caching;

public class SiteDto {
   public Long id;
   public String name;

   public SiteDto(Site site) {
      id = site.getId();
      name = site.getName();
   }
}
