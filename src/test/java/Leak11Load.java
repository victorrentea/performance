import gatling.GatlingEngine;
import io.gatling.javaapi.core.Simulation;

import java.util.UUID;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;

public class Leak11Load extends Simulation {
  public static void main(String[] args) {
    GatlingEngine.startClass(Leak11Load.class);
  }

  {
    setUp(scenario(getClass().getSimpleName())
        .repeat(1000).on(
            exec(session -> session.set("idempotencyKey", UUID.randomUUID().toString()))
                .exec(http("").get("/leak11")
                    .header("Idempotency-Key", "#{idempotencyKey}")
                )
        ).injectOpen(atOnceUsers(100)))
        .protocols(http.baseUrl("http://localhost:8080"))
        .assertions(global().successfulRequests().percent().gt(99.0));
  }

}
