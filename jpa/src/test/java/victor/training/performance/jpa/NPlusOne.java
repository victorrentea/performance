package victor.training.performance.jpa;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;
import victor.training.performance.jpa.parent.*;
import victor.training.performance.jpa.parent.ParentSearchViewRepo.ParentSearchProjection;
import victor.training.performance.jpa.uber.Country;
import victor.training.performance.jpa.uber.CountryRepo;

import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.List;
import java.util.Set;

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
    public ParentSearchResult(Parent parent) {
      this(
          parent.getId(),
          parent.getName(),
          joinChildrenNames(parent.getChildren()));
    }

    private static String joinChildrenNames(Set<Child> children) {
      return children.stream() // lazy loading causes 1 query / parent
          .map(Child::getName).sorted().collect(joining(","));
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
    var parents = repo.fetchAllWithChildren();

    log.info("Loaded {} parents: {}", parents.size(), parents);

    List<ParentSearchResult> results = toSearchResults(parents);

    assertResultsInUIGrid(results);
  }

  private List<ParentSearchResult> toSearchResults(Collection<Parent> parents) { // eg, in a Mapper
    log.debug("Converting-->Dto START");
    List<ParentSearchResult> results = parents.stream().map(ParentSearchResult::new).toList(); // N SELECT
    log.debug("Converting-->Dto DONE");
    return results;
  }


  // ======================= OPTION 2: native SQL query selecting projections ==============
  @Autowired
  ParentSearchViewRepo searchRepo;

  @Test
  public void nativeQuery() {
    List<ParentSearchProjection> results = searchRepo.nativeQueryForProjections();
    assertResultsInUIGrid(results);
  }

  // ======================= OPTION 3: Hibernate @Subselect ==============
  @Test
  public void subselect() {
    Page<ParentSearchSubselect> results = repo.searchSubselect("%", PageRequest.of(0, 5));
    assertResultsInUIGrid(results.getContent());
  }

  // ======================= OPTION 4: @Entity on VIEW =============================
  @Test
  @Sql("/create-view.sql")
  public void searchOnView() {
    List<ParentSearchView> results = searchRepo.findAll();
    assertResultsInUIGrid(results);
  }

}

