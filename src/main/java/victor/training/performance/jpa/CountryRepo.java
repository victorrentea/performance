package victor.training.performance.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CountryRepo extends JpaRepository<Country, Long> {
   //@QueryHints(@QueryHint(name="org.hibernate.cacheable",value ="true"))
      // Only caches returned IDs => MUST-HAVE cache on Entity
   List<Country> findAll();
}
