package victor.training.performance.jpa.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import victor.training.performance.jpa.entity.Scope;

public interface ScopeRepo extends JpaRepository<Scope, Long> {
}
