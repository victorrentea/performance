package victor.training.performance.jpa;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@Slf4j
@SpringBootTest
public class MassExport {
  public static final int TOTAL_ROWS = 1_000_000;
  // tweak the number of zeros above to see the impact of the export
  @Autowired
  JdbcTemplate jdbcTemplate;

  @BeforeEach
  final void setup() {
    log.info("Importing data (patience; or tweak the number above)...");
    String largeData = "x".repeat(255);
    jdbcTemplate.update("DELETE FROM CHILD");
    jdbcTemplate.update("DELETE FROM PARENT");
    jdbcTemplate.update("""
                INSERT INTO PARENT(ID, NAME)
                SELECT 10000 + X, '%s'
                FROM SYSTEM_RANGE(1, %d)
            """.formatted(largeData, TOTAL_ROWS));
    log.info("Data imported");
  }

  @Autowired
  Export export;

  @Test
  void experiment() {
    log.info("Exporting data...");
    long t0 = System.currentTimeMillis();
    export.export();
    long t1 = System.currentTimeMillis();
    log.info("Export took " + (t1 - t0) + " ms");
  }
}
