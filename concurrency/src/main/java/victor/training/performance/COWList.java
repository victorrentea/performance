package victor.training.performance;

import victor.training.performance.util.PerformanceUtil;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class COWList {
  public static void main(String[] args) throws InterruptedException {
    List<String> consumers = new CopyOnWriteArrayList<>();
    consumers.add("A");
    Runnable task = () -> {
      System.out.println("Start publishing");
      for (String consumer : consumers) {
        PerformanceUtil.sleepMillis(2);
      }
      System.out.println("End publishing");
    };

    var threadPool = Executors.newCachedThreadPool();
    for (int i = 0; i < 100; i++) threadPool.submit(task);
    consumers.add("B");
    threadPool.shutdown();
    threadPool.awaitTermination(1, TimeUnit.MINUTES);
  }
}
