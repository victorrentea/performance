package victor.training.performance.jpa;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;
import victor.training.performance.jpa.ParentSearchViewRepo.ParentSearchProjection;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.List;

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
        Country romania = countryRepo.save(new Country(1L, "Romania")); // SELECT where ID=? si pt ca nu exista ID=> INSERT pt ca in baza nu gaseste ID 1
        repo.save(new Parent("Victor")
                .setCountry(romania)
                .setAge(36)
                .addChild(new Child("Emma"))
                .addChild(new Child("Vlad"))
        );
        repo.save(new Parent("Trofim") // bachelor, no children
                .setAge(42));
        repo.save(new Parent("Peter")
                .setAge(41)
//                .setCountry(romania)
//                .setCountry(countryRepo.save(new Country(1L,"Moldavia"))) // UPDATE pt ca are ID nenull si sub Id=1 exista in PersistenceContext COuntry{id=1}
                .setCountry(countryRepo.save(new Country(2L,"Moldavia")))
                .addChild(new Child("Maria"))
                .addChild(new Child("Paul"))
                .addChild(new Child("Stephan"))
        );
        TestTransaction.end();

        TestTransaction.start();
    }

    @Autowired
    private DataSource dataSource;

    @Value
    static class ParentSearchResult { // pleaca catre BRO ca JSON
        Long id;
        String name;
        String childrenNames;
        public ParentSearchResult(Parent parent) {
            id = parent.getId();
            name = parent.getName();
            childrenNames = parent.getChildren().stream().map(Child::getName).sorted().collect(joining(","));
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

    // ======================= STAGE 1: SELECT full @Entity =============================
    @Test
    public void selectFullEntity() {
        log.info("Start!");
        // JPQL="SELECT p FROM Parent p LEFT JOIN FETCH p.country" exclude parintii fara country
        List<Parent> parents = repo.finduMeu();
        log.info("Loaded {} parents: {}", parents.size(), parents);

        List<ParentSearchResult> results = toSearchResults(parents);

        assertResultsInUIGrid(results);
    }

    private List<ParentSearchResult> toSearchResults(List<Parent> parents) { // eg, in a Mapper
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
        List<ParentSearchProjection> results = searchRepo.nativeQueryForProjections();
        assertResultsInUIGrid(results);
    }

    // ======================= STAGE 3: @Entity on VIEW =============================
    @Test
    @Sql("/create-view.sql")
    public void searchOnView() {
        List<ParentSearchView> results = searchRepo.findAll();
        assertResultsInUIGrid(results);
    }

}

