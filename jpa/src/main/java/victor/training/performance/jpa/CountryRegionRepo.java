package victor.training.performance.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import java.util.Optional;

public interface CountryRegionRepo extends JpaRepository<CountryRegion, Long> {
//  @Lock(LockModeType.PESSIMISTIC_WRITE) ~> FOR UPDATE in the select = row lock
  @Override
  Optional<CountryRegion> findById(Long aLong);
}
