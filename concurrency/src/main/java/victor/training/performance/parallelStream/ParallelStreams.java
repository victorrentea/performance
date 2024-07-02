package victor.training.performance.parallelStream;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
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
          log.info("Calling " + i);
          sleepMillis(100); // I/O
          return i * 2;
        });
    // imi sap singur piscina. urat. si greu.
    ForkJoinPool pool = new ForkJoinPool(20);
    var result = pool.submit(() -> stream.toList()).get();

    // NB: de ce?
    // ca sa faci I/O pe mai multe threaduri? da nu ti-e mila de ala in care dai?


    long t1 = System.currentTimeMillis();
    log.debug("Took {} ms to get: {}", t1 - t0, result);
  }
}

// Probleme cu parallelStream:
// 1) parallelSream in parallelStream = crash!
//config.exceptions.ExceptionControllerAdvice - handleException(): An unexpected exception occurred!java.util.concurrent.RejectedExecutionException: Thread limit exceeded replacing blocked worker
// 2) evita I/O in ForkJoinPool.commonPool: parallelStream, sau CompletableFuture.supplyAsync() (fara param executor)