package victor.training.performance;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static java.util.concurrent.TimeUnit.MINUTES;


@SuppressWarnings("ALL")
@Slf4j
public class RaceBugs {
  private static List<Integer> evenNumbers = new ArrayList<>();

  private static Integer total = 0;
  private static final Object lock = new Object();
//  private static final List<String> messages = new ArrayList<>(10000);
  // record JFR events that in mem binary compressed

  // many parallel threads run this method:
  private static void countEven(List<Integer> numbers) {
    log.info("Start");
    for (Integer n : numbers) {
      if (n % 2 == 0) {
        synchronized (lock) { // WRONG object
          total++;
        }
      }
    }
    log.info("End");
  }

  public static void main(String[] args) throws Exception {
    List<Integer> fullList = IntStream.range(0, 100_000).boxed().toList();

    // [[500elems],[500elems]]
    List<List<Integer>> parts = splitList(fullList, 2);

    ExecutorService pool = Executors.newCachedThreadPool();
    for (List<Integer> part : parts) {
      pool.submit(()-> countEven(part));
    }
    pool.shutdown();
    pool.awaitTermination(1, MINUTES);

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