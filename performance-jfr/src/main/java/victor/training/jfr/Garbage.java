package victor.training.jfr;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Garbage {
   public static void main(String[] args) {
//      System.out.println("[ENTER] when ready");
//      new Scanner(System.in).nextLine();
//      System.out.println("Start...");
      String s = "";
      long t0 = System.currentTimeMillis();
      for (int i = 0; i <  50_000; i++) {
         s += i + " ";
//         writer.write(i + " "); better option because avoids creating laaarge obhects in memory and flushes data out of your heap (eg to DISk, netword, DB)
         if (i % 1000 == 0) {
            System.out.println(i);
         }
      }
      long t1 = System.currentTimeMillis();

      System.out.println("Took " + (t1-t0));

      //
//      List<Integer> list = new ArrayList<>();
//      for (int i =0;i<100_000;i++) {
//         list.add(i);
//      }
      System.out.println(s.length());
//      IOUtils.write(s,file)




      // you do this 100/sec
      List<String> list1 = new ArrayList<>(); // large listst (1000+)
      List<String> list2 = new ArrayList<>();

      for (String s1 : list2) {
         list1.add(s1); // cause progressive large allocations
      }
      // preallocate one shot everything
      List<String> result = new ArrayList<>(list1.size() + list2.size());
      result.addAll(list1);
      result.addAll(list2);

      //1M --> 1M jumps to do by the GC --> Lnked lIst kills your GC

   }
}
