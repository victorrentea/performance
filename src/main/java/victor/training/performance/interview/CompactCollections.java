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
      var ids = IntStream.range(0, 1_000_000).toArray();
      // HashSet // 90 MB
      // ArrayList List<Int> 49
      // Long[] Array<Long?>
      // long[] Array<Long>
      // LinkedList // 65
      // int[] Array<Int> 21

      System.out.println("Allocated!");

      System.out.println(PerformanceUtil.getUsedHeap());
   }

}
