package victor.training.performance.concurrency;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;


@Slf4j
public class RaceBugsIntro {
  private static final Object MUTEX = new Object(); // SOC: e folosit doar ca obiect pe care sa te sincronzezi
  private static List<Integer> evenNumbers = new ArrayList<>();

  // excelent pt a genera id-uri noi, secvente in memorie, sau a face sume
//  AtomicLong
//  AtomicReference
  private static AtomicInteger total = new AtomicInteger(0);

  // many parallel threads run this method:
  private static void countEven(List<Integer> numbers) {
    log.info("Start");
    for (Integer n : numbers) {
      if (n % 2 == 0) {
//        synchronized (MUTEX) {
//          total++;
//        }
        total.incrementAndGet(); // spilu e ca foloseste o instruct de CPU low level
      }
    }
    log.info("end");
  }

  public static void main(String[] args) throws ExecutionException, InterruptedException {
    List<Integer> fullList = IntStream.range(0, 10_000).boxed().collect(toList());
    // 5000 nr pare

    List<List<Integer>> lists = splitList(fullList, 2);
    List<Callable<Void>> tasks = lists.stream().map(numbers -> (Callable<Void>) () -> {
      countEven(numbers);
      return null;
    }).collect(toList());

    ExecutorService pool = Executors.newCachedThreadPool();
    pool.invokeAll(tasks);
    pool.shutdown();

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