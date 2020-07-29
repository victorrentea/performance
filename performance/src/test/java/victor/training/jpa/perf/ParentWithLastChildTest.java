package victor.training.jpa.perf;

import org.junit.Before;
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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.time.LocalDate.now;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Rollback(false)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ParentWithLastChildTest {

	private static final Logger log = LoggerFactory.getLogger(ParentWithLastChildTest.class);

	@Autowired
	private EntityManager em;

	@Before
	public void persistData() {
		em.persist(new Parent("Victor")
				.addChild(new Child("Emma").setCreateDate(now().minusYears(7)))
				.addChild(new Child("Vlad").setCreateDate(now().minusYears(2)))
		);
		TestTransaction.end();
		TestTransaction.start();
	}


	@Test
	public void allParentsWithLatestChild() {
		List<ParentWithLastChild> parents = em.createQuery("SELECT p FROM ParentWithLastChild p ", ParentWithLastChild.class).getResultList();
		System.out.println("Got # " + parents.size());
		for (ParentWithLastChild parent : parents) {
			System.out.println(parent);
		}
	}


}
