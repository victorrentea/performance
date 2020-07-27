package victor.training.performance;

import java.util.ArrayList;
import java.util.List;

import static victor.training.performance.ConcurrencyUtil.sleepq;

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
         sleepq(300);
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
         sleepq(300); // TODO reduce delay
      }
   }
}
