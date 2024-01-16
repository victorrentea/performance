package victor.training.performance.parallelStream;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@Slf4j
public class ParallelStreams {
   public static void main(String[] args) throws ExecutionException, InterruptedException {
      OnAServer.otherParallelRequestsAreRunning(); // starve the shared commonPool din JVM

      List<Integer> list = IntStream.range(1,100).boxed().collect(toList());

      long t0 = System.currentTimeMillis();

     Stream<Integer> stream = list.parallelStream()
         .filter(i -> i % 2 == 0)
         .map(i -> {
           log.debug("Map " + i);
           sleepMillis(100); // time-consuming work (CPU or DB, REST, SOAP)
           return i * 2; // pretend: return api.call(i);
         });

     // cum rulez parallelStream pe thread pool privat al meu
     ForkJoinPool forkJoinPool = new ForkJoinPool(16);
     List<Integer> result = forkJoinPool.submit(
         () -> stream.collect(toList())).get();
     // acum fluxul ruleaza pe 10-1 = 9th + main = 10 threaduri
     // munca dureaza 500ms nu 5000 ca la inceput
      log.debug("Got result: " + result);

      long t1 = System.currentTimeMillis();
      log.debug("Took {} ms", t1 - t0);
   }
}

// concluzii:
// - pe parallelStream ar trebui sa fac doar calcule cu CPU in memory fara sa ating reteaua. eg: transformate XML, generezi pdf,


