package victor.training.performance.concurrency;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;


@Slf4j
public class RaceBugsIntro {

   private static int id = 0;
   private static final Object lock = new Object();

   private static final List<Integer> ints =
           Collections.synchronizedList(new ArrayList<>());

   // 2 parallel threads run this:
   private static void doCountAlive(List<Integer> idsChunk) {
      for (Integer i : idsChunk) { // .size() = 10k
         synchronized (lock) {
            // in acest bloc nu poate intra decat 1 thread odata,
            // cu conditia ca toata lumea sa se sync pe
            // aceeasi instanta ! eg lock
            id++;
            ints.add(i);
         }
      }
   }

   public static void main(String[] args) throws ExecutionException, InterruptedException {
      List<Integer> ids = IntStream.range(0, 20_000).boxed().collect(toList());

      // split the work in two
      List<Integer> firstHalf = ids.subList(0, ids.size() / 2);
      List<Integer> secondHalf = ids.subList(ids.size() / 2, ids.size());

      // submit the 2 tasks
      ExecutorService pool = Executors.newCachedThreadPool();
      Future<?> future1 = pool.submit(() -> doCountAlive(firstHalf));
      Future<?> future2 = pool.submit(() -> doCountAlive(secondHalf));
      log.debug("Tasks launched...");

      // wait for the tasks to complete
      future1.get();
      future2.get();

      log.debug("Counted: " + id);
   }


}