package victor.training.performance.leak;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static victor.training.performance.util.PerformanceUtil.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class Leak13_Hibernate {
  private final BigEntityRepo repo;
  private final EntityManager entityManager;

  @GetMapping("leak13/export")
  @Transactional
  public String export() throws IOException {
    File file = new File("big-entity-export.txt");
    log.debug("Exporting from DB to {}...", file.getAbsolutePath());
    try (PrintWriter writer = new PrintWriter(file)) {
      repo.streamAll() // iterates rows w/o loading all ≈ while(resultSet.next()) {👴🏻
          .map(BigEntity::getDescription)
          .forEach(writer::write);
    }

    log.debug("Export completed");
//    sleepMillis(60 * 1000); // take a heap dump
    return "Exported " + humanSize(file.length()) + " in " + file.getAbsolutePath()
           + "<br>" + done();
  }
}

/**
 * ⭐️ KEY POINTS:
 * 🧠 You can Stream data from DB within a @Transactional
 * ☣️ A copy of all @Entities given to you is kept in Hibernate's 1st level cache
 * 👍 entityManager#detach(entity) removes it from there
 */

// === === === === === === === Support code  === === === === === === ===

@RestController
@RequestMapping("leak13")
@RequiredArgsConstructor
class Leak13_Support {
  private final BigEntityRepo repo;
  private final FastInserter fastInserter;

  @PostConstruct
  public void clearDB() {
    repo.deleteAll();
  }

  @GetMapping
  public String html() {
    return "First <a href=\"/leak13/persist\">persist the data</a>,<br> then <a href=\"/leak13/export\"> export it to a file</a>.<br> Note that after each restart the database is cleared";
  }

  @GetMapping("persist")
  public String persist() {
    fastInserter.insert(600);
    return "Inserted 600MB of data. Now <a href=\"/leak13/export\">export</a> the file 'big-entity-export.txt' and check the logs";
  }
}

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

@Service
@RequiredArgsConstructor
@Slf4j
class FastInserter {
  //<editor-fold desc="Irrelevant Support Code">
  private final DataSource dataSource;

  @SuppressWarnings("SqlResolve")
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
                           "VALUES (  next value for big_entity_seq, repeat(? ,500000))", params); // random letter repeated 500.000 times
//                              "VALUES ( HIBERNATE_SEQUENCE.nextval, repeat(? ,500000))",params);
          log.debug("Persist {}0%", percent.incrementAndGet());
        });
    log.debug("DONE inserting {} MB in {} ms", mb, System.currentTimeMillis() - t0);
  }
  //</editor-fold>
}

