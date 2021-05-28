package victor.training.performance.batch.sync;

import org.springframework.data.jpa.repository.JpaRepository;
import victor.training.performance.batch.sync.domain.Person;

public interface PersonRepo extends JpaRepository<Person, Long> {
}
