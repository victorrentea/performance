package victor.training.performance.front;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class FrontPollersPaginateAndSchedule {

  @SneakyThrows
  public static void main(String[] args) {
    ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    scheduledExecutorService.scheduleWithFixedDelay(() -> poll("wc", scheduledExecutorService),
        500, 1000, TimeUnit.MILLISECONDS);

    CompletableFuture.runAsync(() -> {
      pervLeft.add("x");
    }, CompletableFuture.delayedExecutor(2000,TimeUnit.MILLISECONDS));
  }

  @SneakyThrows
  public static void poll(String type, ScheduledExecutorService executor) {
    var results = apiCall(2);
    System.out.println(results);
    if (results.size() == 2) {
      executor.schedule(() -> poll(type, executor),0, TimeUnit.MILLISECONDS);
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
