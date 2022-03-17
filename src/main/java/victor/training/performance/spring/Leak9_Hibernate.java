package victor.training.performance.spring;

import lombok.Getter;
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
import victor.training.performance.util.PerformanceUtil;

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

//region Fast Inserter

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
             List<Object[]> params = IntStream.range(0, mb / 5)
                 .mapToObj(n -> new Object[]{RandomStringUtils.randomAlphabetic(1)})
                 .collect(toList());

             jdbc.batchUpdate("INSERT INTO BIG_ENTITY(ID, DESCRIPTION) " +
                              "VALUES ( HIBERNATE_SEQUENCE.nextval, repeat(? ,500000))",params); // random letter repeated 500.000 times
             log.debug("Persist {}0%", percent.incrementAndGet());
          });
      log.debug("DONE inserting {} MB in {} ms", mb, System.currentTimeMillis() - t0);
   }

}
//endregion

@Entity
@Getter
class BigEntity {
   @Id
   @GeneratedValue
   private Long id;
   @Lob
   private String description;
}

interface BigEntityRepo extends JpaRepository<BigEntity, Long> {
   @Query("FROM BigEntity")
   Stream<BigEntity> streamAll();
}

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("leak9")
public class Leak9_Hibernate {
   private final BigEntityRepo repo;
   private final EntityManager entityManager;
   private final FastInserter persister;

   @PostConstruct
   public void clearDB() {
      repo.deleteAll();
   }

   @GetMapping
   public String test() {
      return "First <a href=\"/leak9/persist\">persist the data</a>, then <a href=\"/leak9/export\"> export it to a file</a>.";
   }

   @GetMapping("persist")
   public String persist() {
      persister.insert(500);
      return "Persisted data. Now <a href=\"/leak9/export\">export</a> and check the logs";
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

/**
 * KEY POINTS:
 * - We need to keep @Transactional open while consuming the stream
 * - Hibernate keeps a copy of the original state of every entity it gives you, for dirty check at end of transaction
 * - Solution: entityManager.detach(entity) removes the entity from Hibernate
 */