package victor.training.performance.leak;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@RestController
@RequestMapping("leak15")
public class Leak15_ThreadLeak {

   @GetMapping
   public void endpoint() {
      ExecutorService pool = Executors.newFixedThreadPool(2);
      try {
         pool.submit(() -> log.info("Work"));
         surprise();
      } finally {
         pool.shutdown();
      }
   }

   private void surprise() {
      throw new RuntimeException("#life");
   }
}

// TODO avoid creating new Thread Pools per request
//  > inject and use a Spring-managed ThreadPoolTaskExecutor
