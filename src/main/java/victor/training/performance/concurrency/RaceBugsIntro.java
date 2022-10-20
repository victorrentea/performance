package victor.training.performance.concurrency;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import victor.training.performance.util.PerformanceUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;


@Slf4j
public class RaceBugsIntro {

   private static AtomicInteger id = new AtomicInteger(0);

   private static List<Integer> lista = Collections.synchronizedList(new CopyOnWriteArrayList<>(new ArrayList<>()));

   //colectii neblocante.
   // anti pattern: foloseste tata cu cache: ehcache, caffeine, // distribuite: redis, hazelcast, ...
//   private static ConcurrentHashMap<String, String> cacheInHouseCaSuntemMaiDesteptiCaEhcache_sauCaffeine = new ConcurrentHashMap<>();

   // lookup cache key:id   value:language.Name


//   @Cacheable("countries")
   // 2 parallel threads run this:
   private static void doCountAlive(List<Integer> idsChunk) {
      for (Integer i : idsChunk) { // .size() = 10k
         id.incrementAndGet();

         lista.add(i); // O(1)

         int s = 0;
         for (Integer integer : lista) {
            s += integer;
            PerformanceUtil.sleepq(1);
         }

//         int[] vs ArrayList<Integer>
      }
   }

   public static void main(String[] args) throws ExecutionException, InterruptedException {
      List<Integer> ids = IntStream.range(0, 200).boxed().collect(toList());

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
      System.out.println(lista);

      log.debug("Counted: " + id);
   }


}