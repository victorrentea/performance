package victor.training.jpa.perf;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@ActiveProfiles("inmemdb")
@Rollback(false)
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
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
                .setFiscalCountryId(romania.getId())
                .setOriginCountryId(romania.getId())
//                .setInvoicingCountryId(romania.getId())
                .setInvoicingCountry(romania)
                .setCreatedBy(testUser)
                .setNationalityId(romania.getId())
                .setScopeId(globalScope.getId());
        em.persist(uber);

        TestTransaction.end();
        TestTransaction.start();


//        countryRepo.findById(uber.getFiscalCountryId()); -- asta produce multe queryuri daca in for
//        Map<Long, Country> toateTarile = countryRepo.findAll();
//        toateTarile.get(uber.getFiscalCountryId());

        log.info("Now, loading by id...");
        UberEntity uberEntity = em.createQuery("SELECT u FROM UberEntity u " +
                "LEFT JOIN FETCH u.invoicingCountry WHERE u.id = :id", UberEntity.class)
                .setParameter("id",uber.getId())
                .getResultList().get(0);//em.find(UberEntity.class, uber.getId());
        log.info("Loaded");
        System.out.println("Lazy.id: " + uberEntity.getInvoicingCountry().getId());
        System.out.println("Lazy.name: " + uberEntity.getInvoicingCountry().getName());
        // TODO fetch only the necessary data
        // TODO change link types?
        System.out.println(uberEntity.toString());
    }
}
