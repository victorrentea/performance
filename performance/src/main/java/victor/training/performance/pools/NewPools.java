package victor.training.performance.pools;

import lombok.extern.slf4j.Slf4j;
import victor.training.performance.ConcurrencyUtil;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NewPools {

   public static void main(String[] args) {
      List<Integer> ids = IntStream.range(1, 10).boxed().collect(Collectors.toList());

      for (Integer id : ids) {

      }
   }
}

@Slf4j
class CalculeScumpe {
   public void callREST(int x) {
      log.debug("Apelez serviciu extern " + x);
      ConcurrencyUtil.sleepq(1000);
      log.debug("Gata apel serviciu extern");
   }
}