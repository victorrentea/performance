package victor.training.performance.front;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
public class FrontPollersPaginate {
  static ExecutorService executor = Executors.newFixedThreadPool(1);
//  static ForkJoinPool executor = new ForkJoinPool(Runtime.getRuntime().availableProcessors());

  @SneakyThrows
  public static void main(String[] args) {
    executor.submit(() -> poll());
  }

  @SneakyThrows
  public static void poll() {
    var results = apiCall(2);
    System.out.println(results);
    if (results.size() == 2) {
      executor.submit(() -> poll()).get();
    }
  }

  static List<String> pervLeft = new ArrayList<>(List.of("a","b","c","d","e","f","g"));

  public static List<String> apiCall(int maxDelta) {
    List<String> result = new ArrayList<>();
    for (int i = 0; i < maxDelta && !pervLeft.isEmpty(); i++) {
      result.add(pervLeft.remove(0));
    }
    return result;
  }

}
