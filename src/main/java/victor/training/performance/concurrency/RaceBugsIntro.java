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
private static final Object mutex = new Object();
   private static Integer id = 0; // unde sta asta ? heap
//   private static List<Integer> cuToate = Collections.synchronizedList(new ArrayList<>());

   // 2 parallel threads run this:
   private static List<Integer> doCountAlive(List<Integer> idsChunk) {
      List<Integer> doarCuAleMele = new ArrayList<>();
      for (Integer i : idsChunk) { // .size() = 10k

         synchronized (mutex) {
            // protejezi orice modificare si citire din date mutabile partjate intre threaduri cu cate un mutex d-asta
            id++;
         }
         doarCuAleMele.add(i);
      }
      return doarCuAleMele;
   }

   public static void main(String[] args) throws ExecutionException, InterruptedException {
      List<Integer> ids = IntStream.range(0, 2000).boxed().collect(toList());

      // split the work in two
      List<Integer> firstHalf = ids.subList(0, ids.size() / 2);
      List<Integer> secondHalf = ids.subList(ids.size() / 2, ids.size());

      // submit the 2 tasks
      ExecutorService pool = Executors.newCachedThreadPool();
      Future<List<Integer>> future1 = pool.submit(() -> doCountAlive(firstHalf));
      Future<List<Integer>> future2 = pool.submit(() -> doCountAlive(secondHalf));
      log.debug("Tasks launched...");

      // wait for the tasks to complete
      List<Integer> part1 = future1.get();
      List<Integer> part2 = future2.get();

      log.debug("Counted: " + id);
      List<Integer> cuToate = new ArrayList<>();
      cuToate.addAll(part1);
      cuToate.addAll(part2);
      log.debug("Cate oare in lista " +cuToate.size());
   }


}