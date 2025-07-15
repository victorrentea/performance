package victor.training.spring.batch.core.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.stream.Stream;

public interface CityRepo extends JpaRepository<City, Long> {
//   @QueryHints({
//       @QueryHint(name = "org.hibernate.cacheable", value = "true"),
//       @QueryHint(name = "org.hibernate.cacheRegion", value = "allCities")
//   })
   Optional<City> findByName(String cityName);

   @Query("SELECT c FROM City c")
   Stream<City> streamAll();
}
