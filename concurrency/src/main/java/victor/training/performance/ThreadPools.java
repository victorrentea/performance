package victor.training.performance;

import io.micrometer.core.instrument.logging.LoggingMeterRegistry;

import java.util.List;
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
// ☢️TOO LONG QUEUE: memory💥, high added latency, risk of data loss on crash/shutdown

    // TODO reuse or create any threads necessary; kill idle ones after 1 min
//     ExecutorService executor = Executors.newCachedThreadPool(); // ☢️TOO MANY THREADS

//    ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    // Historically, in the Java language, threads were expensive to create and to keep due to their stack size.
    // Virtual threads: change this. You can spawn a virtual thread in nanoseconds, and its stack is technically zero unless you actually put something inside.
    // thread pool of virtual threads:::
    // > nonsense for saving resources (time to spawn, stack to keep in mem)
    // > throttling concurrency => use a Semaphore instead


    // If you wanted to restrict the number of expensive batch jobs that you accepted
    // to have running at any point in time, a traditional Java way to do this was to
    // have a thread pool with a single executor inside and submit all the batch jobs
    // you wanted to run to that single executor with one thread. This guaranteed that,
    // in your JVM, you never had more than one batch running, since you had only one thread.
    //
    //The modern approach, when you have virtual threads all around you, is to put
    // a semaphore with one permit inside. This allows only one to get in that method
    // or block of code. When it gets out, it finally { releases the semaphore.

    // TODO Start 3 threads but max 10 threads, idle threads killed after 1 second
    //  Keep max 5 element in the queue. => rejection possible; experiment with different policies
    ExecutorService executor = new ThreadPoolExecutor(
        3, 4,
        1, TimeUnit.MINUTES, // after a minute kill idle worker threads
        new ArrayBlockingQueue<>(5),
        new ThreadPoolExecutor.AbortPolicy());
    // executor = ExecutorServiceMetrics.monitor(meterRegistry, executor, "my-thread-pool");
    // TODO monitor queue waiting time
    try {
      for (int i = 0; i < 50; i++) {
        MyTask task = new MyTask(i, 500);
        log("Submitting #" + i);
        try {
          executor.submit(task);
        } catch (Exception e) {
          log("❌HTTP request rejected: "+ e );
        }
        sleepMillis(100); // 10 rps
      }
      // TODO shutdown the executor
      meterRegistry.close(); // logs metrics
    } finally {
      executor.shutdown();
//      executor.awaitTermination(1, TimeUnit.SECONDS);
      log("killing spree");
      List<Runnable> runnablesInQueueThatWeDrained = executor.shutdownNow();
    }
  }

  record MyTask(int id, int taskDurationMillis) implements Runnable {
    public void run() {
      log("Start #" + id);
//      new FileInputStream("a.txt").read();// blocking method not throwing InterruptedException
//      but read() does stop waiting on interrupt returning 0 but after Thread.isInterrupted() will return false
      try {
        Thread.sleep((long) taskDurationMillis);
      } catch (InterruptedException e) {
        log("interrupted");
        Thread.currentThread().interrupt();
        throw new RuntimeException(e);
      }
      log("Finish #" + id);
    }
  }

  public static final LoggingMeterRegistry meterRegistry = new LoggingMeterRegistry();

}

