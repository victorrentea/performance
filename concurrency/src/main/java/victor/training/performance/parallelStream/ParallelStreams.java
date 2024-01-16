package victor.training.performance.parallelStream;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@Slf4j
public class ParallelStreams {
   public static void main(String[] args) {
//      OnAServer.otherParallelRequestsAreRunning(); // starve the shared commonPool

      List<Integer> list = IntStream.range(1,100).boxed().collect(toList());

      long t0 = System.currentTimeMillis();

      List<Integer> result = list.parallelStream()
          .filter(i -> i % 2 == 0)
          .map(i -> {
             log.debug("Map " + i);
             sleepMillis(100); // time-consuming work (CPU or DB, REST, SOAP)
             return i * 2; // pretend: return api.call(i);
          })
          .collect(toList()); // acum fluxul ruleaza pe 10-1 = 9th + main = 10 threaduri
     // munca dureaza 500ms nu 5000 ca la inceput
      log.debug("Got result: " + result);

      long t1 = System.currentTimeMillis();
      log.debug("Took {} ms", t1 - t0);
   }
}

