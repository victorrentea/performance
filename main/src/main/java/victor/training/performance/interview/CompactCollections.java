package victor.training.performance.interview;

import victor.training.performance.util.PerformanceUtil;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class CompactCollections {

   public static final int ONE_MILLION = 1_000_000;

   public static void main(String[] args) {
      // Use-case: you have to keep a large number of IDs throughout a long memory-intensive batch
      long heap0 = PerformanceUtil.getUsedHeapBytes();
// less memory : A or B
//      HashSet<Long> = 65 MB because of buckers, array lists, balanced trees Entry<>
//      Set<Long> x = LongStream.range(0, ONE_MILLION).boxed().collect(Collectors.toSet());

      // LinkedList<Long> = 46 MB because B
//      LinkedList<Long> x = LongStream.range(0, ONE_MILLION).boxed().collect(Collectors.toCollection(LinkedList::new));

      // ArrayList<Long> = 31 MB because of additional capacity
//      List<Long> x = LongStream.range(0, ONE_MILLION).boxed().collect(Collectors.toList());

      // Long[] = 27 MB because we are keeping pointers to wrappers, not just primitives
//      Long[] x = LongStream.range(0, ONE_MILLION).boxed().toArray(Long[]::new);

      // long[] = 8 MB because
//      long[] x = LongStream.range(0, ONE_MILLION).toArray();

      // int[] = 4 MB because
      int[] x = IntStream.range(0, ONE_MILLION).toArray();

      long heap1 = PerformanceUtil.getUsedHeapBytes();
      System.out.println("Collection(size="+ONE_MILLION+")" + PerformanceUtil.objectToString(x) + " occupies: " + (heap1 - heap0) / 1024 / 1024 + " MB");

//      x.clear();
      long heap2 = PerformanceUtil.getUsedHeapBytes();
      System.out.println("Collection.clear()ed still occupies: " + (heap2 - heap0) / 1024 / 1024 + " MB");
   }

}
