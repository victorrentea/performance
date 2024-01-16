package victor.training.performance.threadpool;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static victor.training.performance.util.PerformanceUtil.log;
import static victor.training.performance.util.PerformanceUtil.sleepSomeTime;

public class ThreadPools {

   public static void main(String[] args) throws InterruptedException {
      // TODO Executor that keeps a fixed number (3) of threads until it is shut down
      // RISK: out of memory daca vin foarte multe taskuri gramada
      ExecutorService executor = Executors.newFixedThreadPool(3);

      // TODO Executor that grows the thread pool as necessary, and kills inactive ones after 1 min
      // ExecutorService executor = Executors. ?

      // TODO Executor that have at least 3 thread but can grow up to 10 threads,
      // with a queue of max 5 elements. Inactive threads die in 1 second.
      // ExecutorService executor = new ThreadPoolExecutor(...)

      // TODO Vary the fixed-sized queue to see it grow the pool and then Rejecting tasks

      for (int i = 0; i < 40; i++) {
         MyTask task = new MyTask();
         log("Submitted new task #" + task.id);
         executor.submit(task);
         sleepSomeTime(100, 200); // simulate random request rate
      }
//      executor.shutdown();
      executor.shutdownNow();
      System.out.println("Main iese");
   }
}

class MyTask implements Callable<Integer> {
   private static final AtomicInteger NEXT_ID = new AtomicInteger(0);
   public int id = NEXT_ID.incrementAndGet();
//   byte[] fileContents; // DOAMNE FERESTE!!! 10MB in RAM Nu cumva!!

   public Integer call() {
      log("Start work item #" + id);
      sleepSomeTime(600, 800);
      log("Finish work item #" + id);
      return id;
   }
}
