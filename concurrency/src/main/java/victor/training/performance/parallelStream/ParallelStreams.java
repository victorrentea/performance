package victor.training.performance.parallelStream;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;

import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@Slf4j
public class ParallelStreams {
  public static void main(String[] args) throws ExecutionException, InterruptedException {
    OnAServer.otherParallelRequestsAreRunning(); // starve the shared commonPool din JVM

    List<Integer> list = IntStream.rangeClosed(1, 100).boxed().toList();

    long t0 = System.currentTimeMillis();

    ForkJoinPool fjp = new ForkJoinPool(20);
    var result = fjp.submit(() -> list.parallelStream()
            .filter(i -> i % 2 == 0)
            .map(id -> networkCall(id))
            // runs on ForkJoinPool.commonCommon pool with 9 threads on vic's mac= 10CPU -1 (requestor)
            .toList()) // "terminate" a parallel stream in a FJP task
        .get();
    // knowing that the common pool has N_CPU - 1 threads,
    // what kind of tasks should you run on parallel stream?
    // => CPU-bound tasks!!! no i/o: DB/API call/Redis/Files

    long t1 = System.currentTimeMillis();
    log.debug("Took {} ms to get: {}", t1 - t0, result);
  }

  private static int networkCall(Integer i) {
    log.debug("Map " + i);
    sleepMillis(100); // network call (DB, REST, SOAP..) or CPU work
    return i * 2;
  }
}
