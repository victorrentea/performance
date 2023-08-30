package victor.training.performance.interview;

import victor.training.performance.util.PerformanceUtil;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class CompactCollections {
   class Ta {
      int x;
      int a;

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (o == null || getClass() != o.getClass()) return false;
         Ta ta = (Ta) o;
         return x == ta.x && a == ta.a;
      }

      @Override
      public int hashCode() {
         return Objects.hash(x, a);
      }
   }

   public static final int ONE_MILLION = 1_000_000;

   public static void main(String[] args) {
      // Use-case: you have to keep a large number of IDs throughout a long memory-intensive batch
      long heap0 = PerformanceUtil.getUsedHeapBytes();

//      HashSet<Long> = 65 MB because
//      Set<Long> x = LongStream.range(0, ONE_MILLION).boxed().collect(Collectors.toSet());

      // LinkedList<Long> = 45 MB because
      LinkedList<Long> x = LongStream.range(0, ONE_MILLION).boxed().collect(Collectors.toCollection(LinkedList::new));

      // ArrayList<Long> = 31 MB because ca are CAPACITATE nefolosita 4MB
//      List<Long> x = LongStream.range(0, ONE_MILLION).boxed().collect(Collectors.toList());

      // Long[] = 27 MB because WTF?! -> pt ca pe langa arrayul de pointeri, mai ai si 19MB pt 1M de Long instance =>19b instanta Long
//      Long[] x = LongStream.range(0, ONE_MILLION).boxed().toArray(Long[]::new);

      // long[] = 8 MB because 64 biti
//      long[] x = LongStream.range(0, ONE_MILLION).toArray();

      // int[] = 4 MB because
//      int[] x = IntStream.range(0, ONE_MILLION).toArray();

      long heap1 = PerformanceUtil.getUsedHeapBytes();
      System.out.println("Object " + PerformanceUtil.objectToString(x) + " occupies: " + (heap1 - heap0) / 1024 / 1024 + " MB");
   }

}
