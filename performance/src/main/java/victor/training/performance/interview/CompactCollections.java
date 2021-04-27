package victor.training.performance.interview;

import victor.training.performance.PerformanceUtil;

import java.util.ArrayList;
import java.util.List;

public class CompactCollections {


   public static void main(String[] args) {
      List<Long> list = generate();
      System.out.println("Allocated " + list.hashCode());
      System.gc();

      PerformanceUtil.printUsedHeap();
   }

   private static List<Long> generate() {
      List<Long> list = new ArrayList<>();
      for (long i = 0; i < 1_000_000; i++) {
         list.add(i);
      }
      return list;
      //Tp: .stream().mapToInt(i -> i.intValue()).toArray();
   }
}
