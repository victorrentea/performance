import base.GatlingEngine;
import io.gatling.javaapi.core.Simulation;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;

public class Leak3Load extends Simulation {
  public static void main(String[] args) {
    GatlingEngine.startClass(Leak3Load.class);
  }

  {
    setUp(scenario(getClass().getSimpleName())
        .repeat(100).on( // for i=1..100
            exec(http("leak3").get("/leak3"))
        )
        .injectOpen(atOnceUsers(100)) // in 100 parallel threads
        // = 10.000 calls
    )
        .protocols(http.baseUrl("http://localhost:8080"))
        .assertions(global().successfulRequests().percent().gt(99.0));
  }

}
