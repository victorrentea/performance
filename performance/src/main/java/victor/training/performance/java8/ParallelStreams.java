package victor.training.performance.java8;

import victor.training.performance.ConcurrencyUtil;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static victor.training.performance.ConcurrencyUtil.*;

public class ParallelStreams {
   public static void main(String[] args) {
      List<Integer> numbers = IntStream.range(1, 50).boxed().collect(toList());


      numbers.parallelStream()
          .filter(n -> {
             log("Filter " +n);
             return n % 2 == 1;
          })
          .parallel()
          .map(n -> {
             log("Map " +n);
             return n * n;
          })
          .flatMap(n -> m(n))
          .sequential()
          .forEach(x -> {
             System.out.println("Out " +x);
          });

   }

   private static Stream<Integer> m(Integer n) {
      return IntStream.range(1,2).boxed().parallel().peek(e -> log("Peek " + e));
   }
}
