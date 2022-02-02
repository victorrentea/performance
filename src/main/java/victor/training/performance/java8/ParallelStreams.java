package victor.training.performance.java8;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

@Slf4j
public class ParallelStreams {
   public static void main(String[] args) {

      List<Integer> numbers = IntStream.range(1,10_000).boxed().collect(toList());
          //Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

      // parallelStream ruleaza by default pe un [ForkJoinPool.commonPool comun in tot JVM cu NCORE-1 threaduri
      // care e pericolul ?
//CompletableFuture.sup

      List<Integer> list = numbers.parallelStream()
          .filter(n -> {
             log.debug("Filter {}", n);
//             REST calll blocheaza 1/11 threaduri globale (dc ai 12 core logice)
             // NU IO in parallelStream. DOAR CPU faci. eg crypto, jpg, generi grafice, pdf, xslt
             return n % 2 == 1;
          })
          .map(n -> {
             log.debug("Map {}", n);
             return n * n;
          })
          .collect(toList());

      System.out.println(list);
//          .forEach(x -> log.debug("OUT: " + x));
   }
}
