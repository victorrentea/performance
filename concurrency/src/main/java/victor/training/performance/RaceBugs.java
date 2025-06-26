package victor.training.performance;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

import static java.util.concurrent.TimeUnit.MINUTES;


@SuppressWarnings("ALL")
@Slf4j
public class RaceBugs {
  private static List<Integer> evenNumbers = new ArrayList<>();

  // many threads run in parallel this method:
  private static int countEven(List<Integer> numbers) {
    int total = 0;
    log.info("Start");
    for (Integer n : numbers) {
      if (n % 2 == 0) {
        total ++;
      }
    }
    if (Math.random() < .5) {
      throw new IllegalArgumentException("VAI VAI");
    }
    log.info("End");
    return total;
  }

  public static void main(String[] args) throws Exception {
    List<Integer> fullList = IntStream.range(0, 10_000).boxed().toList();

    // split in [[1..500],[501..1000]]
    List<List<Integer>> parts = splitList(fullList, 4);

    ExecutorService pool = Executors.newCachedThreadPool();
    int total = 0;
    List<Future<Integer>> futures = new ArrayList<>();
    for (List<Integer> part : parts) {
      var future = pool.submit(() -> countEven(part));
      futures.add(future);
    }
    // aici zboara 2 threaduri libere
    for (var future : futures) {
      total += future.get(); // iti arunca ex din worker
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