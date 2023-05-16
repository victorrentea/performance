package victor.training.performance.concurrency.primitives;

import java.util.ArrayList;
import java.util.List;

import static victor.training.spring.batch.util.PerformanceUtil.sleepMillis;

public class CopyOnWrite {

   public static void main(String[] args) {
      List<Integer> list = new ArrayList<>();
      Thread thread = new Thread(new Builder(list));
      thread.start();
      while (thread.isAlive()) {
         String s = "";
         for (Integer e : list) {
            // TODO add delay
            s += e + " ";
         }
         sleepMillis(300);
         System.out.println(s);
      }
   }
}

class Builder implements Runnable {
   private final List<Integer> list;

   public Builder(List<Integer> list) {
      this.list = list;
   }

   @Override
   public void run() {
      for (int i = 0; i < 1000; i++) {
         list.add(i);
         sleepMillis(300); // TODO reduce delay
      }
   }
}
