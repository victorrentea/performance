package victor.training.performance.interview;

import victor.training.performance.PerformanceUtil;

import java.util.stream.IntStream;

public class CompactCollections {

   public static void main(String[] args) {
      System.out.println(PerformanceUtil.getUsedHeap());
      // Use-case: I had to keep a large number of IDs throughout a long memory-intensive batch

//      Set<Long> ids = LongStream.range(0, 1_000_000).boxed().collect(Collectors.toSet()); // 76 MB

      // ArrayList
//      List<Long> ids = LongStream.range(0, 1_000_000).boxed().collect(Collectors.toList()); // 40 MB

//      Long[] ids = LongStream.range(0, 1_000_000).boxed().collect(Collectors.toList()).toArray(new Long[0]); // 38 M
      // Long[]
//      long[] ids = LongStream.range(0, 1_000_000).toArray(); //  18 MB
      // long[]
//      long[] ids = LongStream.range(0, 1_000_000).toArray(); //  18 MB

      int[] ids = IntStream.range(0, 1_000_000).toArray(); //  14 MB
      // int[]
      // LinkedList
      System.out.println("Allocated!");

      System.out.println(PerformanceUtil.getUsedHeap());
   }

}
