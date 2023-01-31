import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.constantConcurrentUsers;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static java.time.Duration.ofSeconds;

public class Service2_HttpClientGatling extends Simulation {
  {
    String host = "http://localhost:8082";

    setUp(scenario(getClass().getSimpleName())
            .exec(http("httpclient").post("/httpclient"))
            .injectClosed(constantConcurrentUsers(40).during(ofSeconds(5))))
            .protocols(http.baseUrl(host));
  }
}
