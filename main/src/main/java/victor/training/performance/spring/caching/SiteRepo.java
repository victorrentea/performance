package victor.training.performance.spring.caching;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SiteRepo extends JpaRepository<Site, Long> {
}
