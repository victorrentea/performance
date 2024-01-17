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
import static org.springframework.data.domain.Sort.Direction.DESC;
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
  @Test
  public void selectFullEntity() {
//    List<Parent> parents = repo.findAll();
    List<Parent> parents = repo.fetchParentFull(); // fac un @Query dedicat
    log.info("Loaded {} parents: {}", parents.size(), parents);
    List<ParentDto> results = toSearchResults(parents);
    assertResults(results);
  }

  private List<ParentDto> toSearchResults(Collection<Parent> parents) { // eg, in a Mapper
    log.debug("Converting-->Dto START");
    List<ParentDto> results = parents.stream()
        .map(parent -> ParentDto.fromEntity(parent))
        .toList();
    log.debug("Converting-->Dto DONE");
    return results;
  }

  // ======================= @Query(native sql) returning Spring projections ==============
  @Test
  public void nativeQuery() { // ca sa scot strict ce am nevoie (nu coloane in plus parent.age, nu randuri in plus 1 rand/parinte)
    List<ParentProjection> results = repo.nativeQuery();
    assertResults(results);
  }

  // ======================= @Entity on Hibernate @Subselect(native sql) ==============
  @Test
  public void subselect() {
    // pp ca vrea prima pagina de 50 eleme, sortata desc dupa nume
    PageRequest pageRequest = PageRequest.of(0, 50, DESC, "name");

    Page<ParentSubselect> results = repo.subselect(pageRequest);
    System.out.println("cate elem: "+results.getTotalElements());
    assertResults(results.getContent());
  }

  // ======================= @Entity mapped on DB VIEW(native sql) =============================
  @Test
  public void view() {
    List<ParentView> results = repo.view();
    assertResults(results);
  }
  // pana aici vorbim despre tuning de searchuri - sa aduca cat mai putine date necesare
  // si sa poti exprima criteriile in JPQL (pe modelul JPA)

  // daca vrei sa exporti date:
  // 1) Stream<> peste toate datele, tii conexiunea deschisa pana termini de trecut prin toate
  // 2) Driving Query technique:
  //    a) selectezi doar ID-urile de exportat -> List<Long> sau int[]
  //    b) faci un query separat care sa aduca datele in PAGINI de cate 1000 de elem din ID-urile preselectate [in paralel] : tema parallelStream() rulat pe ForkJoinPool-ul meu!!!! privat! al meu!
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

