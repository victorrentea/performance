package victor.training.performance;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

@Slf4j
public class DaemonOrAngel {
  public static void main(String[] args) throws InterruptedException {
    CompletableFuture<Void> f = CompletableFuture.runAsync(() ->
        log.info("RUNNING"));
    f.join(); // or .get() on main
//    Thread.sleep(10); // on main
  }
}
