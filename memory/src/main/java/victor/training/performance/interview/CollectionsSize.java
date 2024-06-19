package victor.training.performance.interview;

import victor.training.performance.util.PerformanceUtil;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class CollectionsSize {
   public static final int ONE_MILLION = 1_000_000;

   public static void main(String[] args) {
      long heap0 = PerformanceUtil.getUsedHeapBytes();

      //  MB because HashSet
      Set<Long> x = LongStream.range(0, ONE_MILLION).boxed().collect(Collectors.toSet());

      //  MB because ArrayList
//      List<Long> x = LongStream.range(0, ONE_MILLION).boxed().collect(Collectors.toList());

      //  MB
//      int[] x = IntStream.range(0, ONE_MILLION).toArray();

      //  MB
//      Long[] x = LongStream.range(0, ONE_MILLION).boxed().toArray(Long[]::new);

      //  MB
//      LinkedList<Long> x = LongStream.range(0, ONE_MILLION).boxed().collect(Collectors.toCollection(LinkedList::new));

      //  MB
//      long[] x = LongStream.range(0, ONE_MILLION).toArray();

      long heap1 = PerformanceUtil.getUsedHeapBytes();
      System.out.println("Collection(size="+ONE_MILLION+")" + PerformanceUtil.objectToString(x) + " occupies: " + (heap1 - heap0) / 1024 / 1024 + " MB");

//      x.clear();
//      long heap2 = PerformanceUtil.getUsedHeapBytes();
//      System.out.println("Collection.clear()ed) still occupies: " + (heap2 - heap0) / 1024 / 1024 + " MB");
   }

}
