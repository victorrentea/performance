package victor.training.jpa.perf;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

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
            .setName("Nume bun")
                .setFiscalCountry(romania)
                .setOriginCountryId(romania.getId())
                .setInvoicingCountry(romania)
                .setCreatedBy(testUser)
                .setNationality(romania)
                .setScope(globalScope);
        em.persist(uber);

        TestTransaction.end();
        TestTransaction.start();

        System.out.println(em.find(UberEntity.class, uber.getId()));

        log.info("LINIE --------------------------------------------------------------------");


        log.info("Now, loading by id...");
        UberSearchResult result = em.createQuery("SELECT new victor.training.jpa.perf.UberSearchResult(u.name, u.fiscalCountry.name, u.createdBy.name, u.ibanCode) " +
            " FROM UberEntity u WHERE u.id=:id", UberSearchResult.class)
            .setParameter("id",uber.getId())
            .getSingleResult();
        log.info("Loaded");
        // TODO fetch only the necessary data
        // TODO change link types?
        System.out.println(result);
    }
}
class UberSearchResult {
    private final String name;
    private final String fiscalCountry;
    private final String creatorUser;
    private final String ibanCode;

    public UberSearchResult(String name, String fiscalCountry, String creatorUser, String ibanCode) {
        this.name = name;
        this.fiscalCountry = fiscalCountry;
        this.creatorUser = creatorUser;
        this.ibanCode = ibanCode;
    }

    public String getIbanCode() {
        return ibanCode;
    }
//    public String getIbanCodeWithSpaces() {
//        return ibanCode.substring(0,4) + " ";
//    }

    @Override
    public String toString() {
        return "UberSearchResult{" +
            "name='" + name + '\'' +
            ", originCountry='" + fiscalCountry + '\'' +
            ", creatorUser='" + creatorUser + '\'' +
            ", ibanCode='" + ibanCode + '\'' +
            '}';
    }
}