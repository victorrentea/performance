import io.gatling.javaapi.core.Simulation;

import static io.gatling.javaapi.core.CoreDsl.constantConcurrentUsers;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static java.time.Duration.ofSeconds;

public class Service4_LockGatling extends Simulation {
  {

    String host = "http://localhost:8084";

    setUp(scenario(getClass().getSimpleName())
            .exec(http("").post("/lock"))
            .injectClosed(constantConcurrentUsers(40).during(ofSeconds(5))))
            .protocols(http.baseUrl(host));
  }
}
