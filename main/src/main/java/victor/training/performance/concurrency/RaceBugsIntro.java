package victor.training.performance.concurrency;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;


@Slf4j
public class RaceBugsIntro {
  private static List<Integer> evenNumbers = Collections.synchronizedList(new ArrayList<>());

//  private static AtomicInteger total = new AtomicInteger(0);

  // 2 parallel threads run this method with [1..5000], [5001..10000]
  private static int countEven(List<Integer> numbers) {
    log.info("Start");
    int myTotal = 0;
    for (Integer n : numbers) {
      if (n % 2 == 0) {
//        synchronized (RaceBugsIntro.class) {
//          m();
//        }
//        total.incrementAndGet();
        myTotal++;
        evenNumbers.add(n);
      }
    }
    log.info("end");
    return myTotal; // FUNCTIONAL PROGRAMMING IS MUST-HAVE FOR MULTITHREADING
  }
  // map-reduce = imparti munca in parti independente, le executi fara syncronizare separat, si abia la final unesti rezultate

  private static void m() {
//    PerformanceUtil.sleepMillis(7); // risk: sa pui blocari in synchronized repo.find/api.call
//    total++;
  }

  public static void main(String[] args) throws ExecutionException, InterruptedException {
    List<Integer> fullList = IntStream.range(0, 10_000).boxed().collect(toList());

    List<List<Integer>> lists = splitList(fullList, 2);
    List<Callable<Integer>> tasks = lists.stream().map(numbers -> (Callable<Integer>) () -> {
      return countEven(numbers);
    }).collect(toList());

    ExecutorService pool = Executors.newCachedThreadPool();
    List<Future<Integer>> futures = pool.invokeAll(tasks);
    pool.shutdown();

    int total = futures.get(0).get() + futures.get(1).get();
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