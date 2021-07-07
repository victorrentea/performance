package victor.training.performance.batch.core;

import org.springframework.data.jpa.repository.JpaRepository;
import victor.training.performance.batch.core.domain.Person;

public interface PersonRepo extends JpaRepository<Person, Long> {
}
