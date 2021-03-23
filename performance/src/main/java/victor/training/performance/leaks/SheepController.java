package victor.training.performance.leaks;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.ConcurrencyUtil;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.sql.DataSource;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@RestController
@RequestMapping("load")
@RequiredArgsConstructor
public class SheepController {
   private final SheepService service;

   @GetMapping("create")
   public CompletableFuture<Long> create(@RequestParam(defaultValue = "Bisisica") String name) {
      log.debug("create " + name);
      return service.create(name);
   }
   // TODO Starve Connections
   // TODO Starve Threads

   @GetMapping("search")
   public List<Sheep> search(@RequestParam(defaultValue = "Bisisica") String name) {
      log.debug("search for " + name);
      return service.search(name);
   }
}

@Slf4j
@Service
@RequiredArgsConstructor
class SheepService {
   private final SheepRepo repo;
   private final ShepardService shepard;
   private final DataSource dataSource;

   private final ExecutorService pool = Executors.newFixedThreadPool(200);

   @SneakyThrows
   public CompletableFuture<Long> create(String name) {
//      Future<String> snFuture = pool.submit(() -> shepard.registerSheep(name));
      CompletableFuture<String> futureSN = CompletableFuture
          .supplyAsync(() -> shepard.registerSheep(name), pool);
      log.debug("Persist");


      CompletableFuture<Sheep> futureSheep = futureSN.thenApply(sn -> new Sheep(name, sn));
      CompletableFuture<Long> futureId = futureSheep.thenApply(sheep -> repo.save(sheep).getId());

      return futureId;
   }

   public List<Sheep> search(String name) {
      return repo.getByNameLike(name);
   }
}

@Slf4j
@Service
class ShepardService {
   public String registerSheep(String name) {
      log.debug("Calling external WS");
      ConcurrencyUtil.sleepq(500);
      return UUID.randomUUID().toString();
   }
}

interface SheepRepo extends JpaRepository<Sheep, Long> {
   List<Sheep> getByNameLike(String name);
}


@Entity
@Data
    // Don't
class Sheep {
   @GeneratedValue
   @Id
   private Long id;

   private String name;
   private String sn;

   public Sheep() {
   }

   public Sheep(String name, String sn) {
      this.name = name;
      this.sn = sn;
   }
}