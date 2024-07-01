package victor.training.performance.parallelStream;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@Slf4j
public class ParallelStreams {
  public static void main(String[] args) throws ExecutionException, InterruptedException {
    // OnAServer.otherParallelRequestsAreRunning(); // starve the shared commonPool din JVM

    List<Integer> list = IntStream.range(1, 100).boxed().toList();

    long t0 = System.currentTimeMillis();

    var result = list.parallelStream()
        .filter(i -> i % 2 == 0)
        .map(i -> {
          log.info("Calling " +  i);
          sleepMillis(100); // I/O
          return i * 2;
        })
        .toList();

    long t1 = System.currentTimeMillis();
    log.debug("Took {} ms to get: {}", t1 - t0, result);
  }
}

// Probleme cu parallelStream:
// 1) parallelSream in parallelStream = crash!
//config.exceptions.ExceptionControllerAdvice - handleException(): An unexpected exception occurred!java.util.concurrent.RejectedExecutionException: Thread limit exceeded replacing blocked worker
