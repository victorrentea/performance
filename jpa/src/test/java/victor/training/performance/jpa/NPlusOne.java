package victor.training.performance.jpa;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;
import victor.training.performance.jpa.parent.*;
import victor.training.performance.jpa.uber.Country;
import victor.training.performance.jpa.uber.CountryRepo;

import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.joining;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Rollback(false) // at the end of each @Test, don't rollback the @Transaction, to be able to inspect the DB contents
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD) // nuke Spring + re-init DB with Hibernate
public class NPlusOne {
  @Autowired
  EntityManager entityManager;
  @Autowired
  ParentRepo repo;
  @Autowired
  private CountryRepo countryRepo;

  @BeforeEach
  void persistData() {
    Country ro = countryRepo.save(new Country(1L, "Romania"));
    repo.save(new Parent("Victor")
        .setCountry(ro)
        .setAge(36)
        .addChild(new Child("Emma"))
        .addChild(new Child("Vlad"))
    );
    repo.save(new Parent("Trofim") // bachelor, no children
        .setCountry(ro)
        .setAge(42));
    Country md = countryRepo.save(new Country(2L, "Moldavia"));
    repo.save(new Parent("Peter")
        .setAge(41)
        .setCountry(md)
        .addChild(new Child("Maria"))
        .addChild(new Child("Paul"))
        .addChild(new Child("Stephan"))
    );
    TestTransaction.end();

    TestTransaction.start();
  }

  record ParentSearchResult(Long id, String name, String childrenNames) {
    public static ParentSearchResult fromEntity(Parent parent) {
      String childrenNames = parent.getChildren().stream()
          .map(Child::getName)
          .sorted()
          .collect(joining(","));
      return new ParentSearchResult(parent.getId(), parent.getName(), childrenNames);
    }
  }


  // This is what is displayed in the UI:
  private static void assertResultsInUIGrid(List<?> results) {
    assertThat(results)
        .extracting("name", "childrenNames")
        .containsExactlyInAnyOrder(
            tuple("Trofim", ""),
            tuple("Victor", "Emma,Vlad"),
            tuple("Peter", "Maria,Paul,Stephan"))
    ;
  }

  // ======================= OPTION 1: SELECT full @Entity =============================
  @Test
  public void selectFullEntity() {
//    List<Parent> parents = repo.findAll(); // 1 SELECT
    Pageable pageable = PageRequest.of(0, 10, Sort.Direction.ASC, "name");

    var parents = repo.fetchAllWithChildren();

    log.info("Loaded {} parents: {}", parents.size(), parents);

    List<ParentSearchResult> results = toSearchResults(parents);
//    Collections.sort(results, Comparator.comparing(ParentSearchResult::childrenNames));
    // throw multumita .toList() java 17 care intoarce lista imutabila❤️

    assertResultsInUIGrid(results);
  }

  private List<ParentSearchResult> toSearchResults(Collection<Parent> parents) { // eg, in a Mapper
    log.debug("Converting-->Dto START");
    List<ParentSearchResult> results = parents.stream().map(ParentSearchResult::fromEntity).toList(); // N SELECT
    log.debug("Converting-->Dto DONE");
    return results;
  }


  // ======================= OPTION 2: native SQL query selecting projections ==============
  @Autowired
  ParentSearchViewRepo searchRepo;

  @Test
  public void nativeQuery() {
    var results = searchRepo.nativeQueryForProjections();
    assertResultsInUIGrid(results);
  }

  // ======================= OPTION 3: Map an @Entity to a Hibernate @Subselect ==============
  @Test
  public void subselect() {
//    List<ParentSearchSubselect> results = repo.findAllWithSubselect(); // equivalent cu @Query native=true ce scoate Spring Projections
    Pageable pageable = PageRequest.of(0, 10, Sort.Direction.ASC, "childrenNames");
    List<ParentSearchSubselectEntity> results = repo.findAllWithSubselectCuJoinuriInPlus(pageable); // equivalent cu @Query native=true ce scoate Spring Projections
//    List<ParentSearchSubselect> results = repo.searchSubselect("%", PageRequest.of(0, 5)).getContent();
    assertResultsInUIGrid(results);
  }

  // ======================= OPTION 4: Map an @Entity on DB VIEW =============================
  @Test
  @Sql("/create-view.sql")
  public void searchOnView() {
//    List<ParentSearchViewEntity> results = searchRepo.findAll();
    Pageable pageable = PageRequest.of(0, 10, Sort.Direction.ASC, "childrenNames");
    Page<ParentSearchViewEntity> results = searchRepo.findAll(pageable);
    assertResultsInUIGrid(results.getContent());
  }

}

