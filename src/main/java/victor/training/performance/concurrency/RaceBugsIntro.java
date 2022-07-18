package victor.training.performance.concurrency;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.stream.Collectors.toList;


@Slf4j
public class RaceBugsIntro {

   private static Integer a = 0;

   // 2 parallel threads run this:
   private static void doCountAlive(List<Integer> idsChunk) {
      for (Integer id : idsChunk) { // .size() = 10k
         a++;
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

      log.debug("Counted: " + a);
   }


}