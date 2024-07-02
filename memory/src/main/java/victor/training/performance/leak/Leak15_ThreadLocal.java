package victor.training.performance.leak;

import com.github.dockerjava.api.command.AuthCmd.Exec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.leak.obj.BigObject20MB;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@RestController
@RequestMapping("leak15")
public class Leak15_ThreadLocal {
   private static final ThreadLocal<BigObject20MB> threadLocal = new ThreadLocal<>();

   @GetMapping
   public void endpoint() {
      ExecutorService pool = Executors.newFixedThreadPool(2);
      try {
         pool.submit(() -> log.info("Work"));
         f();
      } finally {
         pool.shutdown();
      }
      // TODO tomorrow (not me) when you have time,
      //   inject and use a Spring-managed ThreadPoolTaskExecutor
   }

   private void f() {
      if (true) throw new RuntimeException("Intentional");
   }
}
