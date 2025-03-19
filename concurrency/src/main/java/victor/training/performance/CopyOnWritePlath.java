package victor.training.performance;

import victor.training.performance.util.PerformanceUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.lang.System.currentTimeMillis;

public class CopyOnWritePlath {
  static List<String> consumers = new CopyOnWriteArrayList<>(new ArrayList<>());

  static public void connect(String consumer) {
    consumers.add(consumer);
  }

  static public void disconnect(String consumer) {
    consumers.remove(consumer);
  }

  static public void publish(String message) {
    System.out.println("Start publishing");
    try {
      for (String consumer : consumers) {
        publishToConsumer(message, consumer);
      }
      System.out.println("End publishing");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  static private void publishToConsumer(String message, String consumer) {
    PerformanceUtil.sleepMillis(2);
  }

  public static void main(String[] args) throws InterruptedException {
    connect("a");
    long t0 = currentTimeMillis();
    ExecutorService threadPool = Executors.newCachedThreadPool();
//    threadPool.submit(() -> {for (int i = 0; i < 100; i++) publish("Hello" + i);});
    for (int i = 0; i < 100; i++) threadPool.submit(() -> publish("Hello"));
//    PerformanceUtil.sleepMillis(50);
    connect("b");
    threadPool.shutdown();
    threadPool.awaitTermination(1, TimeUnit.MINUTES);
    long t1 = currentTimeMillis();
    System.out.println("Time: " + (t1 - t0));
  }

  //
}
