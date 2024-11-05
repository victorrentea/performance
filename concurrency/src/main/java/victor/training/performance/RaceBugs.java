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
  private static final Object lock = new Object();
  private static List<Integer> evenNumbers = new ArrayList<>(); // mutable and doesn't lose increments

  // to assign sequential request Ids, PKs...
//  private static AtomicInteger total = new AtomicInteger(0);
//  private static Integer total = 0;

  // many parallel threads run this method:
  private static int countEven(List<Integer> numbers) {
    int localTotal = 0;
    log.info("Start");
    for (Integer n : numbers) {
      if (n % 2 == 0) {
        localTotal++;
      }
    }
//    total += localTotal; // there can still be a race.
    log.info("End");
    return localTotal;
  }

  public static void main(String[] args) throws Exception {
    List<Integer> fullList = IntStream.range(0, 10000).boxed().toList();

    // [[500elems],[500elems]]
    List<List<Integer>> parts = splitList(fullList, 2);

    ExecutorService pool = Executors.newCachedThreadPool();
//    List<Future<Integer>> futureList = new ArrayList<>();
//    for (List<Integer> part : parts) {
//      Future<Integer> localTotalFuture = pool.submit(new Callable<Integer>() {
//        @Override
//        public Integer call() throws Exception {
//          return countEven(part);// this method is executed by a worker thread
//        }
//      });
////      Future<Integer> localTotalFuture = pool.submit(() -> countEven(part)); // java 8 style
//
//      futureList.add(localTotalFuture);
//    }

    Future<Integer> firstHalfResultsFuture = pool.submit(() -> countEven(parts.get(0)));
    Future<Integer> secondHalfResultsFuture = pool.submit(() -> countEven(parts.get(1)));
    // 2 threads are now counting my even numbers
    int total = 0;
    total += firstHalfResultsFuture.get(); // blocks the main thread until the results of the first half are ready
    total += secondHalfResultsFuture.get();

    pool.shutdown();
    pool.awaitTermination(1, MINUTES);

//    for (Future<Integer> future : futureList) {
//      total += future.get();
//    }
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