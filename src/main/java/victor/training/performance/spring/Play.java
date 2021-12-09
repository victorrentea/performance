package victor.training.performance.spring;

import victor.training.performance.util.PerformanceUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Play {
   public static void main(String[] args) {


      m();
   }

   private static void m() {

      int x = 2;
      ExecutorService pool = Executors.newFixedThreadPool(2);

      pool.submit(() -> {
         try {
            PerformanceUtil.sleepq(3000);
            System.out.println("referencing variable on stack of m()" + x);
         } catch (Exception e) {
            System.err.println("LOST BUT FOUND : " + e);
            throw e;
         }
      });
//      x ++ ;

//      pool.shutdownNow();
   }
}
