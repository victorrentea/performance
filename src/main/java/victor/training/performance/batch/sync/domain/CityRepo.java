package victor.training.performance.batch.sync.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CityRepo extends JpaRepository<City, Long> {
   Optional<City> findByName(String cityName);
}
