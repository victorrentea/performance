package victor.training.performance.jpa;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;
import static victor.training.performance.util.PerformanceUtil.measureCall;

@Slf4j
@SpringBootTest
@Sql(statements = {"DELETE FROM COUNTRY",
    "INSERT INTO COUNTRY(ID, NAME) VALUES (1, 'Romania')",
    "INSERT INTO COUNTRY(ID, NAME) VALUES (2, 'Belgium')"
})
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class CacheTest {

   @Autowired
   CountryRepo countryRepo;

//   @BeforeEach
//   final void before() {
//      countryRepo.save(new Country("Romania"));
//   }

   @Test
   void test2ndLevelCacheById() {
      int t1 = measureCall(() -> countryRepo.findById(1L).get());
      int t1bis = measureCall(() -> countryRepo.findById(1L).get());

      int t2 = measureCall(() -> countryRepo.findById(2L).get());
      int t2bis = measureCall(() -> countryRepo.findById(2L).get());

      log.info("t1={}, t1bis={}, t2={}, t2bis={}", t1, t1bis, t2, t2bis);

      assertThat(t1bis).isLessThanOrEqualTo(t1 /2 );
      assertThat(t2bis).isLessThanOrEqualTo(t2 /2 );
   }

   @Test
   void test2ndLevelCacheAll() {
      log.debug("Query1");
      int t1 = measureCall(() -> countryRepo.findAll());
      log.debug("Query2");
      int t1bis = measureCall(() -> countryRepo.findAll());
      log.debug("END");

      System.out.println("Countries: " + countryRepo.findAll());

      log.info("t1={}, t1bis={}", t1, t1bis);

      assertThat(t1bis).isLessThanOrEqualTo(t1 /2 );
   }
}
