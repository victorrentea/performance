package victor.training.performance;

import io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics;
import io.micrometer.core.instrument.logging.LoggingMeterRegistry;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static victor.training.performance.util.PerformanceUtil.log;
import static victor.training.performance.util.PerformanceUtil.sleepMillis;

public class ThreadPools {
  public static void main(String[] args) throws InterruptedException {
    // TODO use a fixed number (3) of threads
//    ExecutorService executor = Executors.newFixedThreadPool(3);
    // unbounded queue= stupid => OOME risk + extra latency METRIC queue.size,queue.waiting! (bad if they need results)

    // TODO reuse or create any threads necessary; kill idle ones after 1 min
//    ExecutorService executor = Executors.newCachedThreadPool(); // = ∞ no of threads > too many threads? OS crash?
    // only makes sense if you control # & rate of .submit()

    // TODO Start 3 threads but max 10 threads, idle threads killed after 1 second
    //  Keep max 5 element in the queue. => rejection possible; experiment with different policies
    ExecutorService executor = new ThreadPoolExecutor(
        2, // core = always up
        3, // max = under pressure
        1, TimeUnit.MINUTES,
        new ArrayBlockingQueue<>(200)
//        new ThreadPoolExecutor.CallerRunsPolicy() // tomcat thread will do the work itself
//        new ThreadPoolExecutor.DiscardPolicy()
//        new ThreadPoolExecutor.DiscardOldestPolicy() // sensor reading / stock quotes ~ real-time-ish  (discard the older tasks)
    );

     executor = ExecutorServiceMetrics.monitor(meterRegistry, executor, "my-thread-pool");
    // TODO monitor queue waiting time
    for (int i = 0; i < 40; i++) {
      MyTask task = new MyTask(i, 500);
      log("Submitting #" + i);
      executor.submit(task);
      sleepMillis(100);
    }
    // TODO shutdown the executor
    meterRegistry.close(); // logs metrics
  }
  record MyTask(int id, int taskDurationMillis) implements Runnable {
    public void run() {
      log("Start #" + id);
      sleepMillis(taskDurationMillis);
      log("Finish #" + id);
    }
  }

  public static final LoggingMeterRegistry meterRegistry = new LoggingMeterRegistry();

}

