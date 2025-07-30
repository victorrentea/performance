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
//  private final Semaphore semaphore = new Semaphore(199); //1 for liveness
  private final Semaphore semaphore = new Semaphore(Runtime.getRuntime().availableProcessors());

  // @Bulkhead // for resilience4j wait_timeout=0,max_permits=...
  @GetMapping // call it 500 times to saturate Tomcat's thread pool: 200 in action + 300 in queue
  public String hotEndpoint() throws InterruptedException {
//    return CompletableFuture.supplyAsync(()->slow());  // runs on max CPU-1 threads, leaving tomcat's thread free
    // unbounded queue -> x-high client latency + potential OOME

    // thread pools also could solve it with a thread pool of max-thread=8, queue-size=0

    if (!semaphore.tryAcquire()) throw new RuntimeException("too busy");
    try {
      return slow();
    }finally {
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
