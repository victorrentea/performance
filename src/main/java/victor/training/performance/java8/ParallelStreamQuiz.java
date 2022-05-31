package victor.training.performance.java8;

import lombok.extern.slf4j.Slf4j;
import victor.training.performance.util.PerformanceUtil;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

@Slf4j
public class ParallelStreamQuiz {
   public static void main(String[] args) throws ExecutionException, InterruptedException {
      List<Integer> list = asList(1, 2, 3, 4);
       Stream<Integer> stream = list.parallelStream()
               .filter(i -> {
                   log.debug("Filter " + i);
                   PerformanceUtil.sleepq(1000); // NU: ~ RestTemplate, jdbcTemplate, entityManger, repo
                   return true;
               })
               .map(i -> {
                   log.debug("Map " + i);
                   return i;
               });
//       ForkJoinPool pool = new ForkJoinPool(2);
//       Optional<Integer> fi =   pool.submit(()->     stream.findFirst()     ).get()     ; // pe thread poolul meu, doar al meu

       Optional<Integer> fi =   stream.findFirst();

       // terminal operation (.collect, .min)
      System.out.println(fi);


       //. REGULA: pe parallelStream lansezi doar munca GREA de CPU

   }
}
