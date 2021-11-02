package victor.training.performance.jpa;

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
import javax.persistence.TypedQuery;
import java.util.HashMap;
import java.util.Map;

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

    private final Country romania = new Country(1L, "Romania");
    private final User testUser = new User(1L,"test");
    private final Scope globalScope = new Scope(1L,"Global");
    private Long id;

    @BeforeEach
    final void before() {
        em.persist(romania);
        em.persist(testUser);
        em.persist(globalScope);

        UberEntity uber = new UberEntity()
                .setName("Uber2")
                .setStatus(Status.SUBMITTED)
                .setFiscalCountry(romania)
                .setOriginCountryId(romania.getId())
                .setInvoicingCountry(romania)
                .setCreatedBy(testUser)
                .setNationality(romania)
                .setScope(globalScope);
        em.persist(uber);
        id = uber.getId();

        TestTransaction.end();
        TestTransaction.start();
    }
    @Test
    public void findByIdExcessive() {
        log.info("Loading a 'very OOP' @Entity by id...");
        UberEntity uber = em.find(UberEntity.class, id);
//        UberEntity uber = repo.findById(id); // Spring Data
        log.info("Loaded");

        // TODO change link types?

        // --- prod code ---
        if (uber.getStatus() == Status.DRAFT) { // i only loaded UberEntity to get its status
            throw new IllegalArgumentException("Not submitted yet");
        }
        // blah blah
    }
    @Test
    public void searchQuery() {
        log.info("Searching a 'very OOP' @Entity...");
        UberSearchCriteria criteria = new UberSearchCriteria();
        criteria.name = "Uber2";

        // --- prod code ---
        String jpql = "SELECT    " +
                      "new victor.training.performance.jpa.UberBriefDto(u.id, u.name, oc.name)" +
                      "     FROM UberEntity u" +
                      " LEFT JOIN Country oc ON oc.id = u.originCountryId WHERE 1 = 1 ";
        // se mai poate cu : CriteriaAPI, Criteria+Metamodel, QueryDSL, Spring Specifications

        Map<String, Object> params = new HashMap<>();

        if (criteria.name != null) {
            jpql += " AND u.name = :name ";
            params.put("name", criteria.name);
        }

        TypedQuery<UberBriefDto> query = em.createQuery(jpql, UberBriefDto.class);
        for (String key : params.keySet()) {
            query.setParameter(key, params.get(key));
        }
        var results = query.getResultList();

        // TODO fetch only the necessary data
        System.out.println(results);
    }
}
class UberSearchCriteria {
    public String name;
    public Status status;
    // etc
}
@Data
class UberBriefDto {
    private final Long id;
    private final String name;
    private final String originCountry;
}