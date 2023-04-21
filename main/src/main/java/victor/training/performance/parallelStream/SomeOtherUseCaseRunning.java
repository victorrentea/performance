package victor.training.performance.parallelStream;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.IntStream;

// =========== far away, in a distant Package ...... =============
@Slf4j
public class SomeOtherUseCaseRunning {
  @SneakyThrows
  public static void parallelRequest() {
    Thread thread = new Thread(SomeOtherUseCaseRunning::optimized);
    thread.setDaemon(true); // to exit program
    thread.start();
    Thread.sleep(100);
  }

  public static void optimized() {
    int result = IntStream.range(1, 1000)
            .parallel()
            .map(SomeOtherUseCaseRunning::callNetworkOrDB)
            .sum();
    System.out.println(result);
  }

  @SneakyThrows
  public static int callNetworkOrDB(int id) {
    log.debug("Blocking...");
    Thread.sleep(1000);
    return id * 2;
  }

}
