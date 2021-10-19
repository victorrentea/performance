package victor.training.performance.interview;

import victor.training.performance.util.PerformanceUtil;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class CompactCollections {

   public static void main(String[] args) {
      // Use-case: I had to keep a large number of IDs throughout a long memory-intensive batch

      Set<Long> ids = LongStream.range(0, 1_000_000).boxed().collect(Collectors.toSet());
      // HashSet
      // ArrayList
      // Long[]
      // long[]
      // int[]
      // LinkedList
      System.out.println("Allocated!");

      System.out.println(PerformanceUtil.getUsedHeap());
   }

}
