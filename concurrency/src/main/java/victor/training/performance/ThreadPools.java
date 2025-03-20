package victor.training.performance;

import io.micrometer.core.instrument.logging.LoggingMeterRegistry;

import java.util.Objects;
import java.util.concurrent.*;

import static java.lang.System.currentTimeMillis;
import static victor.training.performance.util.PerformanceUtil.log;
import static victor.training.performance.util.PerformanceUtil.sleepMillis;

public class ThreadPools {
  public static void main(String[] args) throws InterruptedException {
    // TODO use a fixed number (3) of threads; 3 threads can do 2 tasks / sec = 6/sec
    // tasks arrive 10/sec (higher)
    // 1) Risk: OOM from the queue
    // 2) Longer Latency due to queue waiting time > how to monitor ?
//    ExecutorService executor = Executors.newFixedThreadPool(3);

    // TODO create new threads as necessary, and kill idle ones after 1 min
//     ExecutorService executor = Executors.newCachedThreadPool();
     // Risk: OS crash in face of a request spike < avoid when you can't control the rate of incoming tasks

    // TODO Start 3 threads but max 10 threads, idle threads killed after 1 second
    //  Keep max 5 element in the queue. => rejection possible; experiment with different policies
    ExecutorService executor = new ThreadPoolExecutor(
        2,
        4,
        1, TimeUnit.SECONDS,
        new ArrayBlockingQueue<>(2), // bounded queue
    new ThreadFactory(){
      @Override
      public Thread newThread(Runnable r) {
        Thread t = new Thread(r);
        t.setPriority(3);
        return t;
      }
    }
//         new LinkedBlockingDeque<>() // unbounded
//         , new ThreadPoolExecutor.DiscardPolicy() // so exception to submit = scary!
//         , new ThreadPoolExecutor.CallerRunsPolicy()

         , new ThreadPoolExecutor.DiscardOldestPolicy() // real time time
     );

//    executor = ExecutorServiceMetrics.monitor(meterRegistry, executor, "my-thread-pool");
    for (int i = 0; i < 40; i++) {
      MyTask task = new MyTask(i, 500);
      log("Submitting #" + i);
      executor.submit(task);

      sleepMillis(100);
    }
    // TODO shutdown the executor
    executor.shutdown();
    executor.awaitTermination(1, TimeUnit.MINUTES); //blocks main thread

  }
  public static final LoggingMeterRegistry meterRegistry = new LoggingMeterRegistry();

  static final class MyTask implements Runnable {
    private final int id;
    private final int durationMillis;
    private long t0;

    MyTask(int id, int durationMillis) {// main
      t0 = currentTimeMillis();
      this.id = id;
      this.durationMillis = durationMillis;
    }

    public void run() { // in worker thread
      long t1 = currentTimeMillis();
      meterRegistry.timer("queue_waiting_time").record(t1-t0, TimeUnit.MILLISECONDS);
      log("Waited ms: " + (t1 - t0));
      log("Start #" + id);
      sleepMillis(durationMillis);
      log("Finish #" + id);
    }

    public int id() {
      return id;
    }

    public int durationMillis() {
      return durationMillis;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == this) return true;
      if (obj == null || obj.getClass() != this.getClass()) return false;
      var that = (MyTask) obj;
      return this.id == that.id &&
             this.durationMillis == that.durationMillis;
    }

    @Override
    public int hashCode() {
      return Objects.hash(id, durationMillis);
    }

    @Override
    public String toString() {
      return "MyTask[" +
             "id=" + id + ", " +
             "durationMillis=" + durationMillis + ']';
    }

  }
}

