package victor.training.performance.pools;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.DiscardOldestPolicy;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static victor.training.performance.util.PerformanceUtil.*;

public class ThreadPools {

   public static void main(String[] args) throws InterruptedException {
      // TODO Executor that keeps a fixed number (3) of threads until it is shut down
//      ExecutorService executor = Executors.newFixedThreadPool(3); // large queue => OOME or intolerable waiting times
      // Dangerous for memory

      // How to decide queue size:
      // 1) how much mem can I afford
      // 2) how much latency my tasks can tolerate : waitingTimeInQueue = QueueSize * avgTaskTime / #threads

      // TODO Executor that grows the thread pool as necessary, and kills inactive ones after 1 min
//       ExecutorService executor = Executors.newCachedThreadPool();
      // what can go wrong ? >> many tasks at once ==> OS failure

      // TODO Executor that have at least 3 thread but can grow up to 10 threads,
      // with a queue of max 5 elements. Inactive threads die in 1 second.

      Executor decoratedExecutor = decorateExecutor(new ThreadPoolExecutor(
          3, 10,
          1, TimeUnit.SECONDS,
          new ArrayBlockingQueue<Runnable>(5),
          r -> {

             return new Thread(r);
          }
          ,

//          new CallerRunsPolicy()
          new DiscardOldestPolicy()
      ));

//      executor = new DeExecutorService() {
//      }
//      Executor decoratedExecutor = r -> executor.execute(decorate(r));

      // TODO Vary the fixed-sized queue to see it grow the pool and then Rejecting tasks

      for (int i = 0; i < 40; i++) {
         MyTask task = new MyTask();
         log("Submitted new task #" + task.id);
         decoratedExecutor.execute(task);
         sleepq(10);
//         sleepSomeTime(100, 200); // simulate random request rate
      }
      // TODO shutdown the executor !
   }

   private static Runnable decorate(Runnable task) {
      return () -> {
         try {
            task.run();
         } catch (Exception e) {
            e.printStackTrace();
            throw e;
         }
      };
   }

   private static Executor decorateExecutor(Executor executor) {
      return new Executor() {
//         MDC propage...
         @Override
         public void execute(Runnable r) {
            executor.execute(() -> {
               try {
                  r.run();
               } catch (Exception e) {
                  e.printStackTrace();
                  throw e;
               }
            });
         }
      };
   }
}

class MyTask extends LoggingRunnable {
   private static final AtomicInteger NEXT_ID = new AtomicInteger(0);
   public int id = NEXT_ID.incrementAndGet();

   public void doRun() {
      log("Start work item #" + id);
      sleepSomeTime(600, 800);
      if (randomInt(1, 2) == 1) {
         throw new RuntimeException("BANG!");
      }
      log("Finish work item #" + id);
//      return id;
   }
}


abstract class LoggingRunnable implements Runnable {
   @Override
   public final void run() {
      try {
         doRun();
      } catch (RuntimeException e) {
         e.printStackTrace();
         throw e;
      } catch (Exception e) {
         e.printStackTrace();
         throw new RuntimeException(e);
      }
   }

   abstract void doRun() throws Exception;
}