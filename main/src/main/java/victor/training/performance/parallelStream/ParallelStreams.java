package victor.training.performance.parallelStream;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@Slf4j
public class ParallelStreams {
   public static void main(String[] args) {
//      Enemy.parallelRequest(); // demonstrates starvation of the shared commonPool

      long t0 = System.currentTimeMillis();

      List<Integer> ids = IntStream.range(1,100).boxed().collect(toList());

      List<Integer> result = ids.parallelStream() // runs in
          // global ForkJoinPool in JVM (size = NCPU-1)
          .filter(i -> i % 2 == 0)
          .map(i -> networkFetch(i))
          .collect(toList());
      log.debug("Got result: " + result);

      long t1 = System.currentTimeMillis();
      log.debug("Took {} ms", t1 - t0);
   }

  private static int networkFetch(Integer i) {
    log.debug("Map " + i);
    sleepMillis(100); // do some 'paralellizable' I/O work (DB, REST, SOAP)
    return i * 2;
  }
}

