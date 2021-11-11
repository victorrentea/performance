package victor.training.performance.spring;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.collection.internal.PersistentSet;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.*;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RestController // TODO uncomment and study
@RequestMapping("profile/nplus1")
@RequiredArgsConstructor
public class Profile2_NPlusOne implements CommandLineRunner {
   private final ParentRepo repo;
   private final JdbcTemplate jdbc;
   @Override
   public void run(String... args) throws Exception {
      log.warn("INSERTING data ...");
      jdbc.update("INSERT INTO COUNTRY(ID, NAME) SELECT X, 'Country' || X FROM SYSTEM_RANGE(1, 1000)");
      jdbc.update("INSERT INTO PARENT(ID, NAME, COUNTRY_ID) SELECT X, 'Parent' || X, X FROM SYSTEM_RANGE(1, 1000)");
      jdbc.update("INSERT INTO CHILD(ID, NAME, PARENT_ID) SELECT X, 'Child' || X || '-1',X FROM SYSTEM_RANGE(1, 1000)");
      jdbc.update("INSERT INTO CHILD(ID, NAME, PARENT_ID) SELECT X + 1000, 'Child' || X || '-2', X FROM SYSTEM_RANGE(1, 1000)");
      log.info("DONE");
   }

   @GetMapping("{id}")
   public Parent findOne(@PathVariable Long id) {
      Parent p = repo.findById(id).get();
      log.info("returnez p");
      // aici p are country luat cu JOIN dar NU are children
      return p;
   }
   @GetMapping("fara-copii/{id}")
   public String findOneFaraCopii(@PathVariable Long id) {
      return repo.findById(id).get().getName();
   }
   @GetMapping
   public Page<Parent> query() {
      Page<Long> idPage = repo.findByNameLike("%ar%", PageRequest.of(1, 20).withSort(Direction.ASC, "name"));

      List<Parent> parents = repo.findParentsWithChildren(idPage.getContent());
      Map<Long, Parent> parentsById = parents.stream().collect(Collectors.toMap(Parent::getId, Function.identity()));

      return idPage.map(parentId -> parentsById.get(parentId));
   }
}

interface ParentRepo extends JpaRepository<Parent, Long> {
   // driving query
  @Query("SELECT p.id FROM Parent p WHERE p.name LIKE ?1")
   Page<Long> findByNameLike(String namePart, Pageable page);

   // fetching query
   @Query("SELECT distinct p FROM Parent p " +
          "LEFT JOIN FETCH p.children " +
          "LEFT JOIN FETCH p.country " +
          "WHERE p.id IN ?1")
   List<Parent> findParentsWithChildren(List<Long> parentIds);

//   @Query("SELECT p.id FROM Parent p WHERE p.name LIKE ?1")
//   Page<Long> findByNameLike(String namePart, Pageable page);

}

@Entity // doar pt ca demo
@Data
class Country {
   @Id
   @GeneratedValue
   private Long id;
   private String name;
//   @ManyToOne
//   private Region region;

}

@Entity
@Getter
@Setter
class Parent {
   @Id
   @GeneratedValue
   private Long id;
   private String name;

   @ManyToOne
   private Country country; // ma asteptam sa faca Parent LEFT JOIN Country si sa aduca coloanele Country automat intr-un singur query

   @OneToMany(cascade = CascadeType.ALL)
   @JoinColumn(name = "PARENT_ID")
   private Set<Child> children = new PersistentSet();//   HashSet<>();

   public Parent() {}

   public Parent(String name, Set<Child> children) {
      this.name = name;
      this.children = children;
   }
}


@Data
@Entity
class Child {
   @Id
   @GeneratedValue
   @EqualsAndHashCode.Exclude
   private Long id;
   private String name;
}

