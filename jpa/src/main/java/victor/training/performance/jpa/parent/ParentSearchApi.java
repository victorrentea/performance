package victor.training.performance.jpa.parent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;
import static org.springframework.data.domain.Sort.Direction.ASC;


@Slf4j
@Profile("!test")
@RestController
@RequiredArgsConstructor
public class ParentSearchApi {
   private final ParentRepo repo;

   public static class ParentDto {
//       TODO children csv.
   }

   @GetMapping("parent/search")
   @Transactional
   public Page<Parent> searchPaginated(
           @RequestParam(defaultValue = "ar") String q,
           @RequestParam(defaultValue = "0") int pageIndex,
           @RequestParam(defaultValue = "20") int pageSize,
           @RequestParam(defaultValue = "name") String order,
           @RequestParam(defaultValue = "ASC") String dir
           ) {
//      PageRequest pageRequest = PageRequest.of(0, 20, ASC, "name");
      PageRequest pageRequest = PageRequest.of(pageIndex, pageSize, Direction.fromString(dir), order);
      Page<Parent> parentPage = repo.searchByNameLike("%"+q+"%", pageRequest);
      log.info("Returning data");
      return parentPage;
   }

   @GetMapping("parent/search-driving")
   @Transactional
   public Page<Parent> drivingQuery() {
      PageRequest pageRequest = PageRequest.of(0, 20, ASC, "name");
      Page<Long> parentIdsPage = repo.searchIdsByNameLike("%ar%", pageRequest);
      List<Long> parentIds = parentIdsPage.getContent();
      log.info("Matched parents: {}", parentIds);
      Map<Long, Parent> parentDataById = repo.fetchParentsWithChildren(parentIds)
              .stream().collect(Collectors.toMap(Parent::getId, identity()));
      return parentIdsPage.map(parentDataById::get); // from Page<Long>->Page<Parent>
   }

   // TODO? #3) Break up with Hibernate and extract the hierarchical objects manually from a resultset
   //  (grouped by parentId)



   //<editor-fold desc="initial data">
   private final JdbcTemplate jdbc;
   @EventListener(ApplicationStartedEvent.class)
   public void insertInitialData() {
      log.warn("INSERTING data ...");
      jdbc.update("INSERT INTO COUNTRY(ID, NAME) SELECT X, 'Country ' || X  FROM SYSTEM_RANGE(1, 20)");
      jdbc.update("INSERT INTO PARENT(ID, NAME, COUNTRY_ID, AGE) SELECT X, 'Parent' || X, 1 + MOD(X,20),  20 + MOD(X*17,40),   FROM SYSTEM_RANGE(1, 1000)");

      // jdbc.update("INSERT INTO PARENT(ID, NAME) SELECT X, 'Parent ' || X FROM SYSTEM_RANGE(1, 1000)");
      jdbc.update("INSERT INTO CHILD(ID, NAME, PARENT_ID) SELECT X, 'Child' || X || '-1',X FROM SYSTEM_RANGE(1, 1000)");
      jdbc.update("INSERT INTO CHILD(ID, NAME, PARENT_ID) SELECT X + 1000, 'Child' || X || '-2', X FROM SYSTEM_RANGE(1, 1000)");
      log.info("DONE");
   }
   //</editor-fold>
}

