package victor.training.performance.jpa;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;
import victor.training.performance.jpa.entity.*;
import victor.training.performance.jpa.repo.CountryRepo;
import victor.training.performance.jpa.repo.ParentRepo;
import victor.training.performance.jpa.repo.ParentRepo.ParentProjection;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@Slf4j
@SpringBootTest
@Transactional
@Rollback(false) // COMMIT at the end of each @Test, to be able to look in the DB contents
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD) // nuke Spring + re-init DB with Hibernate
public class NPlusOne {
  @Autowired
  EntityManager entityManager;
  @Autowired
  ParentRepo repo;
  @Autowired
  CountryRepo countryRepo;

  @BeforeEach
  void persistData() {
    repo.deleteAll();
    countryRepo.deleteAll();
    Country romania = countryRepo.save(new Country(1L, "Romania"));
    Country moldavia = countryRepo.save(new Country(2L, "Moldavia"));
    repo.save(new Parent("Victor")
        .setCountry(romania)
        .setAge(36)
        .addChild(new Child("Emma"))
        .addChild(new Child("Vlad"))
    );
    repo.save(new Parent("Peter")
        .setAge(41)
        .setCountry(romania)
        .addChild(new Child("Maria"))
        .addChild(new Child("Paul"))
        .addChild(new Child("Stephan"))
    );
    repo.save(new Parent("Trofim") // bachelor, no children
        .setCountry(moldavia)
        .setAge(42));
    TestTransaction.end(); // force a COMMIT

    TestTransaction.start();
  }

  // This is what is displayed in a UI grid:
  private static void assertResults(Collection<?> results) {
    assertThat(results)
        .extracting("name", "childrenNames")
        .containsExactlyInAnyOrder(
            tuple("Trofim", ""),
            tuple("Victor", "Emma,Vlad"),
            tuple("Peter", "Maria,Paul,Stephan"));
  }

  // ======================= SELECT full @Entity =============================
  @Test
  public void selectFullEntity() {
    List<Parent> parents = repo.findAll();
    log.info("Loaded {} parents: {}", parents.size(), parents);

    List<ParentDto> results = toSearchResults(parents);
    assertResults(results);
  }

  private List<ParentDto> toSearchResults(Collection<Parent> parents) { // eg, in a Mapper
    log.debug("Converting-->Dto START");
    List<ParentDto> results = parents.stream().map(ParentDto::fromEntity).toList();
    log.debug("Converting-->Dto DONE");
    return results;
  }

  // ======================= @Query(native sql) returning Spring projections ==============
  @Test
  public void nativeQuery() {
    List<ParentProjection> results = repo.nativeQuery();
    assertResults(results);
  }

  // ======================= @Entity on Hibernate @Subselect(native sql) ==============
  @Test
  public void subselect() {
    // TODO pagination
    // TODO filter the original @Entity model
    List<ParentSubselect> results = repo.subselect();
    assertResults(results);
  }

  // ======================= @Entity mapped on DB VIEW(native sql) =============================
  @Test
  public void view() {
    List<ParentView> results = repo.view();
    assertResults(results);
  }

}

