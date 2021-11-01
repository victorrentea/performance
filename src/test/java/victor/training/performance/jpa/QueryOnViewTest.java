package victor.training.performance.jpa;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@Slf4j
@SpringBootTest
@Transactional
@Rollback(false)
@Sql("/create-view.sql")
public class QueryOnViewTest {
   @Autowired
   private ParentSearchViewRepo searchRepo;
   @Autowired
   private EntityManager em;

   @BeforeEach
   public void persistData() {
      em.persist(new Parent("Victor")
          .addChild(new Child("Emma"))
          .addChild(new Child("Vlad"))
      );
      TestTransaction.end();
      TestTransaction.start();
   }

   @Test
   public void entityOnView() {
      searchRepo.findAll().forEach(System.out::println);
      Assertions.assertThat(searchRepo.findAll())
          .anyMatch(ps -> ps.getChildrenNames().contains("Vlad,Emma"));
   }
}

