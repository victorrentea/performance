package victor.training.performance.leak;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static victor.training.performance.util.PerformanceUtil.*;

@SuppressWarnings("resource")
@Slf4j
@RestController
@RequiredArgsConstructor
public class Leak7_ThreadLeak {
  @GetMapping("leak7")
  public String endpoint() throws ExecutionException, InterruptedException {
    ExecutorService pool = Executors.newFixedThreadPool(2);
    var f1 = pool.submit(() -> apiCallA());
    var f2 = pool.submit(() -> apiCallB());
    return f1.get() + f2.get() + done() + "<p>" + getUsedHeapHuman() + "<p>" + getProcessMemoryHuman();
  }

  private static String apiCallA() {
    sleepMillis(100);
    return "A";
  }
  private static String apiCallB() {
    sleepMillis(100);
    return "B";
  }
}

/** ⭐️ KEY POINTS
 * ☣️ Fixed thread pool not #shutdown() keeps worker threads alive forever
 * 👍 try-with-resource your thread pools
 * 👍👍 Better: inject and use a singleton Spring-managed ThreadPoolTaskExecutor
 */
