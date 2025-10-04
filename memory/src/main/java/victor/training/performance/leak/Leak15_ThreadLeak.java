package victor.training.performance.leak;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("resource")
@Slf4j
@RestController
public class Leak15_ThreadLeak {
  @GetMapping("leak15")
  public String endpoint() throws ExecutionException, InterruptedException {
    ExecutorService pool = Executors.newFixedThreadPool(2);
    var f1 = pool.submit(() -> apiCall(1));
    var f2 = pool.submit(() -> apiCall(2));
    return f1.get() + f2.get();
  }

  private static String apiCall(int i) {
    log.info("Call " + i);
    return "Data " + i;
  }
}

/** ⭐️ KEY POINTS
 * ☣️ Fixed thread pool not #shutdown() keeps worker threads alive forever
 * 👍 try-with-resource your thread pools
 * 👍👍 Better: inject and use a singleton Spring-managed ThreadPoolTaskExecutor
 */
