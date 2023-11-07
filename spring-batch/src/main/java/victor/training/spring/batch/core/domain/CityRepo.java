package victor.training.spring.batch.core.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;

import javax.persistence.QueryHint;
import java.util.Optional;

public interface CityRepo extends JpaRepository<City, Long> {
   // 2nd level cache hibernate (global) nu per-tranzactie ca 1st level cache
//   @QueryHints({
//       @QueryHint(name = "org.hibernate.cacheable", value = "true"),
//       @QueryHint(name = "org.hibernate.cacheRegion", value = "allCities")
//   })
   Optional<City> findByName(String cityName);
}
