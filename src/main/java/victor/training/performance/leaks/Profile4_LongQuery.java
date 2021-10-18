package victor.training.performance.leaks;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@RequestMapping("profile/long-query")
@RestController // TODO uncomment me!
@RequiredArgsConstructor
public class Profile4_LongQuery implements CommandLineRunner {
   private final JdbcTemplate jdbc;
   @Override
   public void run(String... args) throws Exception {
      log.info("Persisting at startup ...");
      jdbc.update("DROP TABLE IF EXISTS TEST");
      jdbc.update("CREATE TABLE TEST(ID BIGINT PRIMARY KEY, ACCOUNT BIGINT, TXID BIGINT)");

      jdbc.update("INSERT INTO TEST SELECT X, RAND()*100, X FROM SYSTEM_RANGE(1, 2000000)");
//      jdbc.update("CREATE Unique INDEX IDX_TEST_ACCOUNT_TXID ON TEST (account, txId DESC);");
      log.info("DONE");
   }

   @GetMapping
   public List<Long> indexMiss() throws ExecutionException, InterruptedException {
      log.info("Running now the fat pig");
      return throttlingLoad.fat().get();
   }
   private final ThrottlingLoad throttlingLoad;

   @GetMapping("fast")
   public Long fastSql() {
      return jdbc.queryForObject("select 1 from dual",          Long.class);
   }
}

@Slf4j
@Component
@RequiredArgsConstructor
class ThrottlingLoad {
   private final JdbcTemplate jdbc;

   @Async("fatPig")
   public CompletableFuture<List<Long>> fat() {
      log.info("Running now IN the fat pig");
      return CompletableFuture.completedFuture(jdbc.queryForList("select txid from test where account=? AND txid<9999999 order by account, txid desc limit 25", Long.class, new Random().nextInt(100)));
   }

}