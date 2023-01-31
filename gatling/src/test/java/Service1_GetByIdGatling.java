import io.gatling.javaapi.core.Simulation;

import static io.gatling.javaapi.core.CoreDsl.constantConcurrentUsers;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static java.time.Duration.ofSeconds;

public class Service1_GetByIdGatling extends Simulation {
  {
    String host = "http://localhost:8081";

    setUp(scenario(getClass().getSimpleName()).exec(http("")
                    .get("/1"))
            .injectClosed(constantConcurrentUsers(300).during(ofSeconds(10))))

            .protocols(http.baseUrl(host));
  }
}
