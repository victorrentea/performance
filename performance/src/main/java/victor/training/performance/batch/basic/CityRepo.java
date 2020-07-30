package victor.training.performance.batch.basic;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepo extends JpaRepository<City, Long> {
   City findByName(String name);
}
