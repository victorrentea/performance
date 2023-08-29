package victor.training.performance.concurrency;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;


@Slf4j
public class RaceBugsIntro {
  private static List<Integer> evenNumbers = new ArrayList<>();

  private static AtomicInteger total = new AtomicInteger();
  private static final Object lock = new Object();

  private static final List<Integer> pare = Collections.synchronizedList(new ArrayList<>());
//  private static final List<Integer> pare = new Vector<>(); // rau ca add la size mare costa

//  private static final List<Integer> pare = new ConcurrentSkipListSet<>();

  // many parallel threads run this method:
  private static void countEven(List<Integer> numbers) {
    log.info("Start");
    for (Integer n : numbers) {
      if (n % 2 == 0) {
        total.incrementAndGet();
         pare.add(n);
      }
    }
    log.info("end");
  }

  public static void main(String[] args) throws ExecutionException, InterruptedException {
    List<Integer> fullList = IntStream.range(0, 100_000).boxed().collect(toList());

    List<List<Integer>> lists = splitList(fullList, 2);
    List<Callable<Void>> tasks = lists.stream().map(numbers -> (Callable<Void>) () -> {
      countEven(numbers);
      return null;
    }).collect(toList());

    ExecutorService pool = Executors.newFixedThreadPool(2);
    pool.invokeAll(tasks);
    pool.shutdown();

    log.debug("Counted: " + total);
    log.debug("pare: " + pare.size());
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