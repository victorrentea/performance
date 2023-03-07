package victor.training.performance.jpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.jpa.Parent;
import victor.training.performance.jpa.ParentRepo;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;
import static org.springframework.data.domain.Sort.Direction.ASC;


@Slf4j
@Profile("!test")
@RestController
@RequiredArgsConstructor
public class ParentSearchController {
   private final ParentRepo repo;

   @GetMapping("parent/search")
   @Transactional
   public Page<Parent> searchPaginated() {
      PageRequest pageRequest = PageRequest.of(0, 20, ASC, "name");
      Page<Parent> parentPage = repo.findByNameLike("%ar%", pageRequest);
      log.info("Returning data");
      return parentPage;
   }

   @GetMapping("parent/search-driving")
   @Transactional
   public Page<Parent> drivingQuery() {
      PageRequest pageRequest = PageRequest.of(0, 20, ASC, "name");
      Page<Long> parentIdsPage = repo.findIdsByNameLike("%ar%", pageRequest);
      List<Long> parentIds = parentIdsPage.getContent();
      log.info("Matched parents: {}", parentIds);
      Map<Long, Parent> parentDataById = repo.findParentsWithChildren(parentIds)
              .stream().collect(Collectors.toMap(Parent::getId, identity()));
      return parentIdsPage.map(parentDataById::get);
   }

   // TODO? #3) Break up with Hibernate and extract the hierarchical objects manually from a resultset
   //  (grouped by parentId)



   //<editor-fold desc="initial data">
   private final JdbcTemplate jdbc;
   @EventListener(ApplicationStartedEvent.class)
   public void insertInitialData() {
      log.warn("INSERTING data ...");
      jdbc.update("INSERT INTO COUNTRY(ID, NAME) SELECT X, 'Country ' || X  FROM SYSTEM_RANGE(1, 20)");
      jdbc.update("INSERT INTO PARENT(ID, NAME, COUNTRY_ID) SELECT X, 'Parent' || X, 1 + MOD(X,20)  FROM SYSTEM_RANGE(1, 1000)");

      // jdbc.update("INSERT INTO PARENT(ID, NAME) SELECT X, 'Parent ' || X FROM SYSTEM_RANGE(1, 1000)");
      jdbc.update("INSERT INTO CHILD(ID, NAME, PARENT_ID) SELECT X, 'Child' || X || '-1',X FROM SYSTEM_RANGE(1, 1000)");
      jdbc.update("INSERT INTO CHILD(ID, NAME, PARENT_ID) SELECT X + 1000, 'Child' || X || '-2', X FROM SYSTEM_RANGE(1, 1000)");
      log.info("DONE");
   }
   //</editor-fold>
}

