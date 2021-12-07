package victor.training.performance;

import lombok.extern.slf4j.Slf4j;
import victor.training.performance.util.PerformanceUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class ThreadLocals {
   public static ThreadLocal<String> currentUser = new ThreadLocal<>();

   static AtomicInteger counter = new AtomicInteger(0);

   public static void main(String[] args) {
      ExecutorService pool = Executors.newFixedThreadPool(10);
      Runnable task = () -> {
         currentUser.set("user" + counter.incrementAndGet());

         try {
            log.debug("Incep fluxul cu userul: " + currentUser.get());
            PerformanceUtil.sleepq(1);
            method();
         } finally {
            currentUser.remove(); // PATTERN: imediat dupa set pe un threa dlocal faci try {..} finally {remove}
         }
      };
      pool.submit(task);
      pool.submit(task);
      pool.submit(task);
      pool.submit(task);
      pool.submit(task);

//      SecurityContextHolder.getContext()
   }

   // Repository / DAO
   private static void method() {
      log.debug("INSERT INTO .. (..., CREATED_BY) VALUES (..., {})", currentUser.get());
   }
}
