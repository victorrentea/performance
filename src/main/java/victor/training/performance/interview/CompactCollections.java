package victor.training.performance.interview;

import victor.training.performance.util.PerformanceUtil;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class CompactCollections {

   public static void main(String[] args) {
      // Use-case: I had to keep a large number of IDs throughout a long memory-intensive batch
      long heap0 = PerformanceUtil.getUsedHeapBytes();

      Set<Long> ids = LongStream.range(0, 1_000_000).boxed().collect(Collectors.toSet());
      // HashSet ..  MB because
      // ArrayList .. MB because
      // Long[] .. MB because
      // long[] .. MB because
      // int[] .. MB because
      // LinkedList .. MB because
      System.out.println("Allocated!");

      long heap1 = PerformanceUtil.getUsedHeapBytes();
      System.out.println("Occupies: " + (heap1-heap0)/1024/1024 + " MB");
   }

}
