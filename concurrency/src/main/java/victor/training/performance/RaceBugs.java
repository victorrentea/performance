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
  private static List<Integer> evenNumbers = new ArrayList<>();


  // device reachable
  private static  AtomicReference<Frequency> lastFrequency = new AtomicReference<>(new Frequency(0, 1));
  record Frequency(double frequency, double amplitude) {
    public Frequency setFrequency(double newFrequency) {
      return new Frequency(frequency, amplitude);
    }
  }
  class BadNeverEver {
    double frequency;
    public void add() {
      frequency++;//race
    }
  }

//  private static final List<String> messages = new ArrayList<>(10000);
  // record JFR events that in mem binary compressed


  private static AtomicInteger total = new AtomicInteger(); // perfect for sequences

  // many parallel threads run this method:
  private static int countEven(List<Integer> numbers) {
    log.info("Start");
    int localTotal = 0;
    for (Integer n : numbers) {
      if (n % 2 == 0) {
        localTotal++;
//        total.incrementAndGet(); // changig shared mutable state
//        while(true){
//          System.err.println("RETRIED");
//          Frequency oldFreq = lastFrequency.get();
//          Frequency newFreq = oldFreq.setFrequency();
//          boolean ok = lastFrequency.compareAndSet(oldFreq, newFreq);
//          if (ok) break;
//        }
        lastFrequency.getAndUpdate(f -> f.setFrequency(n)); // FP 😎
      }
    }
    log.info("End");
    return localTotal; // return YOUR partial results. no mutalbe state.
  }

  public static void main(String[] args) throws Exception {
    List<Integer> fullList = IntStream.range(0, 100_000).boxed().toList();

    // [[500elems],[500elems]]
    List<List<Integer>> parts = splitList(fullList, 2);

    ExecutorService pool = Executors.newCachedThreadPool();
    Future<Integer> firstHalfResultsFuture = pool.submit(() -> countEven(parts.get(0)));
    Future<Integer> secondHalfResultsFuture = pool.submit(() -> countEven(parts.get(1)));
    pool.shutdown();
    pool.awaitTermination(1, MINUTES);

    // let the workers work on their private dataset/counters..
    // merge their results at the end
    long mergedTotal = firstHalfResultsFuture.get() + secondHalfResultsFuture.get();
    log.debug("Counted: " + total);
    log.debug("Counted: " + lastFrequency);
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