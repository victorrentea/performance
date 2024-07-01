package victor.training.performance;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;


@Slf4j
public class RaceBugs {
  private static List<Integer> evenNumbers = Collections.synchronizedList(new ArrayList<>());
  private static final ReentrantLock lock =  new ReentrantLock();

  //  private static AtomicInteger total = new AtomicInteger(0);
  private static Integer total = 0;

  private static int countEven(List<Integer> numbers) { // functia asta acum e corect proiectata; e pura. doar intoarce date . asa lucram pe multithreading
    log.info("Start");
    int totalLocal = 0;
    for (Integer n : numbers) {
      if (n % 2 == 0) {
        evenNumbers.add(n); // race
//        synchronized (evenNumbers) {
    //        evenNumbers.stream().filter(e -> e % 2 == 0).collect(Collectors.toList());
            // ConcurrentModificationException
        // }
        totalLocal ++;
//        lock.lock();
//        try {
//          f(); // problema daca f dureaza mult!!
//          // Doamne fereste sa faci aici: I/O (REST API CALL, DB, RMI, SOAP, ..)
//        } finally {
//          lock.unlock();
//        }
      }
    }
//    total += totalLocal; // inca avem race p=0.0000000001
    log.info("end");
    return totalLocal;
  }

  private static void f() {
    total++; // => total = new Integer
  }

  public static void main(String[] args) throws Exception {
    // maresti sansa sa dai in concurrencu bug: NUMARUL DE THREADURI, NUMARUL DE ELEMENTE IN LISTA, NUMARUL DE ITERATII
    List<Integer> fullList = IntStream.range(0, 10_000).boxed().collect(toList());

    List<List<Integer>> lists = splitList(fullList, 2);
    List<Callable<Integer>> tasks = lists.stream()
        .map(numbers -> (Callable<Integer>) () -> countEven(numbers))
        .collect(toList());
    ExecutorService pool = Executors.newCachedThreadPool();
    List<Future<Integer>> viitoareInturi = pool.invokeAll(tasks);

    int total=0;
    for (Future<Integer> viitor : viitoareInturi) {
      total+=viitor.get(); // nu race, pt ca sunt intr-un singur thread
    }
    pool.shutdown();

    log.debug("Counted: " + total);
    log.debug("List.size: " + evenNumbers.size());
  }

  //<editor-fold desc="utility functions">
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