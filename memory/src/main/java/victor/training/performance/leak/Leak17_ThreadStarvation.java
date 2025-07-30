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
  private final Semaphore semaphore = new Semaphore(199);

  //   @Bulkhead // resilience4j
  @GetMapping // call it 500 times to saturate Tomcat's thread pool: 200 in action + 300 in queue
  public String hotEndpoint() {
    return slow(); // 10+ seconds
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
