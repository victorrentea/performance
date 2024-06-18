package victor.training.performance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Collections.synchronizedList;

public class ProducerConsumer {
  private static List<Integer> buffer = new ArrayList<>();

  public static void main(String[] args) {
    ExecutorService submitteri = Executors.newCachedThreadPool();
    for (int i = 0; i < 100; i++) {
      submitteri.submit(() -> {
        for (int j = 0; true; j++) {
          synchronized (ProducerConsumer.class) {
            buffer.add(j);
          }
          Thread.sleep(10);
        }
      });

    }
    var sc = Executors.newScheduledThreadPool(1);
    sc.scheduleAtFixedRate(() -> {
      try {
        List<Integer> newBuffer = new ArrayList<>();
        List<Integer> copie;
        synchronized (ProducerConsumer.class) {
           copie = buffer;
           buffer = newBuffer;
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
