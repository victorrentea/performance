package victor.training.performance.front;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
public class FrontPollersPaginateAndSchedule {

  @SneakyThrows
  public static void main(String[] args) {
//    ScheduledExecutorService sched1 = Executors.newSingleThreadScheduledExecutor();
//    sched1.scheduleWithFixedDelay(() -> poll("🚀", sched1), 0, 1, SECONDS);

    ScheduledExecutorService sched2 = Executors.newScheduledThreadPool(1);
    sched2.scheduleWithFixedDelay(() -> poll("tufis", sched2), 0, 1, SECONDS);
//    ScheduledExecutorService sched3 = Executors.newSingleThreadScheduledExecutor();
    sched2.scheduleWithFixedDelay(() -> poll("hartie", sched2), 0, 1, SECONDS);

    Thread.sleep(2000);
    pervLeft.add("x");
    pervLeft.add("x");
//    pervLeft.add("x");
  }

  @SneakyThrows
  public static void poll(String type, ScheduledExecutorService executor) {
    var results = apiCall(2);
    log.info("start:"+type);
    Thread.sleep(100);
    log.info("end:" + type+", got:"+results);
    if (results.size() == 2) {
      executor.schedule(() -> poll(type, executor), 0, TimeUnit.MILLISECONDS);
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
