package victor.training.performance.front;

import io.micrometer.core.instrument.logging.LoggingMeterRegistry;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static java.lang.System.currentTimeMillis;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
public class FrontPollersPaginateAndSchedule {

  @SneakyThrows
  public static void main(String[] args) {
//    ScheduledExecutorService sched1 = Executors.newSingleThreadScheduledExecutor();
//    sched1.scheduleWithFixedDelay(() -> poll("🚀", sched1), 0, 1, SECONDS);

    ScheduledExecutorService sched2 = Executors.newScheduledThreadPool(1); // NU PUNE 2 sa nu reentrez ca vad dublate
    sched2.scheduleWithFixedDelay(() -> semaforizeaza(()->poll("tufis", sched2)), 0, 1, SECONDS);

    ScheduledExecutorService sched3 = Executors.newScheduledThreadPool(1);
    sched3.scheduleWithFixedDelay(() -> semaforizeaza(()->poll("hartie", sched3)), 0, 1, SECONDS);

    ScheduledExecutorService sched4 = Executors.newScheduledThreadPool(1);
    sched4.scheduleWithFixedDelay(() -> semaforizeaza(()->poll("wc", sched4)), 0, 1, SECONDS);

    Thread.sleep(2000);
    pervLeft.add("x");
    pervLeft.add("x");
  }

  static Semaphore semaphore = new Semaphore(2);
  public static final LoggingMeterRegistry meterRegistry = new LoggingMeterRegistry();

  private static void semaforizeaza(Runnable r) {
    try {
      semaphore.acquire();
      long t0 = currentTimeMillis();
      r.run();
      long t1 = currentTimeMillis();
      meterRegistry.timer("task").record(t1 - t0, MILLISECONDS);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    } finally {
      semaphore.release();
    }
  }


  @SneakyThrows
  public static void poll(String type, ScheduledExecutorService executor) {
    var results = apiCall(2);
    log.info("🟢start:"+type);
    Thread.sleep(100);

    // (tokenNou,date) =  STC.fetch(tokenVechi)
    // hashMap.putAll(nisteDate(data))
    // static tokenNou

    log.info("🔴end:" + type+", got:"+results);
    if (results.size() == 2) {
      // follow-up ca poate mai sunt
      executor.schedule(() -> semaforizeaza(()->poll(type, executor)), 0, TimeUnit.MILLISECONDS);
    }
  }

  static List<String> pervLeft = new ArrayList<>(List.of("a", "b", "c", "d", "e", "f", "g"));

  public static List<String> apiCall(int maxDelta) {
    List<String> result = new ArrayList<>();
    for (int i = 0; i < maxDelta && !pervLeft.isEmpty(); i++) {
      result.add(pervLeft.remove(0));
    }
    return result;
  }

}
