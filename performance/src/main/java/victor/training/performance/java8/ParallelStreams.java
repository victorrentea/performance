package victor.training.performance.java8;

import lombok.extern.slf4j.Slf4j;
import victor.training.performance.PerformanceUtil;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

@Slf4j
public class ParallelStreams {
   public static void main(String[] args) throws ExecutionException, InterruptedException {


      List<Integer> integers = asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

      Stream<Integer> stream = integers.parallelStream()
          .filter(n -> {
             log.debug("Filter {}", n);
             return n % 2 == 1;
          })
          .map(n -> {
             log.debug("Map {}", n);
             return n * 2;
          });


      ForkJoinPool pool = new ForkJoinPool(5);

      ForkJoinTask<List<Integer>> result = pool
          .submit(() -> stream.collect(Collectors.toList()));
      System.out.println(result.get());
   }
}
