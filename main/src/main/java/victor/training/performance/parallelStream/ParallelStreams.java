package victor.training.performance.parallelStream;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@Slf4j
public class ParallelStreams {
   public static void main(String[] args) {
      Enemy.parallelRequest(); // demonstrates starvation of the shared commonPool

      long t0 = System.currentTimeMillis();

      List<Integer> list = IntStream.range(1,100).boxed().collect(toList());

      List<Integer> result = list.parallelStream()
          // trimite elementele in executie in plus pe langa threadul caller
          // pe un threadpool GLOBAL☠️☠️☠️ in JVM ('ForkJoinPool.commonPool') care are exact
          // #CPU-1 theaduri in el (la mine = 9)
          .filter(i -> i % 2 == 0)
          .map(i -> {
             log.debug("Map " + i);
             // procesarea unui element in parte dureaza timp.
            // SA FACA CE?
            // A) CPU
            // B) I/O (db,api,soap,rmi,tcp)

            // NU face I/O in parallelStream, ci doar CPU work
            // asta e motivul pt care ForkJoinPool.commonPool are N-1 threaduri: sa faci CPU work

             sleepMillis(100); // do some 'paralellizable' I/O work (DB, REST, SOAP)
             return i * 2;
          })
          .collect(toList());
      log.debug("Got result: " + result);



      long t1 = System.currentTimeMillis();
      log.debug("Took {} ms", t1 - t0);
   }


}

