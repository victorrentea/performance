package victor.training.performance.leaks;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.persistence.*;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("leak10")
@Slf4j
public class Leak10_Hash {
   private final PersisterInPages service;

   public Leak10_Hash(PersisterInPages service) {
      this.service = service;
   }

   @GetMapping
   public void startBatch(HttpServletResponse response) {
      Set<SomeEntity> preexistingPlusThisPage = loadPreExistingData();

      Iterable<List<String>> inputData = readLargeDataSetInPages(500, 1_000_000);
      int pageIndex = 0;
      for (List<String> page : inputData) {
         List<SomeEntity> entities = new ArrayList<>();
         for (String s : page) {
            entities.add(new SomeEntity(s).addTags("a", "b", "c", "d"));
         }
         preexistingPlusThisPage.addAll(entities);

         service.persistPage(entities, preexistingPlusThisPage);

         preexistingPlusThisPage.removeAll(entities);
//         ConcurrencyUtil.sleepq(1); // nice mem graph
         log.info("Persisted Page {}...", pageIndex ++);
      }
   }

   private HashSet<SomeEntity> loadPreExistingData() {
      return new HashSet<>();
   }

   private Iterable<List<String>> readLargeDataSetInPages(int pageSize, int pages) {
      Supplier<String> generateItem = () -> UUID.randomUUID().toString();
      Supplier<List<String>> generatePage = () -> Stream.generate(generateItem).limit(pageSize).collect(toList());
      return  () -> Stream.generate(generatePage).limit(pages).iterator();
   }
}

@Component
class PersisterInPages {
   private final SomeEntityRepo repo;

   PersisterInPages(SomeEntityRepo repo) {
      this.repo = repo;
   }

   @PostConstruct
   public void clearTable() {
      repo.deleteAll();
   }

   @Transactional
   public void persistPage(List<SomeEntity> entities, Set<SomeEntity> preexistingPlusThisPage) {
      for (SomeEntity entity : entities) {
         repo.save(entity);
      }
   }


}

interface SomeEntityRepo extends JpaRepository<SomeEntity, Long> {
}

@Entity
@Data
class SomeEntity {
   @Id
   @GeneratedValue
   private Long id;
   private String name;
   private LocalDateTime createTime = LocalDateTime.now();
   @ElementCollection
   private Set<String> tags = new HashSet<>();

   public SomeEntity() {}
   public SomeEntity(String name) {
      this.name = name;
   }
   public SomeEntity addTags(String... newTags) {
      this.tags.addAll(Arrays.asList(newTags));
      return this;
   }
}