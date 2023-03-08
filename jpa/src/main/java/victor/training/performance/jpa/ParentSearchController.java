package victor.training.performance.jpa;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
@RestController // TODO uncomment and study
@RequestMapping("parent/search")
@RequiredArgsConstructor
public class ParentSearchController implements CommandLineRunner {
  private final ParentRepo repo;
  private final JdbcTemplate jdbc;

  @Value
  public static class ParentDto {
    Long id;
    String name;
    List<String> childrenNames;
  }

  @GetMapping
   @Transactional(readOnly = true)
  public Page<ParentDto> query() {// VERY BAD ARCH DECISION: exposing out the most sacred class you have: your DOMAIN MODEL
    // 🛑 SELECTing full entities for search with ORM ~> "select new Dto"
    Page<Parent> parentPage = repo.findByNameLike("%ar%", PageRequest.of(0, 20));
    log.info("Returning data: ");


    return parentPage.map(e -> toDto(e));
    // since Spring Boot 2.0 the connection is not released at the end of the transaction
    // but kept until the HTTP response is sent out. WHY?
    // to enable LAZY LOADING to happen while serialization as JSON of the object you returned.

    // because JR developers had problems when returning @Entity from @RestController methods!!!
    // and Spring didn't want to tell them how stupid decision they took, but wanted to play along = MARKETING
  }


  @GetMapping("driving")
  public Page<ParentDto> queryDriving() {
     // #1 driving returing the IDs of the parents
    Page<Long> parentIdPage = repo.findIdsByNameLike("%ar%", PageRequest.of(0, 20));
     // #2 fetching query
    log.info("Returning data: ");
     Set<Parent> parentWithChildren = repo.fetchWithChildren(parentIdPage.getContent());

     Map<Long, Parent> parentById = parentWithChildren.stream().collect(Collectors.toMap(Parent::getId, Function.identity()));

     return parentIdPage.map(parentId -> parentById.get(parentId)).map(this::toDto);
  }

  private ParentDto toDto(Parent parent) {
    List<String> names = parent.getChildren().stream().map(Child::getName).collect(Collectors.toList());
    return new ParentDto(parent.getId(), parent.getName(), names);
  }


  //      Page<Long> idPage = repo.findByNameLike("%ar%", PageRequest.of(0, 10));
  //      List<Long> parentIds = idPage.getContent();

  //      Map<Long, Parent> parents = repo.findParentsWithChildren(parentIds).stream().collect(toMap(Parent::getId, identity()));
  //      return idPage.map(parents::get);


  @Override
  public void run(String... args) throws Exception {
    log.warn("INSERTING data ...");
    jdbc.update("INSERT INTO COUNTRY(ID, NAME) SELECT X, 'Country ' || X  FROM SYSTEM_RANGE(1, 20)");
    jdbc.update("INSERT INTO PARENT(ID, NAME, COUNTRY_ID) SELECT X, 'Parent' || X, 1 + MOD(X,20)  FROM SYSTEM_RANGE(1, 1000)");

    // jdbc.update("INSERT INTO PARENT(ID, NAME) SELECT X, 'Parent ' || X FROM SYSTEM_RANGE(1, 1000)");
    jdbc.update("INSERT INTO CHILD(ID, NAME, PARENT_ID) SELECT X, 'Child' || X || '-1',X FROM SYSTEM_RANGE(1, 1000)");
    jdbc.update("INSERT INTO CHILD(ID, NAME, PARENT_ID) SELECT X + 1000, 'Child' || X || '-2', X FROM SYSTEM_RANGE(1, 1000)");
    log.info("DONE");
  }
}

