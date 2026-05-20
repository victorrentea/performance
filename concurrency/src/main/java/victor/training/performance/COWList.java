package victor.training.performance;

import victor.training.performance.util.PerformanceUtil;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;

public class COWList {
  public static void main(String[] args) throws InterruptedException {
    List<String> consumers = new CopyOnWriteArrayList<>();
    consumers.add("A");
    Runnable task = () -> {
      try {
        System.out.println("Start publishing");
        int count = 0;
//        for (Iterator<String> iterator = consumers.iterator(); iterator.hasNext(); ) {
        for (String consumer : consumers) {
          PerformanceUtil.sleepMillis(2);
          count++;
        }
        System.out.println("Published to consumers count: " + count);
      } catch (Exception e) {
        e.printStackTrace();
      }
    };

    var threadPool = Executors.newCachedThreadPool();
    for (int i = 0; i < 100; i++) threadPool.submit(task);
    consumers.add("B"); // concurrent change to the list

    threadPool.shutdown();
  }
}
