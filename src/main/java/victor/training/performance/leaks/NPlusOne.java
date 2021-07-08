package victor.training.performance.leaks;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequiredArgsConstructor
@Slf4j
public class NPlusOne implements CommandLineRunner {
   private final ParentRepo repo;
   @Override
   public void run(String... args) throws Exception {
      List<Parent> parents = IntStream.range(1, 50)
          .mapToObj(i -> new Parent("Parent " + i, createChildren()))
          .collect(Collectors.toList());
      repo.saveAll(parents);
      log.info("Saved parents");
   }

   private Set<Child> createChildren() {
      return Set.of(new Child("one"), new Child("two"));
   }

   @GetMapping("nplus1")
   public Page<Parent> query() {
       return repo.findAll(PageRequest.of(0, 10));
   }
}

interface ParentRepo extends JpaRepository<Parent, Long> {
   List<Parent> findByNameLike(String namePart, Pageable pageRequest);
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


@Entity
class Child {
   @Id
   @GeneratedValue
   private Long id;

   private String name;

   private Child() {
   }

   public Child(String name) {
      this.name = name;
   }
}

