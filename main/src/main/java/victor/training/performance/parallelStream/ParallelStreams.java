package victor.training.performance.parallelStream;

import lombok.SneakyThrows;
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

      List<Integer> result = list.stream() // se poarta single thread, ca altu a fost mai BMW ca tine
          .filter(i -> {
             log.debug("Filter " + i);
             return i % 2 == 0;
          })
          .map(i -> {
             log.debug("Map " + i);
//             sleepMillis(100); // do some 'paralellizable' I/O work (DB, REST, SOAP)
            // ai blocat timp de 100ms 1 / 9 (nCPU-1) threaduri din commonPool global pe JVM

            // DE CE OARE au ales  commonPool sa aiba N-1 ?
            // pt ca pe commonPool (.parallelStream) tre sa faci munca de ...CPU
             return i * 2;
          })
          .collect(toList());
      // REGULA in commonPool (unde merge default parallelStream) NU AI VOIE RETEA. DB, API ,
      log.debug("Got result: " + result);

      long t1 = System.currentTimeMillis();
      log.debug("Took {} ms", t1 - t0);
   }
}

