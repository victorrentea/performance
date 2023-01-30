package victor.training.performance.parallelStream;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@Slf4j
public class ParallelStreams {
   public static void main(String[] args) {
      Enemy.parallelRequest(); // demonstrates starvation of the shared commonPool

      long t0 = System.currentTimeMillis();

      List<Integer> list = IntStream.range(1,100).boxed().collect(toList());

     // moral of the story : don't use parallelStream UNLESS FOR PURELY CPU WORK
      // because doing IO in a shared pool with unknown flows blocks OTHERS/you.

      // parallelStream by default runs your tasks on a special ThreadPool global per JVM
      // called ForkJoinPool.commonPool that has exactly Ncpu-1 threads.
      // in my case: 10 cpu => pool size = 9 => 5 sec / 10 (main+9) = 0.6 s
      List<Integer> result = list.parallelStream()
          .filter(i -> {
             log.debug("Filter " + i);
             return i % 2 == 0;
          })
          .map(i -> {
             log.debug("Map " + i);
             sleepMillis(100); // REST get do some 'paralellizable' I/O work (DB, REST, SOAP)
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