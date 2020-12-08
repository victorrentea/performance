package victor.training.performance.pools;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static victor.training.performance.ConcurrencyUtil.log;
import static victor.training.performance.ConcurrencyUtil.sleepSomeTime;

public class ThreadPool {

   public static void main(String[] args) throws InterruptedException {
      // TODO Executor that keeps a fixed number (3) of threads until it is shut down
//      ExecutorService executor = Executors.newFixedThreadPool(3);

      // TODO Executor that grows the thread pool as necessary, and kills inactive ones after 1 min
//      ExecutorService executor = Executors.newCachedThreadPool();

      // TODO Executor that have at least 3 thread but can grow up to 10 threads. Inactive threads die in 1 second.
      // TODO Vary the fixed-sized queue to see it grow the pool and then Rejecting tasks
      ThreadPoolExecutor executor = new ThreadPoolExecutor(
          3, 4,
          1, TimeUnit.SECONDS,
          new ArrayBlockingQueue<>(10)
          );

      for (int i = 0; i < 40; i++) {
         MyTask task = new MyTask();
         log("Submitted new task #" + task.id);
         executor.submit(task);
         sleepSomeTime(100, 200); // simulate random request rate
      }
      // TODO shutdown the executor !
   }
}

class MyTask implements Callable<Integer> {
   private static final AtomicInteger NEXT_ID = new AtomicInteger(0);
   public int id = NEXT_ID.incrementAndGet();

   public Integer call() {
      log("Start work item #" + id);
      sleepSomeTime(600, 800);
      log("Finish work item #" + id);
      return id;
   }
}
