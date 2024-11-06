package victor.training.performance.interview;

import victor.training.performance.util.PerformanceUtil;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

public class CollectionsSize {
   public static final int ONE_MILLION = 1_000_000;

   public static void main(String[] args) {
      // TODO order the following from lowest->highest memory consumption

      // A) ArrayList<Long>[1M] =  MB because:
//      measureHeap(() -> (ArrayList<Long>)LongStream.range(0, ONE_MILLION).boxed().collect(Collectors.toList()));

      // B) long[1M] =  MB because:
//      measureHeap(() -> (long[]) LongStream.range(0, ONE_MILLION).toArray());

      // C) HashSet[1M] =  MB because:
//      measureHeap(() -> (HashSet<Long>) LongStream.range(0, ONE_MILLION).boxed().collect(Collectors.toSet()));

      // D) LinkedList[1M] =  MB because:
//      measureHeap(() -> (LinkedList<Long>) LongStream.range(0, ONE_MILLION).boxed().collect(Collectors.toCollection(LinkedList::new)));

      // E) Long[1M] =  MB because:
//      measureHeap(() -> (Long[]) LongStream.range(0, ONE_MILLION).boxed().toArray(Long[]::new));

      // F) int[1M] =  MB because:
//      measureHeap(() -> (int[]) IntStream.range(0, ONE_MILLION).toArray());

      // TODO experiment .clear the collections and see if memory is released
   }

   public static void measureHeap(Supplier<Object> allocation) {
      long heap0 = PerformanceUtil.getUsedHeapBytes();
      Object x = allocation.get();
      long heap1 = PerformanceUtil.getUsedHeapBytes();
      System.out.println(PerformanceUtil.objectToString(x) + " [" + ONE_MILLION + " elements] " +
                         " occupies: " + (heap1 - heap0) / 1024 / 1024 + " MB");
   }

}
