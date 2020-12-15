package victor.training.performance.pools;

import org.jooq.lambda.Unchecked;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static victor.training.performance.ConcurrencyUtil.log;
import static victor.training.performance.ConcurrencyUtil.sleepSomeTime;

public class ThreadPools {

   public static void main(String[] args) throws InterruptedException, ExecutionException {
      // TODO Executor that keeps a fixed number (3) of threads until it is shut down
//      ExecutorService executor = Executors.newFixedThreadPool(3);

      // TODO Executor that grows the thread pool as necessary, and kills inactive ones after 1 min
//       ExecutorService executor = Executors.newCachedThreadPool();

      // TODO Executor that have at least 3 thread but can grow up to 10 threads. Inactive threads die in 1 second.
      // TODO Vary the fixed-sized queue to see it grow the pool and then Rejecting tasks
      ThreadPoolExecutor executor = new ThreadPoolExecutor(
          3, 5,
          1, TimeUnit.SECONDS,
          new ArrayBlockingQueue<>(4),
          new CallerRunsPolicy());

      List<Future<Integer>> futures = new ArrayList<>();

      for (int i = 0; i < 40; i++) {
         MyTask task = new MyTask();
         log("Submitted new task #" + task.id);
         Future<Integer> futureInteger = executor.submit(task);
         futures.add(futureInteger);
         sleepSomeTime(100, 200); // simulate random request rate
      }

//      Future<?> future = executor.submit(new Runnable() {
//         @Override
//         public void run() {
//            log("#sieu");
//            throw new IllegalArgumentException("Bum frate!!");
//         }
//      });
      System.out.println("Am trimis tot");

      List<Integer> results = new ArrayList<>();
//      for (Future<Integer> future : futures) {
//         results.add(future.get());
//      }

      results = futures.stream().map(Unchecked.function(Future::get)).collect(Collectors.toList());
      System.out.println(results);

//      future.get();
      // TODO shutdown the executor !
   }
}

class MyTask implements Callable<Integer> {
   private static final AtomicInteger NEXT_ID = new AtomicInteger(0);
   public int id = NEXT_ID.incrementAndGet();
//   int i =0;

   public Integer call() {
      log("Start work item #" + id);
      sleepSomeTime(600, 800);
//      i++;
      log("Finish work item #" + id + " ") ;
      return id * 2;
   }
}
