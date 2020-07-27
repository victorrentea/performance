package victor.training.performance.pools.exercise;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import victor.training.performance.ConcurrencyUtil;

import java.util.concurrent.*;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;

public class PoolsUnleashed {
   private static final Logger log = LoggerFactory.getLogger(PoolsUnleashed.class);

   public static void main(String[] args) {

//      ExecutorService pool = Executors.newFixedThreadPool(1);
//      ExecutorService pool = Executors.newCachedThreadPool(); // pericol

//      avem 2 case deschise mereu,
//          dar daca esti a 5 persoana la coada,
//          deschidem o noua casa(dar avem max 4 case)
      ThreadPoolExecutor pool = new ThreadPoolExecutor(
          2, 4,
          5, TimeUnit.SECONDS,
          new ArrayBlockingQueue<>(5),
          new CallerRunsPolicy());

      log.debug("Start");
      for (int i = 0; i < 11; i++) {
         log.debug("Submit " +i);
         pool.submit(new Cumparator(i));
         log.debug("Am submis " +i);
      }
      log.debug("Gata");
   }
}

class Cumparator implements Runnable {
   private static final Logger log = LoggerFactory.getLogger(Cumparator.class);
   private final int id;
   Cumparator(int id) {
      this.id = id;
   }
   @Override
   public void run() {
     log.debug("Task" + id);
     ConcurrencyUtil.sleep2(100);
   }
}
