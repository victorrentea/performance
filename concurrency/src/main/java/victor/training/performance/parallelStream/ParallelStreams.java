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
//for() if
    Stream<Integer> integerStream = list.parallelStream() //🤔 DOS☢️
        .filter(i -> i % 2 == 0)
        .map(i -> { // runs on main + NCPU-1
          log.debug("Map " + i);
          apiCall();
          return i * 2;
        })
        /*.toList()*/;
    ForkJoinPool forkJoinPool = new ForkJoinPool(10);
    var result =
        forkJoinPool.submit(()->integerStream.toList())
            // 🤢🤮 terminate the parallel Stream in a task runnign in your own FJP
            .get(); // terminal operation

    long t1 = System.currentTimeMillis();
    log.debug("Took {} ms to get: {}", t1 - t0, result);
    //a) List<Future and in a for CompletableFuture.supplyAsync
  }

  private static void apiCall() {
    sleepMillis(100); // network call (DB, REST, SOAP..) or CPU work
  }
}
