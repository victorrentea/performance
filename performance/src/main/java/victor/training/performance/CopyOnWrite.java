package victor.training.performance;

import java.util.ArrayList;
import java.util.List;

import static victor.training.performance.ConcurrencyUtil.sleep2;

public class CopyOnWrite {

   public static void main(String[] args) {
      List<Integer> list = new ArrayList<>();
      Thread thread = new Thread(new Builder(list));
      thread.start();
      while (thread.isAlive()) {
         String s = "";
         for (Integer e : list) {
            sleep2(20);
            s += e + " ";
         }
         sleep2(300);
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
         sleep2(300);
      }
   }
}
