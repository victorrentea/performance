package victor.training.performance.java8;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class ParallelStreams {

   // daca pe alt request paralel cu tine face si el parallelStream() --> el va fi impactat de tin care faci REST/DB pe parallelStream
   public static void main(String[] args) throws ExecutionException, InterruptedException {
      todo();
      todo();
      todo();
      todo();
      todo();
      todo();
      todo();
      todo();
      todo();
      todo();
      todo();
      todo();

      // Recomandarea mea este:
      // folosesti parallelStream DOAR pentru taskuri CPU-only;
      // MERITA ? castigi ceva timp
      // daca munca PER ELEMENT e foarte putina ca CPU, overheadul paralelizarii, coordonari si collect depaseste ce castigi.
      // DACA munca per element e HEAVY, incepe sa merite.
      // image, xls, pdf, encryption, parse

//      System.out.println(list);

      Integer []arr = {1,2,3};
      List<Integer> list = new ArrayList<>();
      List<Integer> syncList = Collections.synchronizedList(list);
   }

   private static void todo() throws InterruptedException, ExecutionException {
      List<Integer> numbers = IntStream.range(0, 100000).boxed().collect(toList());
      // foloseste fix 11 threaduri = (default) N_LOGICAL_PROCESSORS - 1 (ca pune si main mana)


      long t0 = System.currentTimeMillis();

      Stream<Integer> parallelStream = numbers.parallelStream()
          .filter(n -> {
//             PerformanceUtil.log("Filter " + n);

             return n % 2 == 1;
          })
//          .distinct()
//          .sorted()
          .map(n -> {
//             PerformanceUtil.sleepq(100); // DB/REST
//             PerformanceUtil.log("Map " + n);
             return n * n;
          });

      ForkJoinPool pool = new ForkJoinPool(3);
      List<Integer> list = pool.submit(() -> parallelStream.collect(toList())).get();
//      List<Integer> list = parallelStream.sequential().collect(toList());

      long t1 = System.currentTimeMillis();
      System.out.println("Delta = " + (t1-t0));
   }
}
