package victor.training.performance.batch.sync.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CityRepo extends JpaRepository<City, Long> {
   Optional<City> findByName(String cityName);
//   @Query(value = "SELECT * FROM ", nativeQuery = true)
//   List<Object[]> runMyNative(int a, int b);
//   @Procedure("PROD_SUPP_PROC1")
//   List<Object[]> runProcedure(int a, int b);
}
