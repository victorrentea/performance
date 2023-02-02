import io.gatling.javaapi.core.Simulation;

import static io.gatling.javaapi.core.CoreDsl.constantConcurrentUsers;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static java.time.Duration.ofSeconds;

public class Main_BottleGatling extends Simulation {
  public static void main(String[] args) {
    GatlingEngine.startClass(Main_BottleGatling.class);
  }

  {
    String host = "http://localhost:8082";

    setUp(scenario(getClass().getSimpleName()).exec(http("")
                    .get("/bottle"))
            .injectClosed(constantConcurrentUsers(1000).during(ofSeconds(5))))

            .protocols(http.baseUrl(host));
  }
}
