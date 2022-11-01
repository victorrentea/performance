package victor.training.performance.parallelStream;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;

@Slf4j
public class ParallelStreamQuiz {
   public static void main(String[] args) {
      List<Integer> list = asList(1, 2, 3, 4);
      Optional<Integer> fi = list.parallelStream()
          .filter(i -> {
             log.debug("Filter " + i);
             return true;
          })
          .sequential()
          .map(i -> {
             log.debug("Map " + i);
             return i;
          })
          .parallel()
          .findFirst();
      System.out.println(fi);

   }
}
