package victor.training.performance.leak;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.concurrent.Semaphore;

import static org.springframework.http.ResponseEntity.ok;
import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@Slf4j
@RestController
@RequestMapping("leak17")
public class Leak17_HotEndpointStarvingTomcatThreads {
  private final Semaphore semaphore = new Semaphore(
      Runtime.getRuntime().availableProcessors());

  //   @Bulkhead // resilience4j
  @GetMapping // call 200 times to saturate Tomcat's thread pool
  public ResponseEntity<Void> hotEndpoint() throws InterruptedException {
    if (!semaphore.tryAcquire()) {
      throw new IllegalStateException("Drumul e anchis");
    }
    try {
      tensorFlow();
    } finally {
      semaphore.release();
    }
    return ok(null);
  }

  @GetMapping("/liveness")
  public String liveness(HttpServletRequest request) {
    return "k8s, 🙏 please don't kill me! Responded at " + LocalDateTime.now();
  }

  private void tensorFlow() {
    sleepMillis(20_000); // pretend CPU
    sleepMillis(20_000); // criminal SQL
  }
}
