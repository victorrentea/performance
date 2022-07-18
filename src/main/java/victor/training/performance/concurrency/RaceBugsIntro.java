package victor.training.performance.concurrency;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.stream.Collectors.toList;


@Slf4j
public class RaceBugsIntro {

   private static AtomicInteger a = new AtomicInteger();
   private static final Object LOCK = new Object();
   private static List<Integer> toate = Collections.synchronizedList(new ArrayList<>());

   private static void doCountAlive(List<Integer> idsChunk) { // ran in 2 parallel threads
      for (Integer id : idsChunk) { // size = 10_000
//         System.out.println(" " + id); // SQL SELECT

//         synchronized (LOCK) { // 2+ threaduri vor intra pe rand in sectiunea critica din {} daca toate se sync pe aceeasi instanta = =
//            a++;
//         }

         toate.add(id  *2);
         a.incrementAndGet();
      }
   }

   public static void main(String[] args) throws ExecutionException, InterruptedException {
      List<Integer> ids = IntStream.range(0, 2000_000).boxed().collect(toList());

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

      log.debug("Counted: " + a);
      log.debug("CountedList: " + toate.size());
   }


}