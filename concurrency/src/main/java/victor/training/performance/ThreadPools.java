package victor.training.performance;

import io.micrometer.core.instrument.logging.LoggingMeterRegistry;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static victor.training.performance.util.PerformanceUtil.*;

public class ThreadPools {
  public static void main(String[] args) throws InterruptedException {
    // TODO use a fixed number (3) of threads
    ExecutorService executor = null; //Executors.newFixed

    // TODO reuse or create any threads necessary; kill idle ones after 1 min
    // ExecutorService executor = Executors.newCached

    // TODO Start 3 threads but max 10 threads, idle threads killed after 1 second
    //  Keep max 5 element in the queue. => rejection possible; experiment with different policies
    // ExecutorService executor = new ThreadPoolExecutor(...)

    // executor = ExecutorServiceMetrics.monitor(meterRegistry, executor, "my-thread-pool");
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

