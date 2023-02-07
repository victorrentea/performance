package victor.training.performance.profile.showcase;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanApplicationRepo extends JpaRepository<LoanApplication, Long> {
}
