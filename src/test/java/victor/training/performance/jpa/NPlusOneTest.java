package victor.training.performance.jpa;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@Slf4j
@SpringBootTest
@Transactional //  deschide tranzactie pentru fiecare @Test, ruleaza si @before in acea tx, si dupa face ROLLBACK la acea tx
@Rollback(false) // don't wipe the data; lasa datele in db, ca sa mai vad ceva.
//@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD) // anti pattern!
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
				.addChild(new Child("Emma"))
				.addChild(new Child("Vlad"))
		);
		repo.save(new Parent("Trofim") // burlac :P
			.setAge(42));
		repo.save(new Parent("Peter")
				.setAge(27)
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
		List<Parent> parents = repo.findAllFetchingChildren();
		System.out.println(parents);
		System.out.println(parents.stream().map(Parent::getName).collect(joining()));
		em.lock(parents.get(0), LockModeType.PESSIMISTIC_WRITE);

		log.info("Loaded {} parents", parents.size());

		int totalChildren = countChildren(parents);

		assertThat(totalChildren).isEqualTo(5);


		System.out.println("trebuie sa intrebe baza ca nu stie daca altu a facut vreun commit:");
		System.out.println(repo.findAllFetchingChildren());

		System.out.println("Asta insa o ia din 1st level cache:");
//		em.refresh(parents.get(0));
		System.out.println(repo.findById(parents.get(0).getId()));
	}

	// far away...
	private int countChildren(Collection<Parent> parents) {
		log.debug("Start counting children of {} parents: {}", parents.size(), parents);
		int total = 0;
		for (Parent parent : parents) {
//			total += childRepo.findAllByParentId(parent.getId()).count();
			total += parent.getChildren().size(); // N+1 queries problem: 1 query pt parinte, N pentru copii
		}
		log.debug("Done counting: {} children", total);
		return total;
	}


	@Autowired
	private ChildRepo childRepo;


	@Test
	@Sql("/create-view.sql")
	public void searchOnView() {
		Stream<ParentSearchView> parentViews = repo.findAll()
			.stream().map(p -> toDto(p));
//		var parentViews = searchRepo.findAll();

		// TODO 1 restrict to first page (of 1 element)
		// TODO 2 search by parent age >= 40
//		Page<Parent> parentPage = repo.search(null, PageRequest.of(0, 50, Sort.Direction.ASC, "age"));
//		parentPage.get().collect(Collectors.toList())
		assertThat(parentViews)
			.extracting("name","childrenNames")
			.containsExactlyInAnyOrder(
				tuple("Victor",  "Emma,Vlad"),
				tuple("Trofim",  ""),
				tuple("Peter",  "Maria,Paul,Stephan"))
		;
	}

	private ParentSearchView toDto(Parent p) {

		String childrenNames = p.getChildren().stream().map(Child::getName).sorted().collect(joining(","));
		return new ParentSearchView(p.getId(), p.getName(), childrenNames);
	}
}

interface ParentRepo extends JpaRepository<Parent, Long> {
//	@Lock()
//	@EntityGraph
	@Query("SELECT distinct p FROM Parent p LEFT join fetch p.children order by p.age")
	List<Parent> findAllFetchingChildren();


	Page<Parent> search(Specification<Parent> criteria, PageRequest pageable);

//	@Query(value = "select p.ID, P.NAME, STRING_AGG(c.NAME, ',') within group (order by c.name asc) children_names\n" +
//		   "from PARENT P\n" +
//		   "    left join CHILD C on P.ID = C.PARENT_ID\n" +
//		   "group by p.ID, P.NAME", nativeQuery = true)
//	List<Object[]> findNativ();
}

interface ChildRepo extends JpaRepository<Child,Long> {
	@Query("FROM Child  WHERE id IN (?1)")
	List<Child> loadForParent(List<Long> parentIds);

	Stream<Child> findAllByParentId(long parentId);
}

interface ParentSearchViewRepo extends JpaRepository<ParentSearchView, Long> {
	// filtrezi si sortezi pe modelul principal. dar daca vrei vreo fct aggregate (nativa), faci join cu Entitatea mapata pe View
//	@Query("SELECT psv FROM Parent p JOIN ParentSearchView psv ON psv.id=p.id WHERE p.name = 'a' AND p.country.iso2Code='RO'")
//	Page<ParentSearchView> search()
}