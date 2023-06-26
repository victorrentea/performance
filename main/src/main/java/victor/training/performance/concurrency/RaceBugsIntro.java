package victor.training.performance.concurrency;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;


@Slf4j
public class RaceBugsIntro {
  private static List<Integer> evenNumbers = new ArrayList<>();

  // AtomicInteger
//  private static AtomicInteger total = new AtomicInteger(0);

  // many parallel threads run this method:
  private static int countEven(List<Integer> numbers) {
    log.info("Start");
    int total = 0; // Map-reduce: executi separat cat de mult poti fara sa mutezi date comune!
    for (Integer n : numbers) {
      if (n % 2 == 0) {
//        synchronized (RaceBugsIntro.class) {
//          total++;
//        }
//        total.incrementAndGet();
        total++;
      }
    }
    log.info("end");
    return total;
  }

  public static void main(String[] args) throws ExecutionException, InterruptedException {
    List<Integer> fullList = IntStream.range(0, 10_000).boxed().collect(toList());

    List<List<Integer>> lists = splitList(fullList, 2);
    List<Callable<Integer>> tasks = lists.stream()
        .map(numbers -> (Callable<Integer>) () -> countEven(numbers))
        .collect(toList());

    ExecutorService pool = Executors.newCachedThreadPool();
//    List<Future<Integer>> futures = pool.invokeAll(tasks);
    Future<Integer> t1 = pool.submit(tasks.get(0));
    Future<Integer> t2 = pool.submit(tasks.get(1));
    pool.shutdown();

//    futures.stream().map(Future::get).ct
    int total = t1.get() + t2.get();
    log.debug("Counted: " + total);
//    log.debug("Counted: " + evenNumbers.size());
  }

  //<editor-fold desc="splitList utility function">
  private static List<List<Integer>> splitList(List<Integer> all, int parts) {
    Collections.shuffle(all);
    List<List<Integer>> lists = new ArrayList<>();
    for (int i = 0; i < parts; i++) {
      lists.add(new ArrayList<>());
    }
    for (int i = 0; i < all.size(); i++) {
      lists.get(i % parts).add(all.get(i));
    }
    return lists;
  }
  //</editor-fold>


}