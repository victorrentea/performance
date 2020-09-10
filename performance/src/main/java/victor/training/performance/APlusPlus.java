package victor.training.performance;

import java.util.Collections;

public class APlusPlus {
   private static Integer population = 0;
   private static final Object MONITOR = new Object();

   public static class ThreadA extends Thread {
      public void run() {
         for (int i = 0; i < 5_000; i++) {
            ConcurrencyUtil.sleepq(1); // DB call , HTTP call.
            // START TRANSACTION
            //SELECT FOR UPDATE :) --- lock a ROW in the database for the duration of the current Tx
            // any other Tx trying to do SELECT FOR UPDATE will HANG until your Tx terminates
            synchronized (MONITOR) {
               population++;
            }
            // COMMIT
         }
      }
   }

   public static class ThreadB extends Thread {
      public void run() {
         for (int i = 0; i < 5_000; i++) {
            ConcurrencyUtil.sleepq(1);
            synchronized (MONITOR) {
               population++;

            }
         }
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
