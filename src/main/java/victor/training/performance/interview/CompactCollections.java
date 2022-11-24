package victor.training.performance.interview;

import io.vavr.collection.Stream;
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

      // HashSet 70  MB because buckets
      Set<Long> x = LongStream.range(0, 1_000_000).boxed().collect(Collectors.toSet());

      // ArrayList 40 MB because ... are capacitate preaalocata ca sa poti adauga elemente usor, fara sa fii nevoit sa realoci si copiezi arrayul vechi
//      List<Long> x = LongStream.range(0, 1_000_000).boxed().collect(toList());

      // Long[] 28 MB because ...
//      Long[] x = LongStream.range(0, 1_000_000).boxed().toArray(Long[]::new);

      // long[] .. MB because
//      long[] x = LongStream.range(0, 1_000_000).toArray();

      // int[] .. MB because
      // iarta-ma. dar tre sa tiun 10M de id-uri pentru 30 min+
//      int[] x = IntStream.range(0, 1_000_000).toArray();

      // LinkedList .. MB because
//      LinkedList<Long> x = LongStream.range(0, 1_000_000).boxed().collect(toCollection(LinkedList::new));

      long heap1 = PerformanceUtil.getUsedHeapBytes();
      System.out.println("Object " + objectToString(x) + " occupies: " + (heap1-heap0)/1024/1024 + " MB");
   }

   private static String objectToString(Object x) {
      return x.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(x));
   }

}
