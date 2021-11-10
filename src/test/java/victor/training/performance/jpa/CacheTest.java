package victor.training.performance.jpa;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static victor.training.performance.util.PerformanceUtil.measureCall;

@Slf4j
@SpringBootTest
@Sql(statements = {
    "DELETE FROM COUNTRY",
    "INSERT INTO COUNTRY(ID, NAME) VALUES (1, 'Romania')",
    "INSERT INTO COUNTRY(ID, NAME) VALUES (2, 'Belgium')"
})
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class CacheTest {

   @Autowired
   CountryRepo countryRepo;

   @BeforeEach
   final void warmupHibernate() {
      countryRepo.findById(0L);
      log.info("-- Warmed up --");
   }

   @Test
   @Transactional
   void test1stLevelCache_transactionScoped() {
      int t1 = measureCall(() -> countryRepo.findById(1L).get());
      int t1bis = measureCall(() -> countryRepo.findById(1L).get());

      log.info("time={}, time again={}", t1, t1bis);

      assertThat(t1bis).isLessThanOrEqualTo(t1 / 2);
   }

   @Test
   void test2ndLevelCache_byId() {
      int t1 = measureCall(() -> System.out.println(countryRepo.findById(1L).get()));
      int t1bis = measureCall(() -> System.out.println(countryRepo.findById(1L).get()));

      log.info("time={}, time again={}", t1, t1bis);

      assertThat(t1bis).isLessThanOrEqualTo(t1 / 2);
   }

   @Test
   void test2ndLevelCache_findAll() {
      int t1 = measureCall(() -> System.out.println(countryRepo.findAll()));
      int t1bis = measureCall(() -> System.out.println(countryRepo.findAll()));

      log.info("time={}, time again={}", t1, t1bis);

      assertThat(t1bis).isLessThanOrEqualTo(t1 / 2);
   }
}
