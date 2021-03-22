package victor.training.performance.leaks;

import lombok.extern.slf4j.Slf4j;
import victor.training.performance.ConcurrencyUtil;

@Slf4j
public class ThreadLocals {

   static ThreadLocal<Integer> x = new ThreadLocal<>();
   public static void main(String[] args) {


      new Thread(() -> m(1)).start();

      new Thread(() -> m(2)).start();
   }

   private static void m(int currentUserId) {

      x.set(currentUserId);
      ConcurrencyUtil.sleepq(1000);
      saveCuAduti();
   }

   private static void saveCuAduti() {
      log.info("Out: LAST_MODIFIED_BY=" + x.get());
   }
}
