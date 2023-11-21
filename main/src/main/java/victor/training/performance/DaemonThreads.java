package victor.training.performance;

import lombok.extern.slf4j.Slf4j;
import org.testcontainers.shaded.org.apache.commons.lang3.ThreadUtils;
import victor.training.performance.util.PerformanceUtil;

import java.util.concurrent.CompletableFuture;

@Slf4j
public class DaemonThreads {
  public static void main(String[] args) {
    CompletableFuture.runAsync(() -> {
      log.info("Start");
      PerformanceUtil.sleepMillis(2000);
      log.info("Cu intarziere"); // procesu moare si nu apuci sa vezi astga, ca ruleaza pe un thread daemon
    });
    PerformanceUtil.sleepMillis(10);
  }
}
