package victor.training.performance.leaks;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("profile/nplus1")
@RequiredArgsConstructor
public class Profile2_NPlusOne implements CommandLineRunner {
   private final ParentRepo repo;
   private final JdbcTemplate jdbc;
   @Override
   public void run(String... args) throws Exception {
      log.info("Persisting ...");
      jdbc.update("INSERT INTO PARENT(ID, NAME) SELECT X, 'Parent' || X FROM SYSTEM_RANGE(1, 1000)");
      jdbc.update("INSERT INTO CHILD(ID, NAME, PARENT_ID) SELECT X, 'Child' || X || '-1',X FROM SYSTEM_RANGE(1, 1000)");
      jdbc.update("INSERT INTO CHILD(ID, NAME, PARENT_ID) SELECT X + 1000, 'Child' || X || '-2', X FROM SYSTEM_RANGE(1, 1000)");
      log.info("DONE");
   }

   @GetMapping
   public List<Parent> query() {
//      return repo.findByNameLikeAStupid("%ar%").subList(0,20);
//      return repo.findByNameLike("%ar%", PageRequest.of(0, 20));

      Page<Long> idPage = repo.findByNameLike("%ar%", PageRequest.of(0, 10));
      List<Long> parentIds = idPage.getContent();

      List<Parent> parents = repo.findParentsWithChildren(idPage.getContent());

      return parents;
   }
}

interface ParentRepo extends JpaRepository<Parent, Long> {
   @Query("SELECT p.id FROM Parent p WHERE p.name LIKE ?1 ")
   Page<Long> findByNameLike(String namePart, Pageable page);

   @Query("SELECT p FROM Parent p WHERE p.name LIKE ?1")
   List<Parent> findByNameLikeAStupid(String namePart);

//   @Query("SELECT p.id FROM Parent p WHERE p.name LIKE ?1")
//   Page<Long> findByNameLike(String namePart, Pageable page);

   @Query("SELECT distinct p FROM Parent p LEFT JOIN FETCH p.children" +
          " WHERE p.id IN ?1")
   List<Parent> findParentsWithChildren(List<Long> parentIds);
}

@Entity
@Getter
@Setter
class Parent {
   @Id
   @GeneratedValue
   private Long id;

   private String name;


   @OneToMany(cascade = CascadeType.ALL)
   @JoinColumn(name = "PARENT_ID")
   private Set<Child> children = new HashSet<>();

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

