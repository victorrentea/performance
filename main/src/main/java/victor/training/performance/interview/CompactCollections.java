package victor.training.performance.interview;

import victor.training.performance.util.PerformanceUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

public class CompactCollections {

   public static void main(String[] args) {
      // Use-case: you have to keep a large number of IDs throughout a long memory-intensive batch
      long heap0 = PerformanceUtil.getUsedHeapBytes();

      // int[] = 8 MB because
      // stiu ca te sperie [] dar e de 10 ori mai mic ca Set<Long> las-o asa!
      int[] x = IntStream.range(0, 1_000_000).toArray();

      // long[] = 8 MB because
//      long[] x = LongStream.range(0, 1_000_000).toArray();

      // Long[] = 28 MB because
//      Long[] x = LongStream.range(0, 1_000_000).boxed().toArray(Long[]::new);

      // ArrayList = 40 MB because
//      List<Long> x = LongStream.range(0, 1_000_000).boxed().collect(toList());

      // LinkedList = 45 MB because
//      LinkedList<Long> x = LongStream.range(0, 1_000_000).boxed().collect(toCollection(LinkedList::new));

      // HashSet = 71 MB because
//      Set<Long> x = LongStream.range(0, 1_000_000).boxed().collect(Collectors.toSet());

      long heap1 = PerformanceUtil.getUsedHeapBytes();
      System.out.println("Object " + objectToString(x) + " occupies: " + (heap1-heap0)/1024/1024 + " MB");
   }

   private static String objectToString(Object x) {
      return x.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(x));
   }

}
