package victor.training.performance.leak;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("leak15")
public class Leak15_ThreadLeak {

   //   ExecutorService pool = Executors.newFixedThreadPool(2); //#2

   @Autowired
   ThreadPoolTaskExecutor pool; // #asa-da
   @GetMapping
   public void endpoint() {
//      ExecutorService pool = Executors.newFixedThreadPool(2);
      pool.submit(() -> log.info("Work1"));
      pool.submit(() -> log.info("Work2"));
      pool.shutdown();// #1
   }



   private void surprise() {
      throw new RuntimeException("#life");
   }
}

// TODO avoid creating new Thread Pools per request
//  > inject and use a Spring-managed ThreadPoolTaskExecutor
