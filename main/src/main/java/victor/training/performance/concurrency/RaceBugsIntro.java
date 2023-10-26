package victor.training.performance.concurrency;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;


@Slf4j
public class RaceBugsIntro {
  private static List<Integer> evenNumbers = Collections.synchronizedList(new ArrayList<>());

  private static Integer total = 0;
  private static final Object mutex = new Object();
  private static final AtomicInteger totalAtomic = new AtomicInteger(0);

  // many parallel threads run this method:
  private static int countEven(List<Integer> numbers) {
    log.info("Start"); // 2 threads run this
    int localTotal = 0;
    for (Integer n : numbers) {
      if (n % 2 == 0) {
//        synchronized (total) { // not work as instance used in () is different every time
//          total = new Integer(total.intValue() + 1);
//        }
//        synchronized (mutex) {
//          total ++;
//        }
//        totalAtomic.incrementAndGet();
        localTotal++;
      }
    }
    log.info("end");
  return localTotal; // map-reduce style
  }

  public static void main(String[] args) throws ExecutionException, InterruptedException {
    List<Integer> fullList = IntStream.range(0, 10000).boxed().collect(toList());

    List<List<Integer>> lists = splitList(fullList, 2);
    List<Callable<Integer>> tasks = lists.stream().map(numbers -> (Callable<Integer>) () -> {
      return countEven(numbers);
    }).collect(toList());

    ExecutorService pool = Executors.newCachedThreadPool();
    pool.invokeAll(tasks);
    pool.shutdown();
//total = s1+s2
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