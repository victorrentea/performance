package victor.training.performance.concurrency;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.stream.Collectors.toList;


@Slf4j
public class RaceBugsIntro {

   private static final Object LOCK = new Object();

      private static Integer population = 0;
//   private static AtomicInteger population = new AtomicInteger(0);

//   public synchronized void metDeInstanta() {
//      //      synchronized (this) { << echivalent
//      //   chestii
//   }
//   public synchronized static void oAltaMetoda() {
//      //      synchronized (RaceBugsIntro.class) { // << echivalent
//      //         // chestii
//   }

   private static void doCountAlive(List<Integer> idsChunk) { // ran in 2 parallel threads
      for (Integer id : idsChunk) {

         synchronized (LOCK) {
            // zona critica
            population++;
         }

//         population.incrementAndGet();
      }
   }

   public static void main(String[] args) throws ExecutionException, InterruptedException {
      List<Integer> ids = IntStream.range(0, 20_000).boxed().collect(toList());

      // split the work in two
      List<Integer> firstHalf = ids.subList(0, ids.size() / 2);
      List<Integer> secondHalf = ids.subList(ids.size() / 2, ids.size());

      // submit the 2 tasks
      ExecutorService pool = new ThreadPoolExecutor(0, 2, 500, MILLISECONDS, new SynchronousQueue<>());
      Future<?> future1 = pool.submit(() -> doCountAlive(firstHalf));
      Future<?> future2 = pool.submit(() -> doCountAlive(secondHalf));
      log.debug("Tasks launched...");

      // wait for the tasks to complete
      future1.get();
      future2.get();

      log.debug("Counted: " + population);
//      log.debug("Counted: " + population.get());
   }


}