package victor.training.performance.pools;

import org.springframework.web.bind.annotation.GetMapping;
import victor.training.performance.util.PerformanceUtil;

public class ThreadPlay {

   @GetMapping
   public void cevaWeb() {

      new Thread(() -> {
         System.out.println("Trebi ce iau timp");
         PerformanceUtil.sleepq(100);
      }).start();
   } // 0ms     1M
}
