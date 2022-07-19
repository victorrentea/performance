package victor.training.performance.interview;

import victor.training.performance.util.PerformanceUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class CompactCollections {

   public static void main(String[] args) {
      // Use-case: I had to keep a large number of IDs throughout a long memory-intensive batch
      System.out.println(PerformanceUtil.getUsedHeap());

      // IMI PARE RAU. DAR TRE SA TIN 2.5 MLN de iduri pentru 20 min in memorie
      int[] ids = IntStream.range(0, 1_000_000).toArray();
//      LinkedList<Integer> linkedList = IntStream.range(0, 1_000_000).boxed().collect(Collectors.toCollection(LinkedList::new));

      // HashSet ..  90 MB
      // LinkedList ..58 MB (mult pt ca are pointeri la elem inainte / dupa
      // ArrayList .. 50 MB .. ca n-are bucketuri, linked list in bucketuri
      // Long[] .. 45 MB  .. mai putin ca n-are capacitate extra
      // long[] .. 25 MB .. pt ca tine direct valorile, nu mai ai new Long pe heap
      // int[] .. 21 MB .. pt ca e doar 4 octeti nu 8
      System.out.println("Allocated!");

      System.out.println(PerformanceUtil.getUsedHeap());
   }

}
