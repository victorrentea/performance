package victor.training.performance.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import victor.training.performance.jpa.Scope;

import java.util.Optional;

public interface ScopeRepo extends JpaRepository<Scope, Long> {
}
