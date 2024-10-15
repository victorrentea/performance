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
    OnAServer.otherParallelRequestsAreRunning(); // starve the shared commonPool din JVM

    List<Integer> list = IntStream.range(1, 100).boxed().toList();
    long t0 = System.currentTimeMillis();

    Stream<Integer> stream = list.parallelStream()
        .filter(i -> i % 2 == 0)
        .map(i -> {
          log.debug("Map " + i);
          sleepMillis(100); // network call (DB, REST, SOAP..) or CPU work
          return i * 2;
        });
    ForkJoinPool pool = new ForkJoinPool(20);
    var result = pool.submit(() -> stream.toList()).get();
    // DECI: evita sa faci I/O (blocari) in taskuri pe parallelStream
    // ca sa nu blochezi munca altora care fac si ei parallelStream
    // pe parallelStream faci doar munca CPU

    long t1 = System.currentTimeMillis();
    log.debug("Took {} ms to get: {}", t1 - t0, result);
  }
}
