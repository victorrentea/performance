package victor.training.jpa.perf;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@Transactional
@Rollback(false)
public class QueryOnView {
   @Autowired
   private ParentSearchRepo searchRepo;
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
      assertThat(searchRepo.findAll())
          .anyMatch(ps -> ps.getChildrenNames().contains("Vlad,Emma"));
   }
}


interface ParentSearchRepo extends JpaRepository<ParentSearchView, Long> {
}