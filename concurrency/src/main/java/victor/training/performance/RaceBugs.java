package victor.training.performance;

import com.google.common.collect.ImmutableList;
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

  // NEVER DO THIS: clone immutable lists; stupid on purpose
  private static ImmutableList<Integer> evens = ImmutableList.of();

  // many parallel threads run this method:
  private static void countEven(List<Integer> numbers) {
    log.info("Start");
    for (Integer n : numbers) {
      if (n % 2 == 0) {
        evens.add(n);
      }
    }
    log.info("End");
  }

  public static void main(String[] args) throws Exception {
    List<Integer> fullList = IntStream.range(0, 10000).boxed().toList();

    // split in [[1..500],[501..1000]]
    List<List<Integer>> parts = splitList(fullList, 2);

    ExecutorService pool = Executors.newCachedThreadPool();
    List<Future> futures = new ArrayList<>();
    for (List<Integer> part : parts) {
      Future<?> future = pool.submit(() -> countEven(part));
      futures.add(future);
    }

    // wait for all tasks to finish
    for (Future<?> future : futures) {
      future.get(); // Excetions from worker thread pop in your face
      // in spring @Async void! methods(){} exceptions are auto-logged
    }

    pool.shutdown();
    pool.awaitTermination(1, MINUTES);

    log.debug("Counted: " + evens.size());
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