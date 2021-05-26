package victor.training.jpa.perf;

import lombok.Value;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Rollback(false)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UberEntityTest {
    private static final Logger log = LoggerFactory.getLogger(UberEntityTest.class);

    @Autowired
    private EntityManager em;

    private final Country romania = new Country(1L, "Romania");
    private final User testUser = new User(1L,"test");
    private final Scope globalScope = new Scope(1L,"Global");

    @Test
    public void greedyQuery() {
        em.persist(romania);
        em.persist(testUser);
        em.persist(globalScope);


        UberEntity uber = new UberEntity()
                .setFiscalCountry(romania)
                .setOriginCountryId(romania.getId())
                .setInvoicingCountry(romania)
                .setCreatedBy(testUser)
                .setNationality(romania)
                .setScope(globalScope);
        em.persist(uber);

        TestTransaction.end();
        TestTransaction.start();

        log.info("Now, loading by id...");
        repo.findById(uber.getId());
        List<UberDto> uberEntity = repo.getForExport(uber.getId());
        log.info("Loaded");
        // TODO 1 change link types?
        // TODO 2 fetch only the necessary data
        System.out.println(uberEntity);
    }
    @Autowired
    UberEntityRepo repo;
}

interface UberEntityRepo extends JpaRepository<UberEntity, Long> {
    @Query("SELECT new victor.training.jpa.perf.UberDto(u.id, u.name, oc.name) " +
       " FROM UberEntity u " +
           " JOIN Country oc ON u.originCountryId = oc.id  WHERE u.id = ?1")
    List<UberDto> getForExport(Long id);
}

@Value
class UberDto { // sent as JSON
    Long id;
    String name;
    String originCountryName;
}