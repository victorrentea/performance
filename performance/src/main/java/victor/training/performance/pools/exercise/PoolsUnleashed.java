package victor.training.performance.pools.exercise;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import victor.training.performance.ConcurrencyUtil;

import java.util.concurrent.*;

public class PoolsUnleashed {

   public static void main(String[] args) {

//      ExecutorService pool = Executors.newFixedThreadPool(1);
//      ExecutorService pool = Executors.newCachedThreadPool(); // pericol

//      avem 2 case deschise mereu,
//          dar daca esti a 5 persoana la coada,
//          deschidem o noua casa(dar avem max 4 case)
      ThreadPoolExecutor pool = new ThreadPoolExecutor(
          2, 4,
          5, TimeUnit.SECONDS,
          new ArrayBlockingQueue<>(5));

      System.out.println("Start");
      for (int i = 0; i < 9; i++) {
         pool.submit(new Cumparator(i));
      }
      System.out.println("Gata");
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
