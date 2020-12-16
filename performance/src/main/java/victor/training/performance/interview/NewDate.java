package victor.training.performance.interview;

import org.jooq.lambda.Unchecked;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class NewDate {

   public static void main(String[] args) throws InterruptedException {
      ExecutorService pool = Executors.newFixedThreadPool(50);
      long t0 = System.currentTimeMillis();
      List<Future<Long>> futures = pool.invokeAll(IntStream.range(0, 50).mapToObj(Task::new).collect(toList()));
      long sum = futures.stream().mapToLong(Unchecked.toLongFunction(Future::get)).sum();
      long t1 = System.currentTimeMillis();
      System.out.println("Took " + (t1-t0));
      System.out.println(sum);
      pool.shutdown();
   }
}

class Task implements Callable<Long> {
   private final int id;

   Task(int id) {
      this.id = id;
   }

   @Override
   public Long call() throws Exception {
      long x = 0;
      for (int i = 0; i < 1000_000; i++) {
         x += logic();
      }
      return x;
   }

   private long logic() {
      long date = newDate();
      date = math(date);
      return date;
   }

   private long math(long date) {
      for (int i = 0; i < 5; i++) {
         date = date * date;
      }
      return date;
   }

   private long newDate() {
      return new Date().getTime();
   }
}
