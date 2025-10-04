package victor.training.performance.leak;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.concurrent.Semaphore;

import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@Slf4j
@RestController
@RequestMapping("leak17")
public class Leak17_ThreadStarvation {
  // happens if REST calls are handled with Platform Threads (not Virtual)
  private final Semaphore semaphore = new Semaphore(2);

  @GetMapping // call it 500 times to saturate Tomcat's thread pool: 200 in action + 300 in queue
  public String hotEndpoint() throws InterruptedException {
    // Fix: acquire a semaphore permit (or use @Bulkhead in resilience4j)
    semaphore.acquire();

    try {
      return slow(); // Takes time
    } finally {
    semaphore.release();

    }
  }

  @GetMapping("/liveness")
  public String liveness() {
    return "k8s, 🙏 please don't kill me! Responded at " + LocalDateTime.now();
  }

  private String slow() {
    sleepMillis(20_000); // pretend tensorFlow/CPU or criminal SQL
    return "result";
  }
}
