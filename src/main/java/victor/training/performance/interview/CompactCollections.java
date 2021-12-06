package victor.training.performance.interview;

import victor.training.performance.util.PerformanceUtil;

import java.util.stream.IntStream;

public class CompactCollections {

   public static void main(String[] args) {
      // Use-case: I had to keep a large number of IDs throughout a long memory-intensive batch

//      Set<Long> ids = LongStream.range(0, 1_000_000).boxed().collect(Collectors.toSet()); //76 MB
//      List<Long> ids = LongStream.range(0, 1_000_000).boxed().collect(Collectors.toList()); // 40
//      Long[] ids = LongStream.range(0, 1_000_000).boxed().toArray(Long[]::new); // 28
//      long[] ids = LongStream.range(0, 1_000_000).toArray(); // 18 MB
      int[] ids = IntStream.range(0, 1_000_000).toArray(); // 14
      // int[]
      // LinkedList
      System.out.println("Allocated!");

      System.out.println(PerformanceUtil.getUsedHeap());
   }

}
