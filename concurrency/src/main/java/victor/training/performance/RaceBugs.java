package victor.training.performance;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

import static java.util.concurrent.TimeUnit.MINUTES;


@SuppressWarnings("ALL")
@Slf4j
public class RaceBugs {
//  private static AtomicReference<ImmutableList<Integer>> refToImmList =
//      new AtomicReference<>(ImmutableList.of());
  record DataByNow(int evenCounted) {}
  private static AtomicReference<DataByNow> refToImmList =
      new AtomicReference<>(new DataByNow(0));

  private static List<Integer> evenNumbers = Collections.synchronizedList(new ArrayList<>());
//  private static final Object lock = new Object(); // on which synchronzied ⭐️1
//  private static int total=0;

//  private static AtomicInteger total = new AtomicInteger(0);// ⭐️2

  //map-reduce with pure functions [FP-style] ⭐️3 return the value; less mutable state
  // many parallel threads run this method:
  private static int countEven(List<Integer> numbers) {
    int myTotal = 0;
    log.info("Start");
    for (Integer n : numbers) {
//      log.debug("Lemme " + n); // 99.9%
      if (n % 2 == 0) {
//         total.incrementAndGet(); // CAS
        myTotal++;
        evenNumbers.add(n);

//        var immList = refToImmList.get();
//        var immListAdded = ImmutableList.<Integer>builder().addAll(immList).add(n).build(); // CRIME for perf ☠️
//        System.out.println("Different reference: " + (immListAdded != immList));
//        refToImmList.compareAndExchange(immList, immListAdded);

        // use a -> with AtomicReference to increment DataByNow.evenCounted
          refToImmList.updateAndGet(old -> {
            lambdaRunCount.incrementAndGet();
            return new DataByNow(old.evenCounted + 1);
          });
      }
    }
    log.info("End");
    return myTotal;
  }
  private static final AtomicInteger lambdaRunCount = new AtomicInteger(0);

  public static void main(String[] args) throws Exception {
    List<Integer> fullList = IntStream.range(0, 100_000).boxed().toList();

    // split in [[1..500],[501..1000]]
    List<List<Integer>> parts = splitList(fullList, 2);

    ExecutorService pool = Executors.newCachedThreadPool();
    List<Future<Integer>> futures = new ArrayList<>();
    for (List<Integer> part : parts) {
      Future<Integer> futureResult = pool.submit(() -> countEven(part));
      futures.add(futureResult);
    }
    pool.shutdown();
    pool.awaitTermination(1, MINUTES);

    int total = futures.stream().mapToInt(f -> {
      try {
        return f.get();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }).sum();

//    synchronized (lock) {
    log.debug("Counted: " + total);
//    }
    log.debug("List.size: " + evenNumbers.size());

    log.debug("AtomicReference: " + refToImmList.get().evenCounted);
    log.debug("Lambda run count: " + lambdaRunCount.get());
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