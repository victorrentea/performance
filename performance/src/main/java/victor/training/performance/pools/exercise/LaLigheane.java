package victor.training.performance.pools.exercise;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import victor.training.performance.ConcurrencyUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class LaLigheane {
   //   private static final Logger log = LoggerFactory.getLogger(LaLigheane.class);
   public static void main(String[] args) {

      ExecutorService pool1 = Executors.newSingleThreadExecutor();

      log.debug("Trimit comanda");
      Runnable r = new Runnable() {
         @Override
         public void run() {
            m();
         }
      };
      pool1.submit(r);
      log.debug("Am trimis comanda");

   }

   public static void m() {
      log.debug("Chestii");
      ConcurrencyUtil.sleep2(1000);
      log.debug("Gata!");
   }
}
