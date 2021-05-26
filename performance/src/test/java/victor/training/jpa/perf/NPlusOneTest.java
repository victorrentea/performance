package victor.training.jpa.perf;

import org.junit.Before;
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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Rollback(false)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class NPlusOneTest {

	private static final Logger log = LoggerFactory.getLogger(NPlusOneTest.class);

	@Autowired
	private EntityManager em;

	@Before
	public void persistData() {
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
	}

	@Test
	public void nPlusOne() {
		Set<Parent> parents = new HashSet<>(em.createQuery("SELECT p FROM Parent p " +
																			"LEFT JOIN FETCH p.children " +
																			" ", Parent.class).getResultList());

//		"SELECT d FROM Dog d where d.parent.id IN (:idList)").setParamter("idList", list1000Ids)
		int totalChildren = countChildren(parents);
		assertThat(totalChildren).isEqualTo(5);
	}

	private int countChildren(Collection<Parent> parents) {
		log.debug("Start iterating over {} parents: {}", parents.size(), parents);
		int total = 0;
		for (Parent parent : parents) {
			total += parent.getChildren().size();
		}
		log.debug("Done counting: {} children", total);
		return total;
	}

	@Autowired
	private ParentSearchRepo searchRepo;

	@Test
	public void entityOnView() {
		searchRepo.findAll().forEach(System.out::println);
		assertThat(searchRepo.findAll()).anyMatch(ps -> ps.getChildrenNames().contains("Vlad,Emma"));
	}
}

interface ParentSearchRepo extends JpaRepository<ParentSearch, Long> {
	@Query("SELECT p FROM ParentSearch ps JOIN Parent p on ps.id= p.id WHERE ps.childrenNames LIKE ('%' || ?1 ||'%')")
	List<Parent> searchContainsChild(String childNamePart);
}