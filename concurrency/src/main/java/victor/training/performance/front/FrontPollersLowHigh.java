package victor.training.performance.front;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
public class FrontPollersLowHigh {
  static ExecutorService highExecutor = new ThreadPoolExecutor(5, 5,
      1, TimeUnit.MINUTES, new ArrayBlockingQueue<>(50), withThreadName("🔴high"));

  static ExecutorService lowExecutor = new ThreadPoolExecutor(1, 1,
      1, TimeUnit.MINUTES, new ArrayBlockingQueue<>(50), withThreadName("🟢low"));
  @SneakyThrows
  public static void main(String[] args) {
    new ScheduledThreadPoolExecutor(1, withThreadName("🚽-poller"))
        .scheduleWithFixedDelay(() -> {
          try {
            lowExecutor.submit(() -> poll("🚽", 10000000)).get();
          } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
          }
        }, 0, 1, TimeUnit.MILLISECONDS);

    new ScheduledThreadPoolExecutor(1, withThreadName("🌳-poller"))
        .scheduleWithFixedDelay(() -> {
          try {
            lowExecutor.submit(() -> poll("🌳", 100)).get();
          } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
          }
        }, 0, 1, TimeUnit.MILLISECONDS);

    new ScheduledThreadPoolExecutor(1, withThreadName("🚀poller"))
        .scheduleWithFixedDelay(() -> {
          try {
            highExecutor.submit(() -> poll("🚀", 1000)).get();
            // astepti sa termini cererea curenta, intarziind schedule thread pana atunci
          } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
          }
        }, 0, 1, TimeUnit.SECONDS);
  }

  public static void poll(String type, int maxDelta) {
    log.info("Start " + type);
    var response = PervApi.fetchRachete(null, maxDelta);
    log.info("End " + type + ". got: " + response.data());
  }

  private static ThreadFactory withThreadName(String name) {
    return r -> {
      var t = new Thread(r);
      t.setName(name); // TODO counter ++
      return t;
    };
  }

}
