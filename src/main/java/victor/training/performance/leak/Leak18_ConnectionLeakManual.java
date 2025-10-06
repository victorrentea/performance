package victor.training.performance.leak;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.SQLException;

import static victor.training.performance.util.PerformanceUtil.done;

@Slf4j
@RequiredArgsConstructor
@RestController
// from Release It! book: https://www.amazon.com/Release-Production-Ready-Software-Pragmatic-Programmers/dp/0978739213
public class Leak18_ConnectionLeakManual {
  private final DataSource dataSource;

  public record Flight(
      String origin,
      String destination
  ) {}

  @GetMapping("leak18")
  public String endpoint(Flight flight) throws SQLException {
    var connection = dataSource.getConnection();
    log.info("Start work on ✈️{}->{}",
        flight.origin.toUpperCase(),
        flight.destination.toUpperCase());
    // the real work
    connection.close();
    return done();
  }



  // === REDIS ===
  //  private final JedisPool jedisPool;
  //  @GetMapping("/leak/redis")
  //  public String leak() {
  //    // BAD: borrow and never close
  //    Jedis jedis = jedisPool.getResource();
  //    jedis.set("k","v");
  //    // missing jedis.close();
  //    return "done";
  //  }

  // === MONGO ===
  //  @GetMapping("/leak/mongo")
  //  public String leakMongo() {
  //    // BAD: create client per-request and forget to close → sockets pile up
  //    MongoClient client = MongoClients.create("mongodb://localhost:27017");
  //    MongoDatabase db = client.getDatabase("db");
  //    db.getCollection("c").insertOne(new Document("x",1));
  //    // missing client.close();
  //    return "done";
  //  }
}
