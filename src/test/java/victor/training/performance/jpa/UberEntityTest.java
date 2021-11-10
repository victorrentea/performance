package victor.training.performance.jpa;

import lombok.AllArgsConstructor;
import lombok.Value;
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

import static org.assertj.core.api.Assertions.assertThat;

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

    private Long id;

    @BeforeEach
    final void before() {
        Country romania = new Country(1L, "Romania");
        Country belgium = new Country(2L, "Belgium");
        Country france = new Country(3L, "France");
        Country serbia = new Country(4L, "Serbia");
        User testUser = new User(1L,"test");
        Scope globalScope = new Scope(1L,"GLOBAL");
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
                .setOriginCountryId(belgium.getId())
                .setInvoicingCountry(france)
                .setNationality(serbia)
                .setCreatedBy(testUser)
                .setScope(globalScope);
//                .setScope(ScopeEnum.GLOBAL);
        em.persist(uber);
        id = uber.getId();


        TestTransaction.end();
        TestTransaction.start();
    }

//    enum RecordStatus{ DRAFT("D"), SUMITTED("S")}  + / hibernate custom type  == > pe CHAR in DB
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
    public void findById() {
        // standardul JPA il obliga pe Hibernate ca entitatile returnate sa aiba preincarcate
        // toate relatiile @ManyToOne (fie cu JOIN inplus ca mai jos, fie cu SELECT succesive ca in searchQuery la inceput)
        UberEntity uber = repo.findById(id).get();
        uber.setStatus(Status.SUBMITTED);
        log.info("Searching a 'very OOP' @Entity...");
    }
    @Test
    public void searchQueryCuCachePeCountry() { // SOLVED
        log.info("Searching a 'very OOP' @Entity...");
        System.out.println(repo.findAll());
        log.info("No more queries for Countries above, since @Cache solves it");
    }
    @Test
    public void searchQuery() { // SOLVED
        log.info("Searching a 'very OOP' @Entity...");
        UberSearchCriteria criteria = new UberSearchCriteria();
        criteria.name = "::uberName::";

        // --- prod code ---
        List<UberSearchResult> dtos = dynamicSearch(criteria);

        // TODO fetch only the necessary data (List<UserBriefDto>)
        System.out.println(dtos);
        assertThat(dtos).map(UberSearchResult::getName).containsExactly("::uberName::");
    }

    private List<UberSearchResult> dynamicSearch(UberSearchCriteria criteria) {
//        String jpql = "SELECT u.id, u.name, u.originCountry.name FROM UberEntity u WHERE 1 = 1 ";
        String jpql = "SELECT new victor.training.performance.jpa.UberSearchResult(u.id, u.name, originCountry.name)" +
                      " FROM UberEntity u" +
                      " LEFT JOIN Country originCountry ON u.originCountryId = originCountry.id" +
                      " WHERE 1 = 1 ";
        // se mai poate cu : CriteriaAPI, Criteria+Metamodel, QueryDSL, Spring Specifications

        Map<String, Object> params = new HashMap<>();

        if (criteria.name != null) {
            jpql += " AND u.name = :name ";
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
@Value
@AllArgsConstructor
class UberSearchResult {
    Long id;
    String name;
    String originCountry;
}