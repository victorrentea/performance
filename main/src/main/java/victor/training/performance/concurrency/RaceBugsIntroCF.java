package victor.training.performance.concurrency;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;


@Slf4j
public class RaceBugsIntroCF {
  private static int countEven(List<Integer> numbers) {
    int localTotal = 0;
    for (Integer n : numbers) {
      if (n % 2 == 0) {
        localTotal++;
      }
    }
    log.info("end");
    return localTotal; // map-reduce style; FP-style: return the data instead of changing a global one
  }

  public static void main(String[] args) throws ExecutionException, InterruptedException {
    List<Integer> fullList = IntStream.range(0, 10000).boxed().collect(toList());

    List<List<Integer>> inputs = splitList(fullList, 2);

    // ForkJoinPool.commonPool a JVM-global hidden default threadpool to run all CompletableFuture and parallelStreams()
    CompletableFuture<Integer> result1CF = CompletableFuture.supplyAsync(() -> countEven(inputs.get(0)));
    CompletableFuture<Integer> result2CF = CompletableFuture.supplyAsync(() -> countEven(inputs.get(1)));

    Integer total = result1CF.get() + result2CF.get();
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