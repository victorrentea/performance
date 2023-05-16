package victor.training.performance.concurrency.primitives;

import victor.training.spring.batch.util.PerformanceUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ConcurrentMap {
   private static final Map<Integer, String> map = new ConcurrentHashMap<>();
   public static void main(String[] args) {
      ExecutorService pool = Executors.newCachedThreadPool();
      for (int i = 0; i <100 ; i++) {
         pool.submit(ConcurrentMap::add);
      }
      PerformanceUtil.sleepMillis(10);
      for (int i = 0; i < 5; i++) {
         pool.submit(ConcurrentMap::read);
      }
   }

   private static void read() {
      int found =0, not=0;
      for (int i = 0; i < 10_000; i++) {
//         PerformanceUtil.sleepMillis(1); // this increases the hit rate
         String v = map.get(c.get() - 1);
         if (v == null) {
            not ++;
         } else {
            found++;
         }
      }
      System.out.println("FOUND:" + found + " NOT:"+not + " hitRate:" + (1.0 * found/ (found + not)));
   }

   private static final AtomicInteger c = new AtomicInteger(0);
   private static void add() {
      for (int i = 0; i < 10_000; i++) {
         map.put(c.incrementAndGet(), "X");
      }
   }

}
