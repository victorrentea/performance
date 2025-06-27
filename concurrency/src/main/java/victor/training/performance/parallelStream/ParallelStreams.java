package victor.training.performance.parallelStream;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@Slf4j
public class ParallelStreams {
  public static void main(String[] args) throws ExecutionException, InterruptedException {
    //
     OnAServer.otherParallelRequestsAreRunning(); // starve the shared commonPool din JVM

    List<Integer> list = IntStream.range(1, 100).boxed().toList();

    long t0 = System.currentTimeMillis();

    Stream<Integer> stream = list.parallelStream()
        // runs now in ForkJoinPool.commonPool. default size = 9 = #CPU-1(main)
        // main+9 fully saturate my 10 CPUs
        .filter(i -> i % 2 == 0)
        .map(i -> callApi(i));

    // to run a parallelStream on your own pool:
    ForkJoinPool myPool = new ForkJoinPool(4);

    // terminate your stream in a task running in this thread pool
    var result = myPool.submit(()->stream.toList()).get();

    long t1 = System.currentTimeMillis();
    log.debug("Took {} ms to get: {}", t1 - t0, result);
  }

  private static int callApi(Integer i) {
    log.debug("Map " + i);
    sleepMillis(100); // network call (DB, REST, SOAP..) or CPU work
    return i * 2;
  }
}
