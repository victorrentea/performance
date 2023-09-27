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

      List<Integer> list = IntStream.range(1,50).boxed().collect(toList());

      // runs on a default "ForkJoinPool.commonPool" global per JVM with size  N threads = N CPU-1
      List<Integer> result = list.parallelStream()
          .map(i -> apiCall(i))
          .collect(toList());
      log.debug("Got result: " + result);

      long t1 = System.currentTimeMillis();
      log.debug("Took {} ms", t1 - t0);
   }

  private static int apiCall(Integer i) {
    log.debug("Map " + i);
    sleepMillis(100); // do some 'paralellizable' I/O work (DB, REST, SOAP)
    return i * 2;
  }
}

