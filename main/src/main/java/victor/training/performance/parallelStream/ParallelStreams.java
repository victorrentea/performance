package victor.training.performance.parallelStream;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@Slf4j
public class ParallelStreams {
   public static void main(String[] args) {
      SomeOtherUseCaseRunning.parallelRequest(); // demonstrates starvation of the shared commonPool

      long t0 = System.currentTimeMillis();
      List<Integer> ids = IntStream.range(1,100).boxed().collect(toList());

      List<Integer> result = ids.parallelStream()
          .filter(i -> {
             log.debug("Filter " + i);
             return i % 2 == 0;
          })
          .map(id -> {
             log.debug("Map " + id);
            sleepMillis(100); // do some 'paralellizable' CPU or I/O work (DB, REST, SOAP)
            return id * 2;
          })
          .collect(toList());
      log.debug("Got result: " + result);
 // parallelStream work on a shared thread pool (commonPool).size=CPU-1
     // it's easy to starve (it's small). you can impact the whole JVM (other flows) if you BLOCK in a parallelStream

     // what kind of work is intendended the commonPool for (if size = CPU-1) ? => CPU bound work
      long t1 = System.currentTimeMillis();
      log.debug("Took {} ms", t1 - t0);
   }

}

