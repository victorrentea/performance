package victor.training.performance.jpa;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;
import victor.training.performance.jpa.ParentSearchViewRepo.ParentSearchProjection;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
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
        Country romania = countryRepo.save(new Country(1L, "Romania"));
        repo.save(new Parent("Victor")
                .setCountry(romania)
                .setAge(36)
                .addChild(new Child("Emma"))
                .addChild(new Child("Vlad"))
        );
        repo.save(new Parent("Trofim") // bachelor, no children but 4 cats
                .setAge(42));
        Country moldavia = countryRepo.save(new Country(2L, "Moldavia"));
        repo.save(new Parent("Peter")
                .setAge(41)
                .setCountry(romania)
                .addChild(new Child("Maria"))
                .addChild(new Child("Paul"))
                .addChild(new Child("Stephan"))
        );
        TestTransaction.end();

        TestTransaction.start();
    }

    @Value
    static class ParentSearchResult {
        Long id;
        String name;
        String childrenNames;
        public ParentSearchResult(Parent parent) {
            id = parent.getId();
            name = parent.getName();
            childrenNames = parent.getChildren().stream() // asta cauzeaza un Lazy Loading daca esti in trazactia activa.
                //  by default esti daca vii de pe REST in Spring, dar NU esti daca vii de pe:
                //!!  MQ listener, @Scheduled, @Async
                .map(Child::getName).sorted().collect(joining(","));
        }
    }

    // This is what is displayed in the UI:
    private static void assertResultsInUIGrid(List<?> results) {
        assertThat(results) // nimeni nu s-a intors la assertEquals dupa ce a gustat AssertJ
                .extracting("name", "childrenNames")
                .containsExactlyInAnyOrder(
                        tuple("Trofim", ""),
                        tuple("Victor", "Emma,Vlad"),
                        tuple("Peter", "Maria,Paul,Stephan"))
        ;
    }

    // ======================= STAGE 1: SELECT full @Entity =============================
    @Test
    public void selectFullEntity() {
        Set<Parent> parents = repo.findAllCuCopii();
//        Set<Parent> parents = repo.findAllCuCopiiCuPaginare(PageRequest.of(0, 2)).toSet();
//        TypedQuery<Parent> q = entityManager.createNamedQuery("Parent.cuCopii", Parent.class); // crapa de la startup daca JPQL e invalid ❤️
//        TypedQuery<Parent> q = entityManager.createQuery("SELECT p FROM Parent p LEFT JOIN FETCH p.children LEFT JOIN FETCH p.country", Parent.class); // crapa doar la runtime
//        q.setParameter("param", "1")
//        Set<Parent> parents = new HashSet<>(q.getResultList());
//        List<Parent> parents = repo.findAll();
        // acum vad 1 singur query dupa tara, pentru ca ambii parinti au aceeasi tara.
        // Hibernate dupa ce a adus tara primului parinte,
        // o tine in 1st level cache (transaction-scoped) = Persistence COntext
        System.out.println(countryRepo.findById(1L).orElseThrow());

        // - "Cum tunezi second level cacheul Hibernatului"

        log.info("Loaded {} parents: {}", parents.size(), parents);

        List<ParentSearchResult> results = toSearchResults(parents);

        assertResultsInUIGrid(results);
    }

    private List<ParentSearchResult> toSearchResults(Set<Parent> parents) { // eg, in a Mapper
        log.debug("Converting-->Dto START");
        List<ParentSearchResult> results = parents.stream().map(ParentSearchResult::new).collect(toList());
        log.debug("Converting-->Dto DONE");
        return results;
    }


    // ======================= STAGE 2: native SQL query selecting projections ==============
    @Autowired
    ParentSearchViewRepo searchRepo;
    @Test
    public void nativeQuery() {
//        entityManager.createNativeQuery("sql WHERE name LIKE " + httpRequest.getParam("aa")) < bad practice
            // 1) permite SQL injection
            //
//        entityManager.createNamedQuery("ParentSearchView.q1")

        List<ParentSearchProjection> results = searchRepo.nativeQueryForProjections();
        assertResultsInUIGrid(results);
    }

    // ======================= STAGE 3: @Entity on VIEW =============================
    @Test
    @Sql("/create-view.sql")
    public void searchOnView() {
        List<ParentSearchView> results = searchRepo.findAll();
//        entityManager.createQuery("jpql") < bad practice
        assertResultsInUIGrid(results);
    }

}

