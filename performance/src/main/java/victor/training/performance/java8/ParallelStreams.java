package victor.training.performance.java8;

import lombok.extern.slf4j.Slf4j;
import victor.training.performance.ConcurrencyUtil;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

@Slf4j
public class ParallelStreams {
   public static void main(String[] args) {


      List<Integer> numbers = IntStream.range(1, 11).boxed().collect(toList());


      List<Integer> list = numbers.parallelStream()
          // tocmai ai pierdut Tranzactia deschisa din threadul initial, JDBC Connection lost,
          // ThreadLocals (currentUserRights) l-ai pierdut
          // Userul curent logat (@PreAuthorize, @RolesAllowed, SecurityContextHolder) l-ai pierdut

          // NU CARE CUMVA sa faci DB/API dintr-un parallel stream,
          // caci blochezi pe toti ceilalti din JVM
          .filter(n -> {
             log.debug("Filtrez " + n);
             ConcurrencyUtil.sleepq(1000); // Network/DB call -- NU
             return n % 2 == 1;
          })
          .map(n -> {
             log.debug("Map " + n);
             return n * n;
          })
          .collect(toList());


      System.out.println(list);
   }
}
