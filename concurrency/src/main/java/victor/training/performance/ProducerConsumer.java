package victor.training.performance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.synchronizedList;

public class ProducerConsumer {
  private static final List<Integer> buffer = Collections.synchronizedList(new ArrayList<>());

  public static void main(String[] args) {
    ExecutorService submitteri = Executors.newCachedThreadPool();
    for (int i = 0; i < 100; i++) {
      submitteri.submit(() -> {
        for (int j = 0; true; j++) {
          buffer.add(j);
          Thread.sleep(10);
        }
      });

    }
    var sc = Executors.newScheduledThreadPool(1);
    sc.scheduleAtFixedRate(() -> {
      try {
        List<Integer> copie;
        synchronized (buffer) {
           copie = new ArrayList<>(buffer);
           buffer.clear();
        }
        for (Integer e : copie) {
          counter += e;
        }
        System.out.println("Am acumulat " + counter);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }, 1, 1, TimeUnit.SECONDS);
    System.out.println("Started...");
  }

  static int counter;
}
