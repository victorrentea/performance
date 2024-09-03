package victor.training.performance;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.concurrent.TimeUnit.MINUTES;


@SuppressWarnings("ALL")
@Slf4j
public class RaceBugs {
  private static List<Integer> evenNumbers = new ArrayList<>();


  // many parallel threads run this method:
  private static int countEven(List<Integer> numbers) {
    // FP principles: return don;t change
    log.info("Start");
    int localTotal = 0;
    for (Integer n : numbers) {
      if (n % 2 == 0) {
        localTotal++;
      }
    }
    log.info("End");
    return localTotal;
  }

  public static void main(String[] args) throws Exception {
    List<Integer> fullList = IntStream.range(0, 1_0000).boxed().toList();

    List<List<Integer>> parts = splitList(fullList, 4);

    // promises (FE) === CompletableFuture (java): call-backbased way of non-blockign concurreny
    CompletableFuture<Integer> initial = CompletableFuture.completedFuture(0);

    for (List<Integer> part : parts) {
      CompletableFuture<Integer> task = supplyAsync(() -> countEven(part));
      initial = initial.thenCombineAsync(task, Integer::sum);
    }
    int total = initial.join();

//    CompletableFuture < Integer > cf1 = supplyAsync(() -> countEven(parts.get(0)));
//    CompletableFuture<Integer> cf2 = supplyAsync(() -> countEven(parts.get(1)));
//    int total = cf1.join() + cf2.join();
//    int total = cf1.thenCombine(cf2, Integer::sum).join();

//    ExecutorService pool = Executors.newCachedThreadPool(); // risky: too many threads might crash your ssytem=
//    List<Future<Integer>> futures = new ArrayList<>();
//    for (List<Integer> part : parts) {
//      Future<Integer> futureResult = pool.submit(() -> countEven(part));
//      futures.add(futureResult);
//    }
//    int total = futures.stream().mapToInt(f -> {
//      try {
//        return f.get();
//      } catch (Exception e) {
//        throw new RuntimeException(e);
//      }
//    }).sum(); // this sum runs in a single thread
    // map-reduce strategy: split the work in *independent* parts (CAN BE HARD)
    // work on each part in parallel
    // and then combine the results in a single thread
//    pool.shutdown();
//    pool.awaitTermination(1, MINUTES);
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