package victor.training.performance.java8;

import lombok.extern.slf4j.Slf4j;
import victor.training.performance.util.PerformanceUtil;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Slf4j
public class ParallelStreams {
   public static void main(String[] args) throws ExecutionException, InterruptedException {


      List<Integer> numbers =
          IntStream.range(1, 1000).boxed().collect(toList());

      Stream<Integer> iartama = numbers.parallelStream()
          .filter(n -> {
             log.debug("Filtering " + n);
             PerformanceUtil.sleepq(20);
             return n % 2 == 0;
          });

      ForkJoinPool myPool = new ForkJoinPool(100);
      List<Integer> doamnefereste = myPool.submit(() -> iartama.collect(toList())).get();

      System.out.println(doamnefereste);
      // MORALA:  nu te blochezi in commonPool > ca nu stii pe cine strici :: adica nu faci DB/REST calls
      // solutia 1: cresti dimens commonPool (riscanta)
      // solutia 2: (o sa doara) termini parallelStreamul intr-un ForkJoinPool privata (WTF??!!)
      // Solutia 3: pentru ca 2 pute rau>>> nu parallelStream si spargi in pagini si dai la un pool.submit(...)
   }
}
