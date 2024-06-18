package victor.training.performance.parallelStream;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@Slf4j
public class ParallelStreams {
  public static void main(String[] args) throws ExecutionException, InterruptedException {
//    OnAServer.otherParallelRequestsAreRunning(); // starve the shared commonPool din JVM

    List<Integer> list = IntStream.range(1, 100).boxed().collect(toList());

    long t0 = System.currentTimeMillis();

    List<Integer> result = list.stream()
        .filter(i -> i % 2 == 0)
        .map(i -> {
          log.info("Processing " + i);
          sleepMillis(100); // network call (IO)
          return i * 2;
        })
        .toList();
    log.debug("Got result: " + result);

    long t1 = System.currentTimeMillis();
    log.debug("Took {} ms", t1 - t0);
  }
}

// concluzii:
// - pe parallelStream ar trebui sa fac doar calcule cu CPU in memory fara sa ating reteaua. eg: transformate XML, generezi pdf,
// - atentie sa masori inainte cat castigi, ca poate nu merita parallelStream. fa-ti benchmark cu JMH
//    Branch: main on git: https://github.com/victorrentea/performance-jmh.git


