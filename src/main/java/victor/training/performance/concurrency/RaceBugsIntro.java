package victor.training.performance.concurrency;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;
import java.util.stream.IntStream;

import static java.lang.System.currentTimeMillis;
import static java.util.stream.Collectors.toList;


@Slf4j
public class RaceBugsIntro {

//   private static AtomicInteger id = new AtomicInteger();
static long id = 0;
   // 2 parallel threads run this:
   @SneakyThrows
   private static void doCountAlive(List<Integer> idsChunk) {
      int localVar = 0;
      for (Integer i : idsChunk) { // .size() = 10k
//         id.incrementAndGet();
//         localVar++;
id++;
//         System.out.print("."); // HeisenBUG
         // Heisenber principle: trying to measure stuff influence the thing itself.
      }
//      id += localVar;
   }

   public static void main(String[] args) throws ExecutionException, InterruptedException {
      List<Integer> ids = IntStream.range(0, 20_000).boxed().collect(toList());

      long t0 = currentTimeMillis();
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
      long t1 = currentTimeMillis();
      System.out.println("delta T on a cold JVM not optimized by Just in time compiler. it;s a LIE: " + (t1-t0));
   }


}