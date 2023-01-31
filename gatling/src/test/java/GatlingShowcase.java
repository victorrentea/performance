import io.gatling.javaapi.core.Simulation;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;

public class GatlingShowcase extends Simulation {
  {
    String host = "http://localhost:8081";

    setUp(scenario(getClass().getSimpleName()).exec(http("")
                    .get("/drink/sequential"))
            .injectOpen(
                    // Pause for a given duration.
                    nothingFor(4),

                    //  Injects a given number of users at once.
                    atOnceUsers(10),

                    // Injects a given number of users distributed evenly on a time window of a given duration.
                    rampUsers(10).during(5),

                    // Injects users at a constant rate, defined in users per second,
                    // during a given duration. Users will be injected at regular intervals.
                    constantUsersPerSec(20).during(15),

                    // Injects users at a constant rate, defined in users per second,
                    // during a given duration. Users will be injected at randomized intervals.
                    constantUsersPerSec(20).during(15).randomized(),

                    // Injects users from starting rate to target rate, defined in users per second,
                    // during a given duration. Users will be injected at regular intervals.
                    rampUsersPerSec(10).to(20).during(10),

                    // Injects a given number of users following a smooth approximation of the
                    // heaviside step function stretched to a given duration.
                    stressPeakUsers(1000).during(20))
            .protocols(http.baseUrl(host)));


  }
}
