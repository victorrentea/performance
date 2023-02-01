package victor.training.performance;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.locks.Lock;

import static java.lang.Thread.sleep;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RedisLock {
  private static final String MY_LOCK_KEY = "someLockKey";
  private final LockRegistry lockRegistry;

  @GetMapping("lock")
  public String redisLock() throws InterruptedException {
    Lock lock = lockRegistry.obtain(MY_LOCK_KEY);
    try {
      //  🛑 childish/initial parameters ~> tune timeouts considering load to avoid OOME ~>
      if (lock.tryLock(2, MINUTES)) {
        log.info("ENTER critical section");
        sleep(1000);
        log.info("Critical action ☠️ ....");
        log.info("EXIT critical section ");
        return "SUCCESS";
      } else {
        return "ERROR: Timed out waiting for the lock";
      }
    } finally {
      lock.unlock(); // MUST NOT FORGET
    }
  }

}
