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
      long heap0 = PerformanceUtil.getUsedHeapBytes();
      // 3 MB
//      int[] x = IntStream.range(0, ONE_MILLION).toArray();

      // 8 MB
//      long[] x = LongStream.range(0, ONE_MILLION).toArray();

      // 27 MB de ce: 8b + pointer la long 8byte + RefCount (new) 4byte
//      Long[] x = LongStream.range(0, ONE_MILLION).boxed().toArray(Long[]::new);

      // 31 MB because ArrayList
//      List<Long> x = LongStream.range(0, ONE_MILLION).boxed().collect(Collectors.toList());

      // 46 MB de la pointerii de next/prev
//      LinkedList<Long> x = LongStream.range(0, ONE_MILLION).boxed().collect(Collectors.toCollection(LinkedList::new));

      // 69 MB because HashSet are galeti (buckets) = 20x mai mult decati intzii
      Set<Long> x = LongStream.range(0, ONE_MILLION).boxed().collect(Collectors.toSet());




      long heap1 = PerformanceUtil.getUsedHeapBytes();
      System.out.println("Collection(size="+ONE_MILLION+")" + PerformanceUtil.objectToString(x) + " occupies: " + (heap1 - heap0) / 1024 / 1024 + " MB");

//      x.clear();
//      long heap2 = PerformanceUtil.getUsedHeapBytes();
//      System.out.println("Collection.clear()ed) still occupies: " + (heap2 - heap0) / 1024 / 1024 + " MB");
   }

}
