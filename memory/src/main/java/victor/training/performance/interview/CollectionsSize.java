package victor.training.performance.interview;

import victor.training.performance.util.PerformanceUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class CollectionsSize {

   public static final int ONE_MILLION = 1_000_000;

   public static void main(String[] args) {
      // Use-case: you have to keep a large number of IDs throughout a long memory-intensive batch
      long heap0 = PerformanceUtil.getUsedHeapBytes();

      // HashSet = 69 MB because
//      Set<Long> x = LongStream.range(0, ONE_MILLION).boxed().collect(Collectors.toSet());

      // ArrayList = 30 MB because
//      List<Long> x = LongStream.range(0, ONE_MILLION).boxed().collect(Collectors.toList());

      // int[] = 3 MB because // lasa asa ca e muuult  mai mic
//      int[] x = IntStream.range(0, ONE_MILLION).toArray();

      // LinkedList = 46 MB because
      LinkedList<Long> x = LongStream.range(0, ONE_MILLION).boxed().collect(Collectors.toCollection(LinkedList::new));

      // long[] = 8 MB because
//      long[] x = LongStream.range(0, ONE_MILLION).toArray();

      // Long[] = 27 MB because
//      Long[] x = LongStream.range(0, ONE_MILLION).boxed().toArray(Long[]::new);

      long heap1 = PerformanceUtil.getUsedHeapBytes();
      System.out.println("Collection(size="+ONE_MILLION+")" + PerformanceUtil.objectToString(x) + " occupies: " + (heap1 - heap0) / 1024 / 1024 + " MB");

//      x.clear();
      long heap2 = PerformanceUtil.getUsedHeapBytes();
      System.out.println("Collection.clear()ed) still occupies: " + (heap2 - heap0) / 1024 / 1024 + " MB");
   }

}
