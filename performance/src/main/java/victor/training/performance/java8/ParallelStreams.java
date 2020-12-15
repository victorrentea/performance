package victor.training.performance.java8;

import victor.training.performance.ConcurrencyUtil;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static victor.training.performance.ConcurrencyUtil.*;

public class ParallelStreams {
   public static void main(String[] args) throws ExecutionException, InterruptedException {
      List<Integer> numbers = IntStream.range(1, 50).boxed().collect(toList());


      Stream<Integer> stream = numbers.parallelStream()
          .filter(n -> {
             log("Filter " + n);
             // DB, IO/ NET/ API/ File
             return n % 2 == 1;
          })
//          .sorted()
          .map(n -> {
             log("Map " + n);
             return n * n;
          })
          .flatMap(n -> m(n));

      ForkJoinPool pool = new ForkJoinPool(4);

      List<Integer> nums = pool.submit(() -> stream.collect(toList())).get();


      System.out.println(nums);

   }

   private static Stream<Integer> m(Integer n) {
      return IntStream.range(n,n+1).boxed().parallel().peek(e -> log("Peek " + e));
   }
}
