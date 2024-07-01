package victor.training.performance;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.SECONDS;
import static victor.training.performance.util.PerformanceUtil.log;
import static victor.training.performance.util.PerformanceUtil.sleepSomeTime;
@RestController
@Slf4j
public class ThreadPools {
  public static void main(String[] args) throws InterruptedException {
    // TODO keeps a fixed number (3) of threads

//    ExecutorService executor = Executors.newFixedThreadPool(3);

    // TODO creates new threads as necessary, and kills idle ones after 1 min
    // nu exista coada.
//     ExecutorService executor = Executors.newCachedThreadPool();

    // TODO Executor that have at least 3 thread but can grow up to 10 threads,
    //  with a queue of max 5 elements. Kills idle threads after 1 second.
     ExecutorService executor = new ThreadPoolExecutor(
         3, 5,
          1, SECONDS,
          new ArrayBlockingQueue<>(5),
         new CallerRunsPolicy()
     );

    // TODO Experiment: change queue/pool size to see it grow the pool
    // TODO Experiment: cause a task to be rejected

    for (int i = 0; i < 40; i++) {
      MyTask task = new MyTask(i);
      log("Submitting: " + task);
      executor.submit(task);
      sleepSomeTime(100, 200); // simulate random request rate
    }
    // TODO shutdown the executor
  }

  private final AtomicInteger counter = new AtomicInteger();
  @GetMapping("/task")
  public void executatDeUnThreadAlLuiTomcat() {
    int i = counter.incrementAndGet();
    log.info("Executat de un thread al lui Tomcat");
    poolBar.submit(new MyTask(i));
  }
  @Autowired
  ThreadPoolTaskExecutor poolBar;

}

@Slf4j
record MyTask(int id) implements Runnable {
  public void run() {
    log.info("Start " + this);
    sleepSomeTime(6000, 8000);
    log.info("Finish " + this);
  }
}
