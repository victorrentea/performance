package victor.training.performance.interview;

import victor.training.performance.util.PerformanceUtil;

import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static java.util.stream.Collectors.toCollection;

public class CompactCollections {

   public static void main(String[] args) {
      // Use-case: you have to keep a large number of IDs throughout a long memory-intensive batch
      long heap0 = PerformanceUtil.getUsedHeapBytes();

      // HashSet =  MB because
      Set<Long> x = LongStream.range(0, 1_000_000).boxed().collect(Collectors.toSet());

      // ArrayList =  MB because
//      List<Long> x = LongStream.range(0, 1_000_000).boxed().collect(Collectors.toList());

      // Long[] =  MB because
//      Long[] x = LongStream.range(0, 1_000_000).boxed().toArray(Long[]::new);

      // LinkedList =  MB because
//      LinkedList<Long> x = LongStream.range(0, 1_000_000).boxed().collect(Collectors.toCollection(LinkedList::new));

      // long[] =  MB because
//      long[] x = LongStream.range(0, 1_000_000).toArray();

      // int[] =  MB because
//      int[] x = IntStream.range(0, 1_000_000).toArray();

      long heap1 = PerformanceUtil.getUsedHeapBytes();
      System.out.println("Object " + objectToString(x) + " occupies: " + (heap1-heap0)/1024/1024 + " MB");
   }

   private static String objectToString(Object x) {
      return x.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(x));
   }

}
