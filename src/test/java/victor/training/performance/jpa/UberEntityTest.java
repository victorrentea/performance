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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@SpringBootTest
@Transactional
@Rollback(false)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UberEntityTest {
    private static final Logger log = LoggerFactory.getLogger(UberEntityTest.class);

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
        User testUser = new User(1L,"test");
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
                .setFiscalCountry(romania)
                .setOriginCountry(belgium)
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
    public void findById() {
        log.info("Loading a 'very OOP' @Entity by id...");
        UberEntity uber = repo.findById(uberId).get(); // em.find(UberEntity.class, id); // plain JPA
        log.info("Loaded using find (inspect the above query):\n" + uber);

        // Use-case: I only loaded UberEntity to get its status
        if (uber.getStatus() == Status.DRAFT) {
            throw new IllegalArgumentException("Not submitted yet");
        }
        // etc..
    }

    @Test
    public void findAll() {
        log.info("Loading a 'very OOP' @Entity with JPQL ...");
        List<UberEntity> list = repo.findAll();
        log.info("Loaded using JPQL (see how many queries are above):\n" + list);
    }

    @Test
    public void search() {
        log.info("Searching for 'very OOP' @Entity...");
        UberSearchCriteria criteria = new UberSearchCriteria();
        criteria.name = "::uberName::";

        List<UberSearchResult> dtos = search(criteria);

        System.out.println("Results: \n" + dtos.stream().map(UberSearchResult::toString).collect(joining("\n")));
        assertThat(dtos)
            .extracting("id", "name", "originCountry")
            .containsExactly(tuple(uberId, "::uberName::", "Belgium"));
    }

    private List<UberSearchResult> search(UberSearchCriteria criteria) {
        String jpql = "SELECT u FROM UberEntity u WHERE 1 = 1 ";
        // alternative implementation: CriteriaAPI, Criteria+Metamodel, QueryDSL, Spring Specifications

        Map<String, Object> params = new HashMap<>();

        if (criteria.name != null) {
            jpql += " AND u.name = :name ";
            params.put("name", criteria.name);
        }

        var query = em.createQuery(jpql, UberEntity.class);
        for (String key : params.keySet()) {
            query.setParameter(key, params.get(key));
        }
        var entities = query.getResultList();
        return entities.stream().map(UberSearchResult::new).collect(toList());
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

    public UberSearchResult(UberEntity entity) {
        id = entity.getId();
        name = entity.getName();
        originCountry = entity.getOriginCountry().getName();
    }
}