package victor.training.performance.concurrency;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;


@Slf4j
public class RaceBugsIntro {
  private static final List<Integer> evenNumbers = Collections.synchronizedList(new ArrayList<>());
  private static final AtomicInteger total = new AtomicInteger(0);
  private static final Object lock = new Object();

  // many parallel threads run this method:
  // am facut functia PURA (fara side effects). produce date, nu modifica
  private static List<Integer> findEvenNumber(List<Integer> numbers) {
    log.info("Start");
    List<Integer> results = new ArrayList<>();
    for (Integer n : numbers) {
      if (n % 2 == 0) {
//        System.out.println("Heisenbug[haisamibag]");// il pui si trece bugu. #haisamibag
//      synchronized (lock) {
//        total++;// e de fapt        total = Integer.valueOf(total.intValue() + 1);
//      }
//        total.incrementAndGet();
//        synchronized (evenNumbers) {
//          evenNumbers.add(n);
//        }
//        evenNumbers.add(n);
//        Thread.sleep(10); // #haisamibag
        results.add(n);
      }
    }
    log.info("end");
    return results;
  }

  public static void main(String[] args) throws ExecutionException, InterruptedException {
    List<Integer> fullList = IntStream.range(0, 10_000).boxed().collect(toList());

    List<List<Integer>> lists = splitList(fullList, 2);
    List<Callable<List<Integer>>> tasks = lists.stream()
        .map(numbers -> (Callable<List<Integer>>)
            () -> findEvenNumber(numbers)).collect(toList());

    ExecutorService pool = Executors.newCachedThreadPool();
    List<Future<List<Integer>>> viituri = pool.invokeAll(tasks);
    for (Future<List<Integer>> viitor : viituri) {
      evenNumbers.addAll(viitor.get());
    }
    pool.shutdown();

    log.debug("Counted: " + total);
    log.debug("Counted: " + evenNumbers.size());
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