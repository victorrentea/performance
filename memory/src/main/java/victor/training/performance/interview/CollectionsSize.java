package victor.training.performance.interview;

import victor.training.performance.util.PerformanceUtil;

import java.util.LinkedList;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class CollectionsSize {
  public static final int ONE_MILLION = 1_000_000;

  public static void main(String[] args) {
    // TODO order the following from lowest->highest memory consumption

    // F) int[1M] = 3 MB because:; maxint = 2 miliarde
//     measureHeap(() -> (int[]) IntStream.range(0, ONE_MILLION).toArray());

    // B) long[1M] = 8 MB because: 8b vs 4b
//     measureHeap(() -> (long[]) LongStream.range(0, ONE_MILLION).toArray());

    // E) Long[1M] = 26 MB because: acum am pe heap 1M de instante + refcount
//     measureHeap(() -> (Long[]) LongStream.range(0, ONE_MILLION).boxed().toArray(Long[]::new));

    // A) ArrayList<Long>[1M] = 31 MB because: + capacity gol ;
    // time vs memory tradeoff: BlueGene l-a batut pe Kasparov
//     measureHeap(() -> (ArrayList<Long>) LongStream.range(0, ONE_MILLION).boxed().collect(Collectors.toList()));

    // D) LinkedList[1M] = 46 MB because: prev+next
     measureHeap(() -> (LinkedList<Long>) LongStream.range(0, ONE_MILLION).boxed().collect(Collectors.toCollection(LinkedList::new)));

    // C) HashSet[1M] = 70 MB because: BUCKETS
//     measureHeap(() -> (HashSet<Long>) LongStream.range(0, ONE_MILLION).boxed().collect(Collectors.toSet()));


    // TODO extra experiment .clear the collections and see if memory is released
  }

  public static void measureHeap(Supplier<Object> allocation) {
    long heap0 = PerformanceUtil.getUsedHeapBytes();
    Object x = allocation.get();
    long heap1 = PerformanceUtil.getUsedHeapBytes();
    System.out.println(PerformanceUtil.objectToString(x) + " [" + ONE_MILLION + " elements] " +
                       " occupies: " + (heap1 - heap0) / 1024 / 1024 + " MB");
  }

}
