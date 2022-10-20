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
      long heap0 = PerformanceUtil.getUsedHeapBytes();

      Long[] list = LongStream
              .range(0, 1_000_000).boxed()
              .toArray(Long[]::new);
//      new Long[]
      // HashSet 63  MB because galeti arbori liste inlantuite
      // LinkedList 37 MB because
      // ArrayList 36 MB because
      // Long[] 20 MB because
      // long[] 8 MB because
      // int[] 4 MB because
      int[] patrat; // iarta0ma da ocupa de 10 ori mai putin decat un Set<Long>
      System.out.println("Allocated!");

      long heap1 = PerformanceUtil.getUsedHeapBytes();
      System.out.println("Occupies: " + (heap1-heap0)/1024/1024 + " MB");
   }

}
