package victor.training.performance.jpa;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.joining;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@Slf4j
@SpringBootTest
@Transactional
@Rollback(false)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class NPlusOneTest {

	@Autowired
	EntityManager em;
	@Autowired
	ParentRepo repo;
	@Autowired
	ParentSearchViewRepo searchRepo;

	@BeforeEach
	void persistData() {
		repo.save(new Parent("Victor")
				.setAge(36)
				.setBio(new Biography())
				.addChild(new Child("Emma"))
				.addChild(new Child("Vlad"))
		);
		repo.save(new Parent("Peter")
				.setAge(41)
				.setBio(new Biography())
				.addChild(new Child("Maria"))
				.addChild(new Child("Paul"))
				.addChild(new Child("Stephan"))
		);
		TestTransaction.end();

		TestTransaction.start();
	}

	@Test
	void nPlusOne() {
//		List<Parent> parents = repo.findAll();
		List<Parent> parents = repo.findAllWithChildren();

		// TODO reduce the number of queries ran inside countChildren
		// TODO reduce the total number of queries ran to one
		// TODO how to paginate parents while prefetching children?

		int totalChildren = countChildren(parents);
		assertThat(totalChildren).isEqualTo(5);
	}

	// far away...
	private int countChildren(Collection<Parent> parents) {
		log.debug("Start counting children of {} parents: {}", parents.size(), parents);
		int total = 0;
		for (Parent parent : parents) {
			total += parent.getChildren().size(); // lazy load
		}
		log.debug("Done counting: {} children", total);
		return total;
	}




	@Test
	@Sql("/create-view.sql")
	public void searchOnView() {
		var parentViews = repo.findAll()
			.stream().map(p -> new ParentSearchView(
				p.getId(),
				p.getName(),
				p.getChildren().stream().map(Child::getName).sorted().collect(joining(","))
			));
//		var parentViews = searchRepo.findAll();

		// TODO 1 restrict to first page (of 1 element)
		// TODO 2 search by parent age >= 40
		assertThat(parentViews)
			.extracting("name","childrenNames")
			.containsExactlyInAnyOrder(
				tuple("Victor","Emma,Vlad"),
				tuple("Peter","Maria,Paul,Stephan"))
		;
	}
}

interface ParentRepo extends JpaRepository<Parent, Long> {
	//JPA curat:		session.createQuery("SELECT p FROM Parent p LEFT JOIN FETCH p.children")
	@Query("SELECT p FROM Parent p " +
			 "LEFT JOIN FETCH p.children " +
			 "LEFT JOIN FETCH p.bio")
	List<Parent> findAllWithChildren();

}


interface ParentSearchViewRepo extends JpaRepository<ParentSearchView, Long> {
}