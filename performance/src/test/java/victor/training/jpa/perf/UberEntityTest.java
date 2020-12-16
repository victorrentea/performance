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
                .setName("Ubbbbb")
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
        // E RAU sa scoti entitati intregi PT: SEARCH SA EXPORT
//        UberEntity uberEntity = em.find(UberEntity.class, uber.getId()); // 40 coloane + 7 joinuri
//        UberEntity uberEntity = repo.findByNameLike("%bbb%").get(0); // 4 queryuri

        // Scot scalari
        UberProjection uberEntity = repo.findDoarCeamNevoie("%bbb%").get(0);
        log.info("Loaded");
        // TODO 1 change link types?
        // TODO 2 fetch only the necessary data
        System.out.println(uberEntity.getName() + "|" + uberEntity.getOriginCountryName());


    }
    @Autowired
    private UberRepo repo;

}

interface UberRepo extends JpaRepository<UberEntity, Long> {
    //@Query em.createQuery
//    List<UberEntity> findByNameLike(String name);

//    @Query("SELECT name, originCountry.name" +
//           " FROM UberEntity WHERE name LIKE ?1")
//    List<Object[]> findDoarCeamNevoie(String namePart);
    @Query("SELECT new victor.training.jpa.perf.UberProjection" +
           "(name, fiscalCountry.name)" +
           " FROM UberEntity WHERE name LIKE ?1")
    List<UberProjection> findDoarCeamNevoie(String namePart);



//    Map<Long, String> find
}

interface ParentSearchRepo extends JpaRepository<ParentSearch, Long> {
}

@Value
class UberProjection {
    String name;
    String originCountryName;
}
