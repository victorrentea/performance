package victor.training.performance.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UUIDEntityRepo extends JpaRepository<UUIDEntity, String> {
}
