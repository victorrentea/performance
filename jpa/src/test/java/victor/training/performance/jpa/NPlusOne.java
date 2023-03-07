package victor.training.performance.jpa;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;
import victor.training.performance.jpa.projections.ChildProjected;
import victor.training.performance.jpa.projections.ParentProjected;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@Slf4j
@SpringBootTest
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
        repo.save(new Parent("Victor")
                .setCountry(countryRepo.save(new Country(1L, "Romania")))
                .setAge(36)
                .addChild(new Child("Emma"))
                .addChild(new Child("Vlad"))
        );
        repo.save(new Parent("Trofim") // bachelor :)
                .setAge(42));
        repo.save(new Parent("Peter")
                .setAge(41)
                .setCountry(countryRepo.save(new Country(2L,"Moldavia")))
                .addChild(new Child("Maria"))
                .addChild(new Child("Paul"))
                .addChild(new Child("Stephan"))
        );
        TestTransaction.end();

        TestTransaction.start();
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
        List<Parent> parents = repo.findAll(); // N + 1 queries problem : fetching the children parent by parent: repeated queries
//        List<Parent> parents = repo.findAll();

        log.info("Loaded {} parents: {}", parents.size(), parents);

        List<ParentSearchResult> results = toSearchResults(parents);

        assertResultsInUIGrid(results);
    }

    private List<ParentSearchResult> toSearchResults(List<Parent> parents) {
        log.debug("Start converting");
        List<ParentSearchResult> results = parents.stream()
                .map(ParentSearchResult::new)
                .collect(toList());
        log.debug("Converting DONE");
        return results;
    }



    @Value
    static class ParentSearchResult {
        Long id;
        String name;
        String childrenNames;
        public ParentSearchResult(Parent parent) {
            id = parent.getId();
            name = parent.getName();
            childrenNames = parent.getChildren().stream().map(Child::getName).sorted().collect(joining(","));
        }
        public ParentSearchResult(ParentProjected parent) {
            id = parent.getId();
            name = parent.getName();
            childrenNames = parent.getChildren() != null ?
                    parent.getChildren().stream().map(ChildProjected::getName).sorted().collect(joining(","))
                    : "";
        }

    }

    // ======================= STAGE 2: @Entity on VIEW =============================

    @Autowired
    ParentSearchViewRepo searchRepo;
    @Test
    @Sql("/create-view.sql")
    public void searchOnView() {
        List<ParentSearchView> results = searchRepo.findAll();
        assertResultsInUIGrid(results);
    }

    // ======================= STAGE -: Spring Projections [⚠️NOT WORKING] =============================
    @Test
    public void springProjections() {
        Set<ParentProjected> parents = repo.findAllProjected(); // SELECTS ALL columns ! [FAIL]

        String parentNames = parents.stream().map(ParentProjected::getName).collect(joining(","));
        log.info("Loaded {} parents with names: {}", parents.size(), parentNames);

        List<ParentSearchResult> results = parents.stream().map(ParentSearchResult::new).collect(toList());
        assertResultsInUIGrid(results);
    }


}

