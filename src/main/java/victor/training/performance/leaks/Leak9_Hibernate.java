package victor.training.performance.leaks;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.jooq.lambda.Unchecked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.PerformanceUtil;

import javax.annotation.PostConstruct;
import javax.persistence.*;
import javax.sql.DataSource;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("leak9")
public class Leak9_Hibernate {
   private final BigEntityRepo repo;
   private final EntityManager em;
   private final FastInserter persister;

   @PostConstruct
   public void clearDB() {
      repo.deleteAll();
   }

   @GetMapping
   public String test() {
      return "First persist <a href=\"leak8/persist\">a little data</a>, then <a href=\"leak8/export\"> export it</a>. Then try persisting <a href=\"leak8/persist-more\">more data</a>";
   }

   @GetMapping("persist")
   public String persist() {
      persister.insert(500);
      return "Persisted data. Now <a href=\"export\">export</a> and check the logs";
   }


   @GetMapping("export")
   @Transactional
   public void export() throws IOException {
      log.debug("Exporting....");
      try (Writer writer = new FileWriter("big-entity.txt")) {
         repo.streamAll()
             .map(BigEntity::getDescription)
             .forEach(Unchecked.consumer(writer::write));
      }
      log.debug("Export completed. Sleeping 2 minutes to get a heapdump...");
      PerformanceUtil.sleepq(120 * 1000);
   }
}

@Service
@RequiredArgsConstructor
@Slf4j
class FastInserter {
   private final DataSource dataSource;

   public void insert(int mb) {
      log.debug("Inserting...");
      long t0 = System.currentTimeMillis();
      AtomicInteger percent = new AtomicInteger(0);
      JdbcTemplate jdbc = new JdbcTemplate(dataSource);

      IntStream.range(0, 10).parallel() // bad practice in real projects = DB/REST in ForkJoinPool.commonPool
          .forEach(x -> {
             String s = RandomStringUtils.random(500_000);
             List<Object[]> params = IntStream.range(0, mb / 10)
                 .mapToObj(n -> new Object[]{s})
                 .collect(toList());
             jdbc.batchUpdate("INSERT INTO BIG_ENTITY(ID, DESCRIPTION) VALUES ( HIBERNATE_SEQUENCE.nextval, ?)", params);
             log.debug("Persist {}0%", percent.incrementAndGet());
          });
      log.debug("DONE inserting {} MB in {} ms", mb, System.currentTimeMillis() - t0);
   }

}

@Entity
@Data
class BigEntity {
   @Id
   @GeneratedValue
   private Long id;
   @Lob
   private String description;

   public BigEntity() {
   }

   public BigEntity(String description) {
      this.description = description;
   }
}

interface BigEntityRepo extends JpaRepository<BigEntity, Long> {
   @Query("FROM BigEntity")
   Stream<BigEntity> streamAll();
}