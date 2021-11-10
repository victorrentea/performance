package victor.training.performance.jpa;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.joining;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@Slf4j
@SpringBootTest
@Transactional
@Rollback(false)
//@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class NPlusOneTest {

   @Autowired
   EntityManager em;
   @Autowired
   ParentRepo repo;
   @Autowired
   ParentSearchViewRepo searchRepo;

   @BeforeEach
   void persistData() {
      repo.save(new Parent("Victor")
          .setAge(36)
          .addChild(new Child("Emma"))
          .addChild(new Child("Vlad"))
      );
      repo.save(new Parent("Trofim"));
      repo.save(new Parent("Peter")
          .setAge(41)
          .addChild(new Child("Maria"))
          .addChild(new Child("Paul"))
          .addChild(new Child("Stephan"))
      );
      TestTransaction.end();

      TestTransaction.start();
   }

   @Test
   void altLocInCode() {
      System.out.println(repo.findById(1L).get());
   }

   @Test
   void nPlusOneParentWithoutChildren() {
      List<Parent> parents = repo.loadAllWithChildren();
      for (Parent parent : parents) {
         String childrenStr = parent.getChildren().stream()
             .map(Child::getName)
             .collect(joining(","));
         if (parent.getChildren().size() == 0) {
            childrenStr = "BURLAC";
         }
         System.out.println(parent.getName() + ": " + childrenStr);
      }
   }
   @Test
   void nPlusOne_dinNative() {
      List<Object[]> parents = repo.loadDinNative();

      for (Object[] parent : parents) {
         System.out.println(Arrays.toString(parent));
      }


   }

   @Test
   void nPlusOne() {
      List<Parent> parents = repo.loadAllWithChildren();
      // TODO reduce the number of queries ran inside countChildren
      // TODO reduce the total number of queries ran to one
      // TODO how to paginate parents while prefetching children?

      int totalChildren = countChildren(parents);
      assertThat(totalChildren).isEqualTo(5);
   }

   // far away...
   private int countChildren(Collection<Parent> parents) {
      log.debug("Start counting children of {} parents: {}", parents.size(), parents);
      int total = 0;
      for (Parent parent : parents) {
         total += parent.getChildren().size();
      }
      log.debug("Done counting: {} children", total);
      return total;
   }


   @Test
   @Sql("/create-view.sql")
   public void searchOnView() {
      var parentViews = repo.findAll()
          .stream().map(p -> new ParentSearchView(
              p.getId(),
              p.getName(),
              p.getChildren().stream().map(Child::getName).sorted().collect(joining(","))
          ));
//		var parentViews = searchRepo.findAll();

      // TODO 1 restrict to first page (of 1 element)
      // TODO 2 search by parent age >= 40
      assertThat(parentViews)
          .extracting("name", "childrenNames")
          .containsExactlyInAnyOrder(
              tuple("Victor", "Emma,Vlad"),
              tuple("Peter", "Maria,Paul,Stephan"))
      ;
   }
}

interface ParentRepo extends JpaRepository<Parent, Long> {
   @Query("SELECT distinct p FROM Parent p LEFT JOIN FETCH p.children")
   List<Parent> loadAllWithChildren();

   @Query(value = "select parent0_.name        as name3_2_0_,\n" +
                  "           STRING_AGG(c.NAME, ',') within group (order by c.name asc) children_names\n" +
                  "    from parent parent0_\n" +
                  "             left outer join child c on parent0_.id = c.parent_id\n" +
                  "    group by parent0_.name", nativeQuery = true)
   List<Object[]> loadDinNative();
}


interface ParentSearchViewRepo extends JpaRepository<ParentSearchView, Long> {
}