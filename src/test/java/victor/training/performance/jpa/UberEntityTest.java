package victor.training.performance.jpa;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;
import victor.training.performance.jpa.UberEntity.Status;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@SpringBootTest
@Transactional
@Rollback(false) // don't wipe the data
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class UberEntityTest {
    private static final Logger log = LoggerFactory.getLogger(UberEntityTest.class);

    private int _x; // un framework se astepta la "_"
    // pt ca mapa din baza automat coloana COLU pe campul _colu; prin reflection.




    @Autowired
    private EntityManager em;
    @Autowired
    private UberEntityRepo repo;

    private Long uberId;

    @BeforeEach
    final void before() {
        Country romania = new Country(1L, "Romania");
        Country belgium = new Country(2L, "Belgium");
        Country france = new Country(3L, "France");
        Country serbia = new Country(4L, "Serbia");
        User testUser = new User("test");
        Scope globalScope = new Scope(1L,"Global"); // TODO enum
        em.persist(romania);
        em.persist(belgium);
        em.persist(france);
        em.persist(serbia);
        em.persist(testUser);
        em.persist(globalScope);

        UberEntity uber = new UberEntity()
                .setName("::uberName::")
                .setStatus(Status.SUBMITTED)
                .setOriginCountryId(belgium.getId())
                .setFiscalCountry(romania)
                .setInvoicingCountry(france)
                .setNationality(serbia)
                .setScope(globalScope)
//                .setScopeEnum(ScopeEnum.GLOBAL)
                .setCreatedBy(testUser);
        em.persist(uber);
        uberId = uber.getId();

        TestTransaction.end();
        TestTransaction.start();
    }

//    enum RecordStatus{ DRAFT("D"), SUMITTED("S")}  + / hibernate custom type  == > pe CHAR in DB
    @Test
    public void findById() { //!!!!!
        log.info("Loading a 'very OOP' @Entity by id...");
        UberEntity uber = repo.findById(uberId).orElseThrow(); // em.find(UberEntity.class, id); // plain JPA
        log.info("Loaded using find (inspect the above query):\n" + uber);

        // Use-case: I only loaded UberEntity to get its status
        if (uber.getStatus() == Status.DRAFT) {
            throw new IllegalArgumentException("Not submitted yet");
        }
        // etc..
    }

    @Test
    public void findAll() { // in spate ruleaza JQPL: "SELECT u FROM Uber"
        log.info("Loading a 'very OOP' @Entity with JPQL ...");
        List<UberEntity> list = repo.toate();
        log.info("Loaded using JPQL (see how many queries are above):\n" + list);
    }

    @Test
    public void search() {
        log.info("Searching for 'very OOP' @Entity...");
        UberSearchCriteria criteria = new UberSearchCriteria();
        criteria.name = "::uberName::";
        boolean cevaObscur = true   ;
        for (int i = 1; i <= 1; i++) {
            System.out.println();
            System.out.println();
            System.out.println();

            if (cevaObscur) {
                break; // nostalgia dupa GOTO label75
            }
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println();
        }
        List<UberSearchResult> dtos = search(criteria);

        System.out.println("Results: \n" + dtos.stream().map(UberSearchResult::toString).collect(joining("\n")));
        assertThat(dtos)
            .extracting("id", "name", "originCountry")
            .containsExactly(tuple(uberId, "::uberName::", "Belgium"));
    }

    private List<UberSearchResult> search(UberSearchCriteria criteria) {
        // cand faci searchuri, niciodata nu scoti entitati intregi!!!
        String jpql = "SELECT new victor.training.performance.jpa.UberSearchResult(" +
                      "     u.id, u.name, oc.name) " +
                      " FROM UberEntity u " +
                      " JOIN Country oc ON oc.id = u.originCountryId " +
                      " WHERE 1 = 1 ";
        // alternative implementation: CriteriaAPI, Criteria+Metamodel, QueryDSL, Spring Specifications

        Map<String, Object> params = new HashMap<>();
        List<String> parts = new ArrayList<>();

        if (criteria.name != null) {
            jpql += "   AND u.name = :name   ";
            params.put("name", criteria.name);
        }

        var query = em.createQuery(jpql, UberSearchResult.class);
        for (String key : params.keySet()) {
            query.setParameter(key, params.get(key));
        }
        List<UberSearchResult> dtos = query.getResultList();
        return dtos;
    }
}
class UberSearchCriteria {
    public String name;
    public Status status;
    // etc
}
@Data
@AllArgsConstructor
class UberSearchResult {
    private final Long id;
    private final String name;
    private final String originCountry;
//
//    public UberSearchResult(UberEntity entity) {
//        id = entity.getId();
//        name = entity.getName();
//        originCountry = "TODO";
////        originCountry = entity.getOriginCountry().getName();
//    }
}