package victor.training.performance.jpa;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@Slf4j
@SpringBootTest
@Transactional
@Rollback(false) // don't wipe the data
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class NPlusOneTest {
    @Autowired
    EntityManager entityManager;
    @Autowired
    ParentRepo parentRepo;
    @Autowired
    ParentSearchViewRepo searchRepo;

    @BeforeEach
    void persistData() {
        parentRepo.save(new Parent("Victor")
                .setAge(36)
                .addChild(new Child("Emma"))
                .addChild(new Child("Vlad"))
        );
        parentRepo.save(new Parent("Trofim") // bachelor :)
                .setAge(42));
        parentRepo.save(new Parent("Peter")
                .setAge(41)
                .addChild(new Child("Maria"))
                .addChild(new Child("Paul"))
                .addChild(new Child("Stephan"))
        );
        TestTransaction.end();

        TestTransaction.start();
    }

    @Test
    void nPlusOne() {
        List<Parent> parents = parentRepo.loadParentsWithChildren();
        log.info("Loaded {} parents", parents.size());

        int totalChildren = countChildren(parents);

        assertThat(totalChildren).isEqualTo(5);
    }

    // far away in deep production code...
    private int countChildren(Collection<Parent> parents) {
        log.debug("Start counting children of {} parents: {}", parents.size(), parents);
        int total = 0;
        for (Parent parent : parents) {
            total += parent.getChildren().size(); // lazy load: aduce la nevoie copii x N = N+1
        }
//        total = parentRepo.loadParentsWithChildren(parents.stream().map(Parent::getId).collect(Collectors.toList()));
        log.debug("Counted {} children", total);
        return total;
    }


    @Test
    @Sql("/create-view.sql")
    public void searchOnView() {
        Stream<ParentSearchView> parentViews = parentRepo.findAll()
                .stream().map(p -> toDto(p));
        //		var parentViews = searchRepo.findAll();

        // TODO 1 restrict to first page (of 1 element)
        // TODO 2 search by parent age >= 40
        assertThat(parentViews)
                .extracting("name", "childrenNames")
                .containsExactlyInAnyOrder(
                        tuple("Trofim", ""),
                        tuple("Victor", "Emma,Vlad"),
                        tuple("Peter", "Maria,Paul,Stephan"))
        ;
    }

    private ParentSearchView toDto(Parent p) {
        String childrenNames = p.getChildren().stream()
                .map(Child::getName)
                .sorted()
                .collect(joining(","));
        return new ParentSearchView(p.getId(), p.getName(), childrenNames);
    }
}

interface ParentRepo extends JpaRepository<Parent, Long> {
    // tot timpul poti sa faci un query dedicat sa aduci exact ce-ti trebuie. Speri ca JPQL nu SQL
    @Query("SELECT count(c) FROM Child c WHERE c.parent.id IN (?1)") // (parentId,1) IN ((?,1),(?,1),(?,1) ...20k )
    int loadParentsWithChildren(List<Long> parentId);


    @Query("SELECT p FROM Parent p LEFT JOIN FETCH p.children")
    List<Parent> loadParentsWithChildren();



}

interface ChildRepo extends JpaRepository<Child, Long> {

    int countByParentId(Long parentId); //
}


interface ParentSearchViewRepo extends JpaRepository<ParentSearchView, Long> {
    @Query("SELECT psv FROM ParentSearchView psv JOIN Parent p ON p.id = psv.id WHERE p.age > 40")
    ParentSearchView selectFromAggregatedResult_butQueryOnEntityModel();
}