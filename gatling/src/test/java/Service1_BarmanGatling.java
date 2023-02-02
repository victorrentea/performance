import io.gatling.javaapi.core.Simulation;

import static io.gatling.javaapi.core.CoreDsl.constantConcurrentUsers;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static java.time.Duration.ofSeconds;

public class Service1_BarmanGatling extends Simulation {
  public static void main(String[] args) {
    GatlingEngine.startClass(Service1_BarmanGatling.class);
  }

  {
    String host = "http://localhost:8080";
//    String host = "http://localhost:8081";

    setUp(scenario(getClass().getSimpleName()).exec(http("")
//                    .get("/drink/sequential")
//                    .get("/loom")
                            .get("/delay")
            )
            .injectClosed(constantConcurrentUsers(300).during(ofSeconds(5))))

            .protocols(http.baseUrl(host));
  }
}
