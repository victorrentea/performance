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

        // parallelStream is good for CPU-bound tasks, not for I/O-bound tasks
//    ForkJoinPool.commonPool() has 9 threads = #CPU - 1, global, shared JVM - wide

    Stream<Integer> stream = list.parallelStream()
        .filter(i -> i % 2 == 0) // 50 left
        .map(ParallelStreams::fetchFromRemote);

//    var yourForkJoinPool = new ForkJoinPool(); // default #CPU : Q: if you're doing CPU work, please sumbit to the commonPool


    // NEVER USE PARALLEL STREAM BEFORE creating a wiki page listing measurements from PRODUCTION
    // Measure, Don't Guess.
    // eg : before: 12ms after 20ms => DON'T DO IT - BAD
    // eg : before: 12ms after 10ms => DON'T DO IT - IRRELEVANT
    // eg : before: 100ms after 20ms => consider ? is it relevant for my users/SLA
    // eg : before: 2s after 200ms => YES

    // vs risks of :
    // -Loosing Thread Locals/MDC ?
    // -race conditions
    // -deadlocks
    var yourForkJoinPool = new ForkJoinPool(10);

    //terminal operation of the stream must run in your ForkJoinPool
    var result = yourForkJoinPool.submit(stream::toList).get();

    // ALWAYS PREFER TO USE A BULK-ENDPOINT when calling over network (SELECT, GET...)

    /
    long t1 = System.currentTimeMillis();
    log.debug("Took {} ms to get: {}", t1 - t0, result);
  }

  private static int fetchFromRemote(Integer i) {
    log.debug("Map " + i);
    sleepMillis(100); // network call (DB, REST, SOAP..) or CPU work
    return i * 2;
  }
}
