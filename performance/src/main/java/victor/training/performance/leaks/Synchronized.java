package victor.training.performance.leaks;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import victor.training.performance.ConcurrencyUtil;

import javax.inject.Inject;

public class Synchronized {
   @Autowired
//       @EJB
//       @Inject

   public static void main(String[] args) {
      Alta alta = new Alta();

      new Thread(() -> alta.f()).start();
      new Thread(() -> alta.f()).start();

      System.out.println(alta.getTotal());

   }



}

@Slf4j
class Alta {

   private int total = 0;
   private static int dateStatice;

   public static synchronized void inc() {
      dateStatice++;
   }

   public synchronized void f() {
      log.info("Start");
      total++;
      ConcurrencyUtil.sleepq(1000);
      log.info("End");
   }

   public int getTotal() {
      return total;
   }
}
