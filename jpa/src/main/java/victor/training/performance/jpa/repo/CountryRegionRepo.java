package victor.training.performance.jpa.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import victor.training.performance.jpa.entity.CountryRegion;

public interface CountryRegionRepo extends JpaRepository<CountryRegion, Long> {
}
