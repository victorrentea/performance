package victor.training.performance;

import com.github.tomakehurst.wiremock.standalone.WireMockServerRunner;

import java.io.File;

public class StartWireMock {
   public static void main(String[] args) {
      WireMockServerRunner.main(
          "--port", "9999",
          "--root-dir", new File(".", "src/test/resources").getAbsolutePath(),
          "--global-response-templating", // UUID
           "--async-response-enabled=true" // enable Wiremock to not bottleneck on heavy load
      );
   }
}
