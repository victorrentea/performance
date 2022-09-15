package victor.training.performance.java8;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static victor.training.performance.util.PerformanceUtil.sleepq;

@Slf4j
public class ParallelStreams {
   public static void main(String[] args) {
      EnemyDev.parallelRequest(); // demonstrates starvation of the shared commonPool

      long t0 = System.currentTimeMillis();
// 100 items sequential = 5130
// 100 items parallel = 600
// 100 items parallel but starved by other = 5128
      // conclusion: don't do network in parallelStream, but only CPU work. That's why it's sized threads=NCPU-1
      // !!! FIRST PLEASE CHECK THAT YOUR WORK IS HEAVY ENOUGH TO DESERVE THE RISK OF PARALELIZATION
      // race bugs, deadlocks,
      // loss of SecurityContextHolder= (there is a fix, see baeldung.com)
      // loss of @Transactional
      // loss of Logback MDC
      List<Integer> list = IntStream.range(1,100).boxed().collect(toList());

      List<Integer> result = list.parallelStream()
          .filter(i -> {
             log.debug("Filter " + i);
             return i % 2 == 0;
          })
          .map(i -> {
             log.debug("Map " + i);
             sleepq(100); // do some 'paralellizable' I/O work (DB, REST, SOAP)
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
class EnemyDev {
   @SneakyThrows
   public static void parallelRequest() {
      Thread thread = new Thread(EnemyDev::optimized);
      thread.setDaemon(true); // to exit program
      thread.start();
      Thread.sleep(100);
   }
   public static void optimized() {
      int result = IntStream.range(1, 1000)
          .parallel()
          .map(EnemyDev::callNetworkOrDB)
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