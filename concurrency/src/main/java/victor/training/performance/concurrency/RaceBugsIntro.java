package victor.training.performance.concurrency;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;


@Slf4j
public class RaceBugsIntro {
  private static final Object MUTEX = new Object(); // SOC: e folosit doar ca obiect pe care sa te sincronzezi
  private static List<Integer> evenNumbers = new ArrayList<>();
  private static ReentrantLock lock = new ReentrantLock();

  private static Set<Integer> uniceSiInOrdine = Collections.synchronizedSet(new LinkedHashSet<>());

  // excelent pt a genera id-uri noi, secvente in memorie, sau a face sume
//  AtomicLong
//  AtomicReference
//  private static AtomicInteger total = new AtomicInteger(0);
//  private static long total;
  // many parallel threads run this method:
  private static int countEven(List<Integer> numbers) {
    log.info("Start");
    int totalLocal = 0;
    for (Integer n : numbers) {
      if (n % 2 == 0) {
//        synchronized (MUTEX) {
//          total++;
//        }
//        total.incrementAndGet(); // spilu e ca foloseste o instruct de CPU low level
        totalLocal++;
        // orice e modificabil intr-un flux multithread trebuie pazit cu un lock
        lock.lock();
        try {
          if (!evenNumbers.contains(n)) {
            evenNumbers.add(n);
          }
        } finally {
          lock.unlock();
        }
//          }
      }
    }
    log.info("end");
//    total += totalLocal; // inginereste riscul muuult mai mic sa te suprapui
    // dar nu e inca stiintific thread safe;
    return totalLocal;
    // "Map-Reduce" strategy: principiu de viata
    // imparti munca la workeri care ruleaza independent (aici e usor:) = MAP
    // numeri parele din fiecare jumatate independent,
    // si aduni totalele locale = REDUCE
  }

  public static void main(String[] args) throws ExecutionException, InterruptedException {
    List<Integer> fullList = IntStream.range(0, 10_000).boxed().collect(toList());
    // 5000 nr pare

    List<List<Integer>> lists = splitList(fullList, 2);
    List<Callable<Integer>> tasks = lists.stream().map(numbers -> (Callable<Integer>) () -> countEven(numbers)).collect(toList());

    ExecutorService pool = Executors.newCachedThreadPool();
    List<Future<Integer>> rezultate = pool.invokeAll(tasks);
    int totalGeneral = 0;
    for (Future<Integer> r : rezultate) {
      totalGeneral += r.get();
    }

    pool.shutdown();

    log.debug("Counted: " + totalGeneral);
    log.debug("Counted: " + evenNumbers.size());
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