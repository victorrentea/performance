import io.gatling.javaapi.core.Simulation;

import static io.gatling.javaapi.core.CoreDsl.constantConcurrentUsers;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static java.time.Duration.ofSeconds;

public class BarmanGatlingLoadTest extends Simulation {
  public static void main(String[] args) {
    GatlingEngine.startClass(BarmanGatlingLoadTest.class);
  }

  {
    String host = "http://localhost:8081";

    setUp(scenario(getClass().getSimpleName()).exec(http("")
                            .get("/drink")
            )
            // a constant number of concurrent threads,
            // firing request after request ('closed'),
            // like maniacs for 5 seconds
            .injectClosed(constantConcurrentUsers(300).during(ofSeconds(5))))

            .protocols(http.baseUrl(host));
  }
}
