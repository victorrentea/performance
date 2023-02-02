import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.constantConcurrentUsers;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static java.time.Duration.ofSeconds;

public class Main_FireOnLeak extends Simulation {
  public static void main(String[] args) {
    GatlingEngine.startClass(Main_FireOnLeak.class);
  }

  {
    HttpProtocolBuilder httpProtocol = http
      .baseUrl("http://localhost:8080")
      .acceptHeader("*/*")
      .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36");

    ScenarioBuilder scn = scenario(getClass().getSimpleName())
      .exec(http("lock").get("/leak1"));

    setUp(scn.injectClosed(constantConcurrentUsers(200).during(ofSeconds(5)))).protocols(httpProtocol);
  }
}
