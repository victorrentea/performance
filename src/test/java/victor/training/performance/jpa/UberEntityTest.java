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
//                .setScopeEnum(ScopeEnum.GLOBAL) // better?
                .setCreatedBy(testUser);
        uberId = uberRepo.save(uber).getId();

        TestTransaction.end();
        em.clear();
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
    public void findAll_or_JPQL() {
        log.info("Loading a 'very OOP' @Entity with JPQL ...");
        List<UberEntity> list = uberRepo.findAll();
//         List<UberEntity> list = uberRepo.all(); // EQUIVALENT
        log.info("Loaded using JPQL (see how many queries are above):\n" + list);
    }

    @Test
    public void search() {
        log.info("Searching for 'very OOP' @Entity...");
        UberSearchCriteria criteria = new UberSearchCriteria();
        criteria.name = "::uberName::";

        List<UberSearchResultDto> dtos = search(criteria);

        System.out.println("Results: \n" + dtos.stream().map(UberSearchResultDto::toString).collect(joining("\n")));
        assertThat(dtos)
            .extracting("id", "name", "originCountry")
            .containsExactly(tuple(uberId, "::uberName::", "Belgium"));

        // TODO [1] Select new Dto
        // TODO [2] Select u.id AS id -> Dto
        // TODO [3] Select u -> Spring Projections
    }

    // asta e homepageul accesat de 10 ori/sec
    private List<UberSearchResultDto> search(UberSearchCriteria criteria) {
        // NU CARE CUMVA Sa scoti entitati intregi la searchurile fierbinti
        // mandatory: faci asa: select new ...Dto din jqpl
        String jpql = "SELECT new victor.training.performance.jpa.UberSearchResultDto(" +
                      "u.id, u.name, c.name) " +
                      "FROM UberEntity u JOIN Country c ON c.id = u.originCountryId WHERE 1 = 1 ";
        // alternative implementation: CriteriaAPI, Criteria+Metamodel, QueryDSL, Spring Specifications

        Map<String, Object> params = new HashMap<>();

        if (criteria.name != null) {
            jpql += " AND u.name = :name ";
            params.put("name", criteria.name);
        }

        var query = em.createQuery(jpql, UberSearchResultDto.class);
        for (String key : params.keySet()) {
            query.setParameter(key, params.get(key));
        }
        var entities = query.getResultList();
        return entities;
    }
}
class UberSearchCriteria {
    public String name;
    public Status status;
    // etc
}
@Data
@AllArgsConstructor
class UberSearchResultDto { //sent as JSON
    private final Long id;
    private final String name;
    private final String originCountry;

    public UberSearchResultDto(UberEntity entity) {
        id = entity.getId();
        name = entity.getName();
        originCountry = "Belgium";//entity.getOriginCountry().getName();
    }

}
interface UberSearchResultProjection { // Spring can create objects implementing this interface
    Long getId();
    String getName();
    CountryWithName getOriginCountry();
}

interface CountryWithName {
    Long getId();
    String getName();
}