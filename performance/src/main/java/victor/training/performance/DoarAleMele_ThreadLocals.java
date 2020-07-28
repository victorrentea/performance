package victor.training.performance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class DoarAleMele_ThreadLocals implements Runnable {
   private static final Logger log = LoggerFactory.getLogger(DoarAleMele_ThreadLocals.class);

   public static void main(String[] args) {
      ExecutorService pool = Executors.newFixedThreadPool(2);

      pool.submit(new DoarAleMele_ThreadLocals());
      pool.submit(new DoarAleMele_ThreadLocals());
   }

   static ThreadLocal<Integer> n = ThreadLocal.withInitial(() -> 0);

   @Override
   public void run() {
      n.set(n.get() + 1);
      ConcurrencyUtil.sleep2(1000);
      log.debug("Citesc: " + n.get());
   }
}
