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
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;
import victor.training.performance.jpa.parent.*;
import victor.training.performance.jpa.parent.ParentRepo.ParentProjection;
import victor.training.performance.jpa.uber.Country;
import victor.training.performance.jpa.uber.CountryRepo;

import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.joining;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.springframework.data.domain.Sort.Direction.ASC;
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
    // entuziastu de FP
    // repo.findAll().stream().filter(p -> p.getAge() > 40).collect(toList());
    // ca nu mai pun eu SQL pun filter = wrong daca aduci mii ca sa filterezi 10.
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

  // ======================= Driving Query: identify data + fetch data =============================
  @Test
  public void drivingQuery() {
    List<Long> parentIds = repo.findIds("%");
    log.info("Matched parents IDs: {}", parentIds);
    Set<Parent> fullParents = repo.fetchParentsByIds(parentIds);

    List<ParentDto> results = toSearchResults(fullParents);
    assertResults(results);
  }

}

