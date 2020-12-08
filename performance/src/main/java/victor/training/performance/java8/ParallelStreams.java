package victor.training.performance.java8;

import lombok.extern.slf4j.Slf4j;
import victor.training.performance.ConcurrencyUtil;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

@Slf4j
public class ParallelStreams {
   public static void main(String[] args) {
      List<Integer> numbers = IntStream.range(1, 50).boxed().collect(Collectors.toList());


      Stream<Integer> integerStream = numbers.parallelStream()
          .filter(n -> {
             log.debug("Filtering " + n);
//             ConcurrencyUtil.sleepq(1000);
             // SELECT
             System.out.println("I really need network!!");
             // HTTP
             // reader
             // sysncron
             return n % 2 == 1;
          })
          .sorted()
          .map(n -> {
             log.debug("Map " + n);
             return n * n;
          });


      ForkJoinPool fjp = new ForkJoinPool();

      fjp.submit(() -> integerStream.forEach(n -> log.debug("out : " + n)));
      ConcurrencyUtil.sleepq(10000);
   }
}
