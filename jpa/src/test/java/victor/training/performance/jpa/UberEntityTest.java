package victor.training.performance.jpa;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;
import victor.training.performance.jpa.uber.*;
import victor.training.performance.jpa.uber.UberEntity.Status;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@Slf4j
@SpringBootTest
@Transactional
@Rollback(false) // don't wipe the data after each test (for debugging)
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD) // recreate DB schema before each test
public class UberEntityTest {
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
        Country romania = countryRepo.save(new Country(1L, "Romania")
            .setRegion(new CountryRegion().setName("EMEA")));
        Country belgium = countryRepo.save(new Country(2L, "Belgium")
            .setRegion(new CountryRegion().setName("OTHER")));
        Country france = countryRepo.save(new Country(3L, "France"));
        Country serbia = countryRepo.save(new Country(4L, "Serbia"));
        User testUser = userRepo.save(new User("test"));
        Scope globalScope = scopeRepo.save(new Scope(1L, "Global"));

        UberEntity uber = new UberEntity()
                .setName("::uberName::")
                .setStatus(Status.SUBMITTED)
                .setOriginCountry(belgium)
                .setFiscalCountry(romania)
                .setInvoicingCountry(france)
                .setNationality(serbia)
                .setScope(globalScope)
//                .setScopeEnum(ScopeEnum.GLOBAL) // TODO enum
                .setCreatedBy(testUser);

        uberId = uberRepo.save(uber).getId();

        TestTransaction.end();
        TestTransaction.start();
    }

    @Test
    public void jpql() {
        log.info("SELECTING a 'very OOP' @Entity with JPQL ...");
         List<UberEntity> list = uberRepo.findAll();
//        List<UberEntity> list = uberRepo.findAllWithQuery();// EQUIVALENT
//        List<UberEntity> list = uberRepo.findByName("::uberName::");
        log.info("Loaded using JPQL (see how many queries are above):\n" + list);
    }

    @Test
    public void findById() {
        log.info("Loading a 'very OOP' @Entity by id...");
        UberEntity uber = uberRepo.findById(uberId).orElseThrow(); // or em.find(UberEntity.class, id); in plain JPA
        log.info("Loaded using findById (inspect the above query):\n" + uber);

        // Use-case: I only loaded UberEntity to get its status
        if (uber.getStatus() == Status.DRAFT) {
            throw new IllegalArgumentException("Not submitted yet");
        }
        // more logic
    }

    @Test
    public void search() {
        log.info("Searching for a 'very OOP' @Entity...");

        UberSearchCriteria criteria = UberSearchCriteria.builder().name("::uberName::").build();
        List<UberSearchResult> dtos = classicSearch(criteria);

        System.out.println("Results: \n" + dtos.stream().map(UberSearchResult::toString).collect(joining("\n")));
        assertThat(dtos)
            .extracting("id", "name", "originCountry")
            .containsExactly(tuple(uberId, "::uberName::", "Belgium"));

        // TODO [1] Select new Dto
        // TODO [2] Select u.id AS id -> Dto
    }

    private List<UberSearchResult> classicSearch(UberSearchCriteria criteria) {
        String jpql = "SELECT u FROM UberEntity u WHERE 1 = 1 ";
        // alternative implementation: CriteriaAPI, Criteria+Metamodel, QueryDSL, Spring Specifications
        Map<String, Object> params = new HashMap<>();
        if (criteria.name != null) {
            jpql += " AND u.name = :name ";
            params.put("name", criteria.name);
        }
        if (criteria.status != null) {
            jpql += " AND u.status = :status ";
            params.put("status", criteria.status);
        }
        var query = em.createQuery(jpql, UberEntity.class);
        for (String key : params.keySet()) {
            query.setParameter(key, params.get(key));
        }
        var results = query.getResultList();

        // OR: Spring Data Repo @Query with a fixed JPQL
//        results = uberRepo.searchFixedJqpl(criteria.name, criteria.status);

        return results.stream().map(this::toResult).collect(toList());
    }

    private UberSearchResult toResult(UberEntity entity) {
        return new UberSearchResult(
            entity.getId(),
            entity.getName(),
            entity.getOriginCountry().getName());
    }

    @Builder
    record UberSearchCriteria(String name, Status status, boolean hasPassport) {}

    record UberSearchResult(Long id, String name, String originCountry) {}
}

