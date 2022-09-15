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
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@SpringBootTest
@Transactional
@Rollback(false) // don't wipe the data
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class UberEntityTest {
    private static final Logger log = LoggerFactory.getLogger(UberEntityTest.class);

    @Autowired
    private CountryRepo countryRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ScopeRepo scopeRepo;
    @Autowired
    private EntityManager em;
    @Autowired
    private UberEntityRepo uberRepo;

    private Long uberId;

    @BeforeEach
    final void before() {
        Country romania = countryRepo.save(new Country(1L, "Romania"));
        Country belgium = countryRepo.save(new Country(2L, "Belgium"));
        Country france = countryRepo.save(new Country(3L, "France"));
        Country serbia = countryRepo.save(new Country(4L, "Serbia"));
        User testUser = userRepo.save(new User("test"));
        Scope globalScope = scopeRepo.save(new Scope(1L, "Global")); // TODO enum

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
        uberId = uberRepo.save(uber).getId();

        TestTransaction.end();
        TestTransaction.start();
    }

    @Test
    public void findById() {
        log.info("Loading the @Entity by id...");
        UberEntity uber = uberRepo.findById(uberId).orElseThrow(); // or em.find(UberEntity.class, id); in plain JPA
        log.info("Loaded using find (inspect the above query):\n" + uber);

        // Use-case: I only loaded UberEntity to get its status
        if (uber.getStatus() == Status.DRAFT) {
            throw new IllegalArgumentException("Not submitted yet");
        }
        // etc..
    }

    @Test
    public void findAll_orJPQL() {
        log.info("Loading a 'very OOP' @Entity with JPQL ...");
        List<UberEntity> list = uberRepo.findAll();
        // List<UberEntity> list = uberRepo.all(); // EQUIVALENT
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
    private final String originCountry = "repent";

    public UberSearchResult(UberEntity entity) {
        id = entity.getId();
        name = entity.getName();
//        originCountry = entity.getOriginCountry().getName();
    }
}