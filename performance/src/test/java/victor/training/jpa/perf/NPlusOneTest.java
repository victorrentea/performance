package victor.training.jpa.perf;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("inmemdb")
@Transactional
@Rollback(false)
public class NPlusOneTest {

	private static final Logger log = LoggerFactory.getLogger(NPlusOneTest.class);

	@Autowired
	private EntityManager em;

	@Before
	public void persistData() {
//		TestTransaction.end();
//		TestTransaction.start();
	}

	@Test
	public void nPlusOne() {
		em.persist(new Parent("Victor")
				.addChild(new Child("Emma"))
				.addChild(new Child("Vlad"))
		);
		em.persist(new Parent("Peter")
				.addChild(new Child("Maria"))
				.addChild(new Child("Stephan"))
				.addChild(new Child("Paul"))
		);
		TestTransaction.end();
		TestTransaction.start();

		log.debug("Inainte de queryul de parinti");
//		List<Parent> parents = parentRepo.findAll(); // nu merge
//		List<Parent> parents = em.createQuery("SELECT p FROM Parent p", Parent.class).getResultList(); // nu merge
		List<Parent> parents = asList(parentRepo.findById(1L).get());//em.createQuery("SELECT p FROM Parent p", Parent.class).getResultList();
		log.debug("Dupa queryul de parinti");

		int totalChildren = anotherMethod(parents);
		assertThat(totalChildren).isEqualTo(5);
	}
	@Autowired
	ParentRepo parentRepo;
//	@Autowired
//	ClasaCuAltaTx tx;

	public int anotherMethod(Collection<Parent> parents) {
		log.debug("Start iterating over {} parents: {}", parents.size(), parents);
		int total = 0;
		for (Parent parent : parents) {
			log.debug("Oare hehehe ce set e ala ? " + parent.getChildren().getClass());
			total += parent.getChildren().size();
		}
		log.debug("Done counting: {} children", total);
		return total;
	}

}
