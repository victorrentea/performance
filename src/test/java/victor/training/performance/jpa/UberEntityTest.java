package victor.training.performance.jpa;

import lombok.Data;
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
                .setName("nume")
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
//        UberEntity uberEntity = repo.findById(uber.getId()).get();
//
//        UberBriefDto dto = em.createQuery(
//            "SELECT new victor.training.performance.jpa.UberBriefDto(u.id, u.name, u.originCountry.name) " +
//                                         "FROM UberEntity u where u.id=:id", UberBriefDto.class)
//            .setParameter("id", uber.getId())
//            .getSingleResult();


        UberBriefDto dto = repo.findByIdAsBriefDto(uber.getId());

        log.info("Loaded");
        // TODO 1 change link types?
        // TODO 2 fetch only the necessary data
//        UberBriefDto dto = new UberBriefDto(uber.getId(), uber.getName(), uber.getOriginCountry().getName());
        System.out.println(dto);
    }
    @Autowired
    UberEntityRepo repo;
}
@Data
class UberBriefDto {
    private final Long id;
    private final String name;
    private final String originCountryName;
}

interface UberEntityRepo extends JpaRepository<UberEntity, Long> {

    @Query(            "SELECT new victor.training.performance.jpa.UberBriefDto(u.id, u.name, u.originCountry.name) " +
                       "FROM UberEntity u where u.id=?1")
    UberBriefDto findByIdAsBriefDto(long id);

    @Query(            "SELECT u.id, u.name, u.originCountry.name " +
                       "FROM UberEntity u where u.id=?1")
    Object[] findByIdAsBriefDto_degenerat(long id);

}