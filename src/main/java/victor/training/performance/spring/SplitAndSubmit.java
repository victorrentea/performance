package victor.training.performance.spring;

import victor.training.performance.util.PerformanceUtil;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SplitAndSubmit {
   public static void main(String[] args) throws InterruptedException {


      List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

      List<Integer> part1 = numbers.subList(0, 5);
      List<Integer> part2 = numbers.subList(5, 10);

      ExecutorService pool = Executors.newFixedThreadPool(2);

      List<Callable<Integer>> workItems = List.of(() -> process(part1), () -> process(part2));

      List<Future<Integer>> futures = pool.invokeAll(workItems); // blocks

   //all futures all already solved.
//      futures.stream().map(Future::get).collect(Collectors.toList());


   }

   public static Integer process(List<Integer> list) {
      PerformanceUtil.sleepq(1000);
    return 1;
   }

}
