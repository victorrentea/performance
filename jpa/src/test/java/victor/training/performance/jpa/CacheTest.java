package victor.training.performance.jpa;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Cache;
import org.hibernate.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import victor.training.performance.jpa.entity.Country;
import victor.training.performance.util.PerformanceUtil;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

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
   @Autowired
   EntityManager entityManager;

   @BeforeEach
   final void warmupHibernate() {
      countryRepo.findById(0L);
      log.info("-- Warmed up --");
   }

   private Session session() {
      return (Session) entityManager.getDelegate();
   }

   @Autowired
   CacheManager cacheManager;

   @Test
   @Transactional
   void test1stLevelCache_transactionScoped() {
      int t1 = PerformanceUtil.measureCall(() -> countryRepo.findById(1L).get());
      int t1bis = PerformanceUtil.measureCall(() -> countryRepo.findById(1L).get());

      log.info("time={}, time again={}", t1, t1bis);

      assertThat(t1bis).isLessThanOrEqualTo(t1 / 2);
   }

   @Test
   void test2ndLevelCache_byId() {
      assertThat(session().getSessionFactory().getCache().containsEntity(Country.class, 1L)).isFalse();

      int t1 = PerformanceUtil.measureCall(() -> System.out.println(countryRepo.findById(1L).get()));
      int t1bis = PerformanceUtil.measureCall(() -> System.out.println(countryRepo.findById(1L).get()));

      log.info("time={}, time again={}", t1, t1bis);

      assertThat(session().getSessionFactory().getCache().containsEntity(Country.class, 1L)).isTrue();
      long hitCount = session().getSessionFactory().getStatistics()
          .getDomainDataRegionStatistics("victor.training.performance.jpa.entity.Country")
          .getHitCount();
      assertThat(hitCount).isEqualTo(1);
   }

   @Test
   void test2ndLevelCache_findAll() {
      assertThat(session().getSessionFactory().getCache().containsQuery("allCountries")).isFalse();

      int t1 = PerformanceUtil.measureCall(() -> System.out.println(countryRepo.findAll()));
      int t1bis = PerformanceUtil.measureCall(() -> System.out.println(countryRepo.findAll()));

      log.info("time={}, time again={}", t1, t1bis);

      assertThat(session().getSessionFactory().getCache().containsQuery("allCountries")).isTrue();
   }

   @Test
   void test2ndLevelCache_maxSize() {
      Cache cache = session().getSessionFactory().getCache();

      System.out.println(countryRepo.findById(1L).get());
      assertThat(cache.containsEntity(Country.class, 1L)).isTrue();
      System.out.println(countryRepo.findById(2L).get());
      assertThat(cache.containsEntity(Country.class, 1L))
          .describedAs("Second cache load should evict the first one (see ehcache.xml)")
          .isFalse();
      assertThat(cache.containsEntity(Country.class, 2L)).isTrue();

      System.out.println(cacheManager.getCacheNames());
      System.out.println(cacheManager.getCache("victor.training.performance.jpa.entity.Country").getNativeCache());
   }
}
