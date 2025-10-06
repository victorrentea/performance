package victor.training.performance;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import victor.training.performance.util.PerformanceUtil;

import static java.util.concurrent.CompletableFuture.delayedExecutor;
import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
@EnableAsync
@EnableCaching
@EnableScheduling
@SpringBootApplication
public class MemoryApp {
  public static void main(String[] args) {
    SpringApplication.run(MemoryApp.class, args);
  }

  @EventListener
  public void onStart(ApplicationReadyEvent event) {
    log.info("🌟🌟🌟 MemoryApp at http://localhost:8080 pid {} {} 🌟🌟🌟",
        ProcessHandle.current().pid(),
        PerformanceUtil.getJavacVersion(MemoryApp.class));
    runAsync(System::gc, delayedExecutor(2, SECONDS));
    runAsync(System::gc, delayedExecutor(4, SECONDS));
  }
}
