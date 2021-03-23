package victor.training.performance;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class APlusPlus {
   private static AtomicInteger population = new AtomicInteger(0);

   static final Map<Object, Object> hashMap = new ConcurrentHashMap<>();

   public static class ThreadA extends Thread {
      public void run() {
         int populationLocal = 0;
         for (int i = 0; i < 1000_000; i++) {
            populationLocal++;
            hashMap.put(i, "x");
//            hashMap.get(i);
         }
         population.addAndGet(populationLocal);
      }
   }

   public static class ThreadB extends Thread {
      public void run() {
         int populationLocal = 0;
         for (int i = 0; i < 1000_000; i++) {
            populationLocal++;
            if (hashMap.containsKey(i)) {
               System.out.println("Hit!");
            }
         }
         population.addAndGet(populationLocal);
      }
   }

   // TODO (bonus): ConcurrencyUtil.useCPU(1)
   // TODO (extra bonus): Analyze with JFR

   public static void main(String[] args) throws InterruptedException {
      ThreadA threadA = new ThreadA();
      ThreadB threadB = new ThreadB();

      long t0 = System.currentTimeMillis();

      threadA.start();
      threadB.start();
      threadA.join();
      threadB.join();

      long t1 = System.currentTimeMillis();
      System.out.println("Total = " + population);
      System.out.println("Took = " + (t1 - t0));
   }
}
