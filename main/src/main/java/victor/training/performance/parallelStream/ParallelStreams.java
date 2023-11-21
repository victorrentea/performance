package victor.training.performance.parallelStream;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@Slf4j
public class ParallelStreams {
//  static int abominatie = 0;
   public static void main(String[] args) {
//      Enemy.parallelRequest(); // demonstrates starvation of the shared commonPool

      long t0 = System.currentTimeMillis();

      List<Integer> list = IntStream.range(1,100).boxed().collect(toList());

      // si main() lucreaza, de aia FJP.commonPool.size=NCPU-1(main)
//      List<Integer> result = list.parallelStream() // 500 ms
      List<Integer> result = list.stream() // 5000 ms
          .filter(i -> i % 2 == 0)
          .map(i -> i * 2)
          .collect(toList());
      log.debug("Got result: " + result);

      long t1 = System.currentTimeMillis();
      log.debug("Took {} ms", t1 - t0);
   }
}

