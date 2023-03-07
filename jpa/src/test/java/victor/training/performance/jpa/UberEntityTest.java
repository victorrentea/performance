package victor.training.performance.jpa;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@SpringBootTest
@Transactional
@Rollback(false) // don't wipe the data
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD) // recreate DB schema before each test
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
        Country romania = countryRepo.save(new Country(1L, "Romania").setRegion(new CountryRegion().setName("EMEA")));
        Country belgium = countryRepo.save(new Country(2L, "Belgium"));
        Country france = countryRepo.save(new Country(3L, "France"));
        Country serbia = countryRepo.save(new Country(4L, "Serbia"));
        User testUser = userRepo.save(new User("test"));
        Scope globalScope = scopeRepo.save(new Scope(1L, "Global"));

        UberEntity uber = new UberEntity()
                .setName("::uberName::")
                .setStatus(Status.SUBMITTED)
                .setOriginCountryId(belgium.getId())
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
//         List<UberEntity> list = uberRepo.findAll(); // 7 SELECT queries !!!
//        List<UberEntity> list = uberRepo.findAllWithQuery();// EQUIVALENT @Query
        List<UberEntity> list = uberRepo.findByName("::uberName::");
        log.info("Loaded using JPQL (see how many queries are above):\n" + list);
    }

    @Test
    public void findById() {
        log.info("Loading a 'very OOP' @Entity by id...");
        // 55 lines of query 7 JOINs just to get the status
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
        log.info("Searching for 'very OOP' @Entity...");

        UberSearchCriteria criteria = new UberSearchCriteria().setName("::uberName::");
        List<UberSearchResultDto> dtos = classicSearch(criteria);

        System.out.println("Results: \n" + dtos.stream().map(UberSearchResultDto::toString).collect(joining("\n")));
        assertThat(dtos)
            .extracting("id", "name", "originCountry")
            .containsExactly(tuple(uberId, "::uberName::", "Belgium"));

        // TODO [1] Select new Dto
        // TODO [2] Select u.id AS id -> Dto
    }

    private List<UberSearchResultDto> classicSearch(UberSearchCriteria criteria) {
//        String jpql = "SELECT u FROM UberEntity u WHERE 1 = 1 "; // NEVER select full entities to display in a search result grid
//        String jpql = "SELECT u.id, u.name, u.originCountry.name" + // returns a List<Object[]> that you have to guess the type/meaning
//        String jpql = "SELECT u.id as id, u.name as name, u.originCountry.name as originCountry " +
        String jpql = "SELECT new victor.training.performance.jpa.UberSearchResultDto(u.id, u.name, oc.name)" +
                      " FROM UberEntity u" +
                      " LEFT JOIN Country oc ON oc.id = u.originCountryId " + // in JPQL you can traverse a link between two entities even if there to @OneToMany//... link in the mode
                      " WHERE 1 = 1 "; // NEVER select full entities to display in a search result grid
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
        var dtos = query.getResultList();

        // OR: Spring Data Repo @Query with a fixed JPQL
        //entities = uberRepo.searchFixedJqpl(criteria.name);

        return dtos;//.stream().map(UberSearchResultDto::new).collect(toList());
    }
}

interface UberEntityRepo extends JpaRepository<UberEntity, Long> {
    @Query("SELECT u FROM UberEntity u")
    List<UberEntity> findAllWithQuery();

    @Query("SELECT u FROM UberEntity u " +
           "WHERE (:name is null OR UPPER(u.name) LIKE UPPER('%' || :name || '%'))")
    List<UberEntity> searchFixedJqpl(@Nullable String name);


    List<UberEntity> findByName(String name);
}
@Data
class UberSearchCriteria { // received as JSON
    public String name;
    public Status status;
    // etc
}
// Hibernate creates a proxy implementing this interface returning you the data you need
//interface UberSearchResultDto { //sent as JSON
//     Long getId();
//     String getName();
//     String getOriginCountry();
//}

@Value
class UberSearchResultDto { //sent as JSON
    Long id;
    String name;
    String originCountry;
}
// java 17
//record UberSearchResultDto(
//    Long id,
//    String name,
//    String originCountry) {}