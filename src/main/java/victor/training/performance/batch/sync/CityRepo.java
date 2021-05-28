package victor.training.performance.batch.sync;

import org.springframework.data.jpa.repository.JpaRepository;
import victor.training.performance.batch.sync.domain.City;

import java.util.Optional;

public interface CityRepo extends JpaRepository<City, Long> {
   Optional<City> findByName(String cityName);
}
