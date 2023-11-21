package victor.training.performance.parallelStream;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@Slf4j
public class ParallelStreams {
  //  static int abominatie = 0;
  public static void main(String[] args) throws ExecutionException, InterruptedException {
    Enemy.parallelRequests(); // demonstrates starvation of the shared commonPool

    long t0 = System.currentTimeMillis();

    List<Integer> list = IntStream.range(1, 100).boxed().collect(toList());

    ForkJoinPool myfjp = new ForkJoinPool(50);

    // si main() lucreaza, de aia FJP.commonPool.size=NCPU-1(main)
//      List<Integer> result = list.parallelStream() // 500 ms
    List<Integer> result = myfjp.submit(() ->
        list.parallelStream() // 5000 ms
            .filter(i -> i % 2 == 0)
            .map(i -> {
              log.debug("Map " + i);
              sleepMillis(100); // do some 'paralellizable' I/O work (DB, REST, SOAP)
              return i * 2;
            })
            .collect(toList())).get();
    log.debug("Got result: " + result);

    long t1 = System.currentTimeMillis();
    log.debug("Took {} ms", t1 - t0);
  }
}

