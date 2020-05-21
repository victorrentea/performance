package victor.training.jpa.perf;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.lang.System.currentTimeMillis;
import static java.lang.System.setOut;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Rollback(false)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class CutChildrenLinksTest {
    private static final Logger log = LoggerFactory.getLogger(CutChildrenLinksTest.class);

    // TODO imagine the number of children / parent is ~100-1000 :)
    // TODO Redesign the entity model

    @Autowired
    private EntityManager em;

    @Autowired
    private AddressRepo addressRepo;


    @Test
    public void work() {
        City city = new City();
        city.setName("Bucharest");
        em.persist(city);

        for (int i = 0; i < 1000; i++) {
            Address address = new Address();
            address.setName("Address " + i);
            address.setCity(city);
            em.persist(address);
        }
        ///////
        TestTransaction.end();
        TestTransaction.start();

        Pageable page = PageRequest.of(2, 10, Sort.by(Sort.Order.asc("name")));
        Page<Address> toate = addressRepo.findAllByCityId(city.getId(), page);
        System.out.println("N pag: " +  toate.getTotalPages());
        System.out.println(toate.getContent().size());

        System.out.println(toate.stream().map(Objects::toString).collect(Collectors.joining("\n")));
    }
}
