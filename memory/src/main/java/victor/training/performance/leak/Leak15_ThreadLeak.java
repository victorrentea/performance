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
      pool.submit(() -> someWorkInParallel(1));
      pool.submit(() -> someWorkInParallel(2));
      try {
         moreWork();
      } finally {
         pool.shutdown();
         // or even better use a  global Thread Pool  shared across your app
      }
   }

   private static void someWorkInParallel(int i) {
      log.info("Work " + i);
   }

   private void moreWork() {
      throw new RuntimeException("#life");
   }
}

// TODO avoid creating new Thread Pools per request
// 1. thread pool not #shutdown()
// 2. Better: autowire and use a Spring-managed ThreadPoolTaskExecutor
