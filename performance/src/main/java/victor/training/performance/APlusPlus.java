package victor.training.performance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class APlusPlus {
   private static final Object monitor = new Object();
   private static AtomicInteger populationGlobal = new AtomicInteger();
    private static List<Integer> listGlobal = Collections.synchronizedList(new ArrayList<>());

    public static class ThreadA extends Thread {
        public void run() {
            int population = 0;
            List<Integer> list = new ArrayList<>();
            for (int i = 0; i < 100_000; i++) {
                population++;
                list.add(i);
            }
            populationGlobal.addAndGet(population);
            listGlobal.addAll(list);
        }
    }

    public static class ThreadB extends Thread {
        public void run() {
            int population = 0;
            List<Integer> list = new ArrayList<>();
            for (int i = 0; i < 100_000; i++) {
                population ++;
                list.add(i);
            }
            populationGlobal.addAndGet(population);
            listGlobal.addAll(list);
        }
    }
   public static void main(String[] args) throws InterruptedException {
      ThreadA threadA = new ThreadA();
      ThreadB threadB = new ThreadB();

      long t0 = System.currentTimeMillis();
//        synchronized (APlusPlus.class) {
//
//        }
      threadA.start();
      threadB.start();
      threadA.join();
      threadB.join();

      long t1 = System.currentTimeMillis();
      System.out.println("Total = " + populationGlobal);
       System.out.println("Total = " + listGlobal.size());
      System.out.println("Took = " + (t1 - t0));
   }


}
