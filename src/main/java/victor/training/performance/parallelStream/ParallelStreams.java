package victor.training.performance.parallelStream;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@Slf4j
public class ParallelStreams {
   public static void main(String[] args) {
      Enemy.parallelRequest(); // demonstrates starvation of the shared commonPool

      long t0 = System.currentTimeMillis();

      List<Integer> customerIds = IntStream.range(1,100).boxed().collect(toList());


      // aici : ForkJoinPool.commonPool se gasesc by default un nr de
      // N_logical_threads-1 threaduri = 7-11
      // de ce N = sa nu sufoce procesoarele daca ceea ce faci pe acele taskuri estee pure CPU work!!!
      // de ce -1 = sa lase loc si pentru stapanu (ala care vrea munca) = th care face .paralellStream
      // apropos, poti sa modifici size-ul din JVM de -D......=30
      List<Integer> result = customerIds.parallelStream()
          .filter(i -> {
             log.debug("Filter " + i);
             return i % 2 == 0;
          })
          .map(i -> {
             log.debug("Map " + i);

             // #asaNU
             sleepMillis(100); // do some 'paralellizable' I/O work (DB, REST, SOAP)
             return i * 2;
          })
          .collect(toList());
      log.debug("Got result: " + result);

      long t1 = System.currentTimeMillis();
      log.debug("Took {} ms", t1 - t0);
   }
}

// =========== far away, in a distant Package ...... =============
@Slf4j
class Enemy {
   @SneakyThrows
   public static void parallelRequest() {
      Thread thread = new Thread(Enemy::optimized);
      thread.setDaemon(true); // to exit program
      thread.start();
      Thread.sleep(100);
   }
   public static void optimized() {
      int result = IntStream.range(1, 1000)
          .parallel()
          .map(Enemy::callNetworkOrDB)
          .sum();
      System.out.println(result);
   }

   @SneakyThrows
   public static int callNetworkOrDB(int id) {
      log.debug("Blocking...");
      Thread.sleep(1000);
      return id*2;
   }

}