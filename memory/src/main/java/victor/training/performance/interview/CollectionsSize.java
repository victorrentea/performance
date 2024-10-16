package victor.training.performance.interview;

import victor.training.performance.util.PerformanceUtil;

import java.util.function.Supplier;
import java.util.stream.IntStream;

public class CollectionsSize {
   public static final int ONE_MILLION = 1_000_000;

   public static void main(String[] args) {
      // TODO order the following from lowest->highest memory consumption

      // F) int[1M] = 3 MB because:
      // AM folosit int[] ca ocupa mult mai putina memorie
      measureHeap(() -> (int[]) IntStream.range(0, ONE_MILLION).toArray());

//      // B) long[1M] = 8 MB because:
//      measureHeap(() -> (long[]) LongStream.range(0, ONE_MILLION).toArray());

//      // E) Long[1M] = 27 MB because: nu e doar 8 byte/long ci de acum sunt obiecte new / HEAP + RC+pointeri
//      measureHeap(() -> (Long[]) LongStream.range(0, ONE_MILLION).boxed().toArray(Long[]::new));

      // A) ArrayList[1M] = 31 MB because: + capacity goala
//      measureHeap(() -> (ArrayList<Long>) LongStream.range(0, ONE_MILLION).boxed().collect(toList()));

//      // D) LinkedList[1M] = 46 MB because: pointeri inainte/inapoi
//      measureHeap(() -> (LinkedList<Long>) LongStream.range(0, ONE_MILLION).boxed().collect(toCollection(LinkedList::new)));

//      // C) HashSet[1M] = 69 MB because: galetzi
//      measureHeap(() -> (HashSet<Long>) LongStream.range(0, ONE_MILLION).boxed().collect(Collectors.toSet()));

//


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
