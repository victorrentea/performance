package victor.training.performance.pools;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static victor.training.performance.util.PerformanceUtil.log;
import static victor.training.performance.util.PerformanceUtil.sleepSomeTime;

public class ThreadPools {

   public static void main(String[] args) throws InterruptedException {
      // TODO Executor that keeps a fixed number (3) of threads until it is shut down

      // TODO Executor that grows the thread pool as necessary, and kills inactive ones after 1 min
      // ExecutorService executor = Executors. ?

      // TODO Executor that have at least 3 thread but can grow up to 10 threads,
      // with a queue of max 5 elements. Inactive threads die in 1 second.
      // ExecutorService executor = new ThreadPoolExecutor(...)

      // TODO Vary the fixed-sized queue to see it grow the pool and then Rejecting tasks

      // unbounded queue
//      ExecutorService executor = Executors.newSingleThreadExecutor(); // asteptam pan' la anu (@Adrian) + OOM
//      ExecutorService executor = Executors.newFixedThreadPool(4); // cel mai frecvent

      // unbounded thread #
//      ExecutorService executor = Executors.newCachedThreadPool(); // pericol: OS bum (prea multe threaduri)

      ExecutorService executor = new ThreadPoolExecutor(
              3, 4,
               1, TimeUnit.SECONDS,
              new ArrayBlockingQueue<>(5),
              new ThreadPoolExecutor.CallerRunsPolicy()
      );


      // de ce  nu FF multe threaduri:
      // 1) pierzi memorie degeaba ca oricum nu pot RULA SIMULTAN 800 (nu pot face CPU toate)
      // 2) pot sa moara cei pe care-i chemi. Clar nu faci CPU la tine, ca n-are sens sa ridici 800 daca ai 8 procesoare logice.
      // Deci vei face apeluri de network catre EI. EI o sa se supere daca vad 800 in parallel.
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
      sleepSomeTime(600, 800); // Aici te gandest i la Iulia apelu ei care dura 1 min. 800 in parallel. GET SELECT
      log("Finish work item #" + id);
      return id;
   }
}
