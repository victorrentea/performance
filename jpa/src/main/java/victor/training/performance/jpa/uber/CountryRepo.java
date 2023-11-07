package victor.training.performance.jpa.uber;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;

import javax.persistence.QueryHint;
import java.util.List;

public interface CountryRepo extends JpaRepository<Country, Long> {
   // Only caches returned IDs => MUST also have @Cache on Country @Entity
    @QueryHints({
        @QueryHint(name = "org.hibernate.cacheable", value = "true"),
        @QueryHint(name = "org.hibernate.cacheRegion", value = "allCountries")
    })
   List<Country> findAll();
}
