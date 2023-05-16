package victor.training.spring.batch.core.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CityRepo extends JpaRepository<City, Long> {
//   @QueryHints({
//       @QueryHint(name = "org.hibernate.cacheable", value = "true"),
//       @QueryHint(name = "org.hibernate.cacheRegion", value = "allCities")
//   })
   Optional<City> findByName(String cityName);
}
