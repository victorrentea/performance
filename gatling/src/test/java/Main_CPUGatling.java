import io.gatling.javaapi.core.Body.WithString;
import io.gatling.javaapi.core.Simulation;

import java.util.Collections;
import java.util.List;
import java.util.stream.LongStream;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static java.time.Duration.ofSeconds;
import static java.util.stream.Collectors.toList;

public class Main_CPUGatling extends Simulation {
  public static void main(String[] args) {
    GatlingEngine.startClass(Main_CPUGatling.class);
  }

  {
    String host = "http://localhost:8080";

    List<Long> dbData = LongStream.rangeClosed(1, 30_000).boxed().collect(toList());
    Collections.shuffle(dbData);
    String bodyStr = dbData.toString();

    setUp(scenario(getClass().getSimpleName()).exec(http("")
                    .post("/profile/showcase/payments/delta")
                    .header("Content-Type","application/json")
                    .body(StringBody(bodyStr)))
            .injectClosed(constantConcurrentUsers(8).during(ofSeconds(8))))

            .protocols(http.baseUrl(host));
  }


}
