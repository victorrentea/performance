package victor.training.performance.concurrency;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static java.util.Collections.synchronizedList;
import static java.util.stream.Collectors.toList;


@Slf4j
public class RaceBugsIntro {

   private static AtomicInteger total = new AtomicInteger(); // THE way to have a shared counter
   private static final Object mutex = new Object();
      private static final List<Integer> ids = synchronizedList(new ArrayList<>());
//   private static final List<Integer> ids = new ConcurrentSkipListSet<>();
   Map<String,String> map;
   // 2 parallel threads run this:
   private static int doCountAlive(List<Integer> idsChunk) {
      int localTotal = 0;

      for (Integer i : idsChunk) { // .size() = 10k
         localTotal ++;
//         total.incrementAndGet();

//         if (!ids.contains(i)) {
//            ids.add(i);
//         }
      }
      return localTotal; // map-reduce approach:
      // instead of CHANGING MUTABLE SHARED STATE
      // you change local data owned by the thread
      // and RETURN THE VALUE YOU COMPUTED at the end.
   }

   public static void main(String[] args) throws ExecutionException, InterruptedException {
      ExecutorService pool = Executors.newCachedThreadPool();

      List<Future<?>> futures = new ArrayList<>();
      for (int i = 0; i < 1000; i++) {
         Future<?> submit = pool.submit(() -> doCountAlive(IntStream.range(0, 1000).boxed().collect(toList())));
         futures.add(submit);
      }

      futures.forEach(f -> {
         try {
            f.get();
         } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
         }
      });


//      List<Integer> ids = IntStream.range(0, 20000_000).boxed().collect(toList());
//
//      // split the work in two
//      List<Integer> firstHalf = ids.subList(0, ids.size() / 2);
//      List<Integer> secondHalf = ids.subList(ids.size() / 2, ids.size());
//
//      // submit the 2 tasks
//      ExecutorService pool = Executors.newCachedThreadPool();
//      Future<?> future1 = pool.submit(() -> doCountAlive(firstHalf));
//      Future<?> future2 = pool.submit(() -> doCountAlive(secondHalf));
//      log.debug("Tasks launched...");
//
//      // wait for the tasks to complete
//      future1.get();
//      future2.get();

      log.debug("Counted: " + total);
      log.debug("Counted: " + ids.size());
   }


}