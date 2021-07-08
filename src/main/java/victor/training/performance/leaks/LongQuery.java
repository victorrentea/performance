package victor.training.performance.leaks;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Random;

@Slf4j
//@RestController // TODO uncomment me!
@RequiredArgsConstructor
public class LongQuery implements CommandLineRunner {
   private final JdbcTemplate jdbc;
   @Override
   public void run(String... args) throws Exception {
      log.info("Inserting Huge Data...");
      jdbc.update("DROP TABLE IF EXISTS TEST");
      jdbc.update("CREATE TABLE TEST(ID BIGINT PRIMARY KEY, ACCOUNT BIGINT, TXID BIGINT)");
      jdbc.update("INSERT INTO TEST SELECT X, RAND()*100, X FROM SYSTEM_RANGE(1, 2000000)");
//      jdbc.update("CREATE Unique INDEX IDX_TEST_ACCOUNT_TXID ON `test` (account, txId DESC);");
      log.info("Done");
   }

   @GetMapping("long")
   public List<Long> indexMiss() {
      return jdbc.queryForList("select txid from test where account=? AND txid<9999999 order by txid desc limit 25", Long.class, new Random().nextInt(100));
   }
}
