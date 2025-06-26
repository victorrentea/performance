package victor.training.performance;

import io.micrometer.core.instrument.logging.LoggingMeterRegistry;

import java.util.concurrent.*;

import static victor.training.performance.util.PerformanceUtil.log;
import static victor.training.performance.util.PerformanceUtil.sleepMillis;

public class ThreadPools {
  public static void main(String[] args) throws InterruptedException {
    // TODO use a fixed number (3) of threads
//    ExecutorService executor = Executors.newFixedThreadPool(3);
    //# threasduri fixat => queue unbounded
    // RISK: OOME daca submiti prea multe

    // TODO reuse or create any threads necessary; kill idle ones after 1 min
//     ExecutorService executor = Executors.newCachedThreadPool();
    //# queue size=0 => worker threads unbounded
    // RISK:  sa aloci > 10k threaduri = RIP OS;

    // TODO Start min 3 threads but max 4 threads,
    //  idle threads killed after 1 second,
    //  Keep max 5 element in the queue.
    //   => rejection possible; experiment with different policies
     ExecutorService executor = new ThreadPoolExecutor(
         3,4,
         1, TimeUnit.SECONDS,
         new PriorityBlockingQueue<>(5)
//         new ThreadPoolExecutor.CallerRunsPolicy() // o forma primitiva de backpressure
     );

     ScheduledThreadPoolExecutor e = new ScheduledThreadPoolExecutor(1);
     e.scheduleWithFixedDelay(()->{
       var racheteList = scarbosssu.getRachete(token, maxDelta:1000 !important);
       updates=sqllite.merge(racheteList);
       socket.publish(updates);
     },0, 5, TimeUnit.SECONDS);

    // executor = ExecutorServiceMetrics.monitor(meterRegistry, executor, "my-thread-pool");
    // TODO monitor queue waiting time
    for (int i = 0; i < 40; i++) {
      MyTask.Type type = MyTask.Type.values()[i%2];
      MyTask task = new MyTask(i, 500, type);
      log("Submitting #" + i+ type);
      executor.execute(task);
      sleepMillis(100);
    }
    // TODO shutdown the executor
    meterRegistry.close(); // logs metrics
  }
  record MyTask(int id, int taskDurationMillis, Type type) implements Runnable, Comparable<MyTask> {
    enum Type {RACHETA,HARTIE}
    public void run() {
      log("Start #" + id+type);
      sleepMillis(taskDurationMillis);
      log("Finish #" + id);
    }

    @Override
    public int compareTo(MyTask o) {
      return type.compareTo(o.type());
    }
  }

  public static final LoggingMeterRegistry meterRegistry = new LoggingMeterRegistry();

}

