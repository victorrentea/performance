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
//     OnAServer.otherParallelRequestsAreRunning(); // starve the shared commonPool din JVM
    List<Integer> list = IntStream.range(1, 100).boxed().toList();

    long t0 = System.currentTimeMillis();

    Stream<Integer> stream = list.parallelStream()
        .filter(i -> i % 2 == 0)
        .map(i -> {
          log.debug("Map " + i);
          apiCall(i); // network call (DB, REST, SOAP..) or CPU work
          return i * 2;
        });



    // - use parallel stream only for CPU-bound work. not I/O.
    // - PLEASE check that you're improving performance. Don't guess, measure!
    // avoid race condition
    // eleemnts should be processed independently

    // if you reaally want parallelStreams to hit network, do this (safe):
    var myPool = new ForkJoinPool(4);
    // WARNIG: decorate the forkJoinPool with a custom executor that propages a ThreadLocals with the MDC/TraceId/SecurityContext
    // if you run the TERMINAL op of the stream in your own pool, you don't starve the commonPool
    var result = myPool.submit(() -> stream.toList()).get();

    long t1 = System.currentTimeMillis();
    log.debug("Took {} ms to get: {}", t1 - t0, result);
  }

  private static void apiCall(Integer i) {
    sleepMillis(100);
  }
}
