package victor.training.performance.threadpool;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static victor.training.performance.util.PerformanceUtil.log;
import static victor.training.performance.util.PerformanceUtil.sleepSomeTime;

public class ThreadPools {

   public static void main(String[] args) throws InterruptedException {
      // TODO Executor that keeps a fixed number (3) of threads until it is shut down
      // RISK: out of memory daca vin foarte multe taskuri gramada (spike)
//      ExecutorService executor = Executors.newFixedThreadPool(3);

      // TODO Executor that grows the thread pool as necessary,
      //     and kills inactive ones after 1 min
      // RISK: aloci prea multe threaduri (spike) => OutOfMemory/process hang
      // nu ai coada de asteptare, intri direct in lucru
//       ExecutorService executor = Executors.newCachedThreadPool();

      // TODO Executor that have at least min=3 thread but can grow up to max=10 threads,
      //  with a bounded queue of max 5 elements.
      //  Inactive threads kept alive for 1 second.
       ExecutorService executor = new ThreadPoolExecutor(
         3, 3, // fixed size = 3
         1, TimeUnit.SECONDS,
         new ArrayBlockingQueue<>(5),
        new ThreadPoolExecutor.CallerRunsPolicy() // o forma pritimiva de backpressure:
           // impingi munca inapoi in threadul care incearca sa te puna pe tine la munca.
           // -> incetinindu-l
           // Risk: sa STARVEZI thread pool-ul upstream. eg sa epuizezi toate 200 th din Tomcat
           // => rezultat: intarzieri in tratarea req http.
           // BIZ-CRITICAL endpoint: POST /place-bid
       );

      // TODO Vary the fixed-sized queue to see it grow the pool and then Rejecting tasks

      for (int i = 0; i < 40; i++) {
         MyTask task = new MyTask();
         log("Submitted new task #" + task.id);
         executor.submit(task); // acum uneori submit() dureaza 500 millis ca faci TU munca
         sleepSomeTime(100, 200); // simulate random request rate
      }
      executor.shutdown();
//      executor.shutdownNow();
      executor.awaitTermination(10, TimeUnit.SECONDS);
      log("Main iese");
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
