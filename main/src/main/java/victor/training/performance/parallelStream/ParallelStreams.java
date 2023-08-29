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
      Enemy.parallelRequest(); // demonstrates starvation of the shared commonPool

      long t0 = System.currentTimeMillis();

      List<Integer> listOfIds = IntStream.range(1,100).boxed().collect(toList());

     Stream<Integer> stream = listOfIds.parallelStream() // parallelStream a fost gandit sa mearga
         // pe CommonPool in ideea ca faci in el DOAR CPU (non-blocking work)
         .filter(i -> i % 2 == 0)
         .map(i -> {
           return fetchById(i); // blocheaza in parallelStream
         });

     ForkJoinPool pooluMeu = new ForkJoinPool(20); // scarbos, dar singurul mod
     // de a rula paralleStream pe pool dedicat
     List<Integer> result = pooluMeu.submit(()-> stream.collect(toList())).get();
      log.debug("Got result: " + result);

      long t1 = System.currentTimeMillis();
      log.debug("Took {} ms", t1 - t0);
   }

   // pe un proiect matur, se poate MASURA queue waiting time pe commonPool
  // sa iasa o metrica pe /actuator/prometheus

  private static int fetchById(Integer id) {
    log.debug("Fetching id=" + id);
    sleepMillis(100); // do some 'paralellizable' I/O work (DB, REST, SOAP)
    // dar asta nu OCUPA CPU, alte th ar putea sa lucreze.
    // ci doar blocheaza threadul dintr-un pool cu size limitat.
    return id * 2;
  }
}

