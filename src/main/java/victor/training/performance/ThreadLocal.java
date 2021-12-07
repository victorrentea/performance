package victor.training.performance;

import lombok.extern.slf4j.Slf4j;
import victor.training.performance.util.PerformanceUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class ThreadLocal {
   public static String currentUser;

   static AtomicInteger counter = new AtomicInteger(0);

   public static void main(String[] args) {
      ExecutorService pool = Executors.newFixedThreadPool(10);

      Runnable task = () -> {

         currentUser = "user" + counter.incrementAndGet();

         log.debug("Incep fluxul cu userul: " + currentUser);
         PerformanceUtil.sleepq(1);
         method();

      };
      pool.submit(task);
      pool.submit(task);
      pool.submit(task);
      pool.submit(task);
      pool.submit(task);
   }

   // Repository / DAO
   private static void method() {
      log.debug("INSERT INTO .. (..., CREATED_BY) VALUES (..., {})", currentUser);
   }
}
