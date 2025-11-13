package victor.training.performance;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static java.util.concurrent.TimeUnit.MINUTES;


@SuppressWarnings("ALL")
@Slf4j
public class RaceBugs {

  private static AtomicInteger total = new AtomicInteger(0);
  private static List<Integer> even = Collections.synchronizedList(new ArrayList<>());
//  private static Integer total = 0;
//  private static final Object LOCK = new Object();
  // many parallel threads run this method:
  private static List<Integer> countEven(List<Integer> numbers) {
    log.info("Start");
    List<Integer> myResults = new ArrayList<>();
    for (Integer n : numbers) {
      if (n % 2 == 0) {
        myResults.add(n);
      }
    }
    log.info("End");
    return myResults;
  }

  public static void main(String[] args) throws Exception {
    List<Integer> fullList = IntStream.range(0, 10000).boxed().toList();
    // split in [[1..500],[501..1000]]
    List<List<Integer>> parts = splitList(fullList, 4);

    ExecutorService pool = Executors.newCachedThreadPool();
    List<Future<List<Integer>>> futures = new ArrayList<>();
    for (List<Integer> part : parts) {
      Future<List<Integer>> future = pool.submit(()-> countEven(part));
      futures.add(future);
//      future.get();// blocks main thread until the work is done => all work happens sequentially
    }
    List<Integer> finalResults = new ArrayList<>();
    for (Future<List<Integer>> future : futures) {
      List<Integer> partialResults = future.get();
      finalResults.addAll(partialResults);
    }

    pool.shutdown();
    pool.awaitTermination(1, MINUTES);

    log.debug("Counted: " + total);
    log.debug("List.size: " + finalResults.size());
  }

  //<editor-fold desc="utility functions">
  private static List<List<Integer>> splitList(List<Integer> all, int numberOfParts) {
    List<Integer> shuffled = new ArrayList<>(all);
    Collections.shuffle(shuffled);
    List<List<Integer>> lists = new ArrayList<>();
    for (int i = 0; i < numberOfParts; i++) {
      lists.add(new ArrayList<>());
    }
    for (int i = 0; i < shuffled.size(); i++) {
      lists.get(i % numberOfParts).add(shuffled.get(i));
    }
    return lists;
  }
  //</editor-fold>
}