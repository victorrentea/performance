package victor.training.performance.leak;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Random;

@Slf4j
@RequestMapping("profile/long-query")
//@RestController // TODO uncomment and study
@RequiredArgsConstructor
public class Profile4_LongQuery implements CommandLineRunner {
   private final JdbcTemplate jdbc;
   @Override
   public void run(String... args) throws Exception {
      log.warn("INSERTING huge data...");
      jdbc.update("DROP TABLE IF EXISTS TEST");
      jdbc.update("CREATE TABLE TEST(ID BIGINT PRIMARY KEY, ACCOUNT BIGINT, TXID BIGINT)");

      jdbc.update("INSERT INTO TEST SELECT X, RAND()*100, X FROM SYSTEM_RANGE(1, 2000000)");
//      jdbc.update("CREATE Unique INDEX IDX_TEST_ACCOUNT_TXID ON TEST (account, txId DESC);");
      log.info("DONE");
   }

   @GetMapping
   public List<Long> missingIndex() {
      return jdbc.queryForList("select txid from test where account=? AND txid<9999999 order by account, txid desc limit 25", Long.class, new Random().nextInt(100));
   }
}
