package victor.training.performance;

import victor.training.performance.util.PerformanceUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class COWList {
  public static void main(String[] args) throws InterruptedException {
    List<String> rachete = new ArrayList<>();
    rachete.add("A");
    Runnable task = () -> {
      System.out.println("Start publishing");
      for (String e : rachete) {
        PerformanceUtil.sleepMillis(2);
        System.out.println("Racheta pe cer: " + e);
      }
      System.out.println("End publishing");
    };

    // sched
    var threadPool = Executors.newCachedThreadPool();
    for (int i = 0; i < 100; i++) threadPool.submit(task);
    rachete.add("B"); // concurrent change to the list

    threadPool.shutdown();
  }
}
