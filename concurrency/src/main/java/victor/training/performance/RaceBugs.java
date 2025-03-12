package victor.training.performance;

import lombok.SneakyThrows;
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
//  private static AtomicInteger total = new AtomicInteger(0);
//  private static final Object lock = new Object();
  private static final List<Integer> evenNumbers = Collections.synchronizedList(new ArrayList<>());

  // many parallel threads run this method:
  @SneakyThrows
  private static int countEven(List<Integer> numbers) {
    // MAP
    log.info("Start");
    int myTotal = 0;
    for (Integer n : numbers) {
      if (n % 2 == 0) {
        myTotal++;
        if (!evenNumbers.contains(n)) {
          evenNumbers.add(n); // MUTATE DATA = BAD VICTOR
        }
      }
    }
    log.info("End");
    return myTotal;
  }

  public static void main(String[] args) throws Exception {
    List<Integer> fullList = IntStream.range(0, 100000).boxed().toList();

    // [[500elems],[500elems]]
    List<List<Integer>> parts = splitList(fullList, 2);

    ExecutorService pool = Executors.newCachedThreadPool();
    // FORK
    Future<Integer> future1 = pool.submit(() -> countEven(parts.get(0)));
    Future<Integer> future2 = pool.submit(() -> countEven(parts.get(1)));
    pool.shutdown();
    pool.awaitTermination(1, MINUTES);
    //REDUCE
    var total = future1.get() + future2.get();
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