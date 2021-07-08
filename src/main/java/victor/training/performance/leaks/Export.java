package victor.training.performance.leaks;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequiredArgsConstructor
@Slf4j
public class Export implements CommandLineRunner {
   private final SmallRepo smallRepo;

   @GetMapping("/export")
   public String memoryMe() {
      String result = "";
      for (Small small : smallRepo.findAll()) {
         result += small.getName();
      }
      return result;
   }

   @Override
   public void run(String... args) throws Exception {
      log.info("Persisting data");
      smallRepo.saveAll(IntStream.range(0, 50_000).mapToObj(i -> new Small("123456")).collect(Collectors.toList()));
      log.info("DONE");
   }
}

interface SmallRepo extends JpaRepository<Small, Long> {}

@Entity
class Small {
   @Id
   @GeneratedValue
   private Long id;
   private String name;

   public Small() {}

   public Small(String name) {
      this.name = name;
   }

   public String getName() {
      return name;
   }
}
