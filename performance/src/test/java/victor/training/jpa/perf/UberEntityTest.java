package victor.training.jpa.perf;

import lombok.Value;
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
import javax.persistence.Query;
import javax.persistence.TypedQuery;
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
                .setName("aaa")
                .setFiscalCountry(romania)
                .setOriginCountry(romania)
                .setInvoicingCountry(romania)
                .setCreatedBy(testUser)
                .setNationality(romania)
                .setScope(globalScope);
        em.persist(uber);

        TestTransaction.end();
        TestTransaction.start();

        log.info("Now, loading by id...");
//        UberEntity uberEntity = em.find(UberEntity.class, uber.getId());
//        TypedQuery<UberEntity> query = em.createQuery("SELECT u FROM UberEntity u WHERE u.id=:id", UberEntity.class);
//        Query query = em.createQuery("SELECT u.name, u.originCountry.name FROM UberEntity u WHERE u.id=:id");
//        Query query = em.createQuery("SELECT u.name, u.originCountry.name FROM UberEntity u WHERE u.id=:id");
        TypedQuery<UberSearchResult> query = em.createQuery("SELECT new victor.training.jpa.perf.UberSearchResult(u.name, u.originCountry.name)" +
                                     " FROM UberEntity u WHERE u.id=:id", UberSearchResult.class);
        query.setParameter("id", uber.getId());
        UberSearchResult result = query.getSingleResult();


        log.info("Loaded");
        // TODO 1 change link types?
        // TODO 2 fetch only the necessary data
//        System.out.println(u.getName() + "|" + u.getOriginCountry().getName());
//        System.out.println(u[0] + "|" + u[1]);
        System.out.println(result);
    }
}
@Value
class UberSearchResult {
    String name;
    String originCountry;
}