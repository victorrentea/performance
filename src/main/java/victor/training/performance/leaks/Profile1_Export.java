package victor.training.performance.leaks;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Slf4j
@RestController
@RequestMapping("profile/export")
@RequiredArgsConstructor
public class Profile1_Export implements CommandLineRunner {
   private final SmallRepo smallRepo;
   private final JdbcTemplate jdbc;

   @GetMapping
   public String memoryMe() {
      String result = "";
      for (Small small : smallRepo.findAll()) {
         result += small.getName();
      }

      return "Exported characters: " + result.length();
   }

   @Override
   public void run(String... args) throws Exception {
      log.info("Persist...");
      jdbc.update("INSERT INTO SMALL(ID, NAME) SELECT X, '123456' FROM SYSTEM_RANGE(1, 50*1000)");
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
