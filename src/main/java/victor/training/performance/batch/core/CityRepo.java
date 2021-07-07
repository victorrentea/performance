package victor.training.performance.batch.core;

import org.springframework.data.jpa.repository.JpaRepository;
import victor.training.performance.batch.core.domain.City;

import java.util.Optional;

public interface CityRepo extends JpaRepository<City, Long> {
   Optional<City> findByName(String cityName);
}
