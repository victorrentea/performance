package victor.training.performance;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

import static java.util.concurrent.TimeUnit.HOURS;


@SuppressWarnings("ALL")
@Slf4j
public class RaceBugs {
  private static List<Integer> evenNumbers = new ArrayList<>();

//  private static final Object LOCK = new Object();
  private static Integer total = 0;

  // many parallel threads run this method:
  private static Integer countEven(List<Integer> numbers) {
    log.info("Start");
    int totalLocal = 0;
    for (Integer n : numbers) {
      if (n % 2 == 0) {
//        total++;
        totalLocal++;
      }
    }
    log.info("End");
    return totalLocal; // FP-style; map-reduce
    // spark: dataframe spark in imparti si apoi aplici o suma pe partitii separat
    // si apoi faci "reduce" de rezultate (le mergeuiesti)
  }

  public static void main(String[] args) throws Exception {
    List<Integer> fullList = IntStream.range(0, 1000).boxed().toList();

    List<List<Integer>> parts = splitList(fullList, 20);

    List<Future<Integer>> futures = new ArrayList<>();
    ExecutorService pool = Executors.newCachedThreadPool();
    for (List<Integer> part : parts) {
      var f=pool.submit(() -> countEven(part));
      futures.add(f);
    }
    pool.shutdown();
    pool.awaitTermination(1, HOURS);

    for (var f : futures) {
      total += f.get();
    }

    log.debug("Counted: " + total);
    log.debug("List.size: " + evenNumbers.size());
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