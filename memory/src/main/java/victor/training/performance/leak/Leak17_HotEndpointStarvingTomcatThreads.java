package victor.training.performance.leak;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;

import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;
import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("leak17")
public class Leak17_HotEndpointStarvingTomcatThreads {
  private final Semaphore semaphore = new Semaphore(199);

  // accept 8 CPU-bound tasks on 8 cores to avoid context-switch penalty
//   private final Semaphore semaphore = new Semaphore(
//       Runtime.getRuntime().availableProcessors());


  //   @Bulkhead // via config of resilience4j
  @GetMapping // call 200 times to saturate Tomcat's thread pool
  public ResponseEntity<Void> hotEndpoint() throws InterruptedException {
    var gotAPermit = semaphore.tryAcquire();
    if (!gotAPermit) {
      return status(503).build();
    }
    try {
      tensorFlow();
    } finally {
      semaphore.release();
    }
    return ok(null);
  }

  @Configuration
  public static class TensorConfig {
    @Bean
    public ThreadPoolTaskExecutor tensorExecutor() {
      ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
      executor.setCorePoolSize(2);
      var cpuCount = Runtime.getRuntime().availableProcessors();
      executor.setMaxPoolSize(cpuCount /*- 1*/);
      executor.setQueueCapacity(0);
      executor.setThreadNamePrefix("tensor-");
      return executor;
    }
  }

  //   new ThreadPool(size=#cpu, queue=0) // less efficient:  +8 more threads

  private final ThreadPoolTaskExecutor tensorExecutor;

  @GetMapping("cf")
  public CompletableFuture<Void> hotEndpointCF() {
    CompletableFuture<Void> cf = CompletableFuture.runAsync(() -> tensorFlow(), tensorExecutor);
    return cf; // Tomcat's threads is released IMMEDIATELY
  }

  private void tensorFlow() {
    sleepMillis(60_000); // pretend CPU
    sleepMillis(60_000); // criminal SQL
  }

  @GetMapping("/liveness")
  public String liveness() {
    return "k8s, please don't kill me: returned at " + LocalDateTime.now();
  }
}
