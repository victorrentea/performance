import io.gatling.javaapi.core.Simulation;

import static io.gatling.javaapi.core.CoreDsl.constantConcurrentUsers;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static java.time.Duration.ofSeconds;

public class Main_ProfileCpuGatling extends Simulation {
  public static void main(String[] args) {
    GatlingEngine.startClass(Main_ProfileCpuGatling.class);
  }

  {
    String host = "http://localhost:8080";

    setUp(scenario(getClass().getSimpleName())
            .exec(http("create").get("/profile/cpu"))
            .injectClosed(constantConcurrentUsers(40).during(ofSeconds(5))))
            .protocols(http.baseUrl(host));
  }
}
