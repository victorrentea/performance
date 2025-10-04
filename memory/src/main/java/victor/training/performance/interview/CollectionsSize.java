package victor.training.performance.interview;

import victor.training.performance.util.PerformanceUtil;

import java.util.concurrent.Callable;

public class CollectionsSize {
  public static final int N_ELEMENTS = 1_000_000;

  public static void main(String[] args) {
    // TODO order the following from lowest->highest memory consumption

    // A) ArrayList<Long>[1M] =  MB because:
    // measureHeap(() -> (ArrayList<Long>)LongStream.range(0, ONE_MILLION).boxed().collect(Collectors.toList()));

    // B) long[1M] =  MB because:
    // measureHeap(() -> (long[]) LongStream.range(0, ONE_MILLION).toArray());

    // C) HashSet[1M] =  MB because:
    // measureHeap(() -> (HashSet<Long>) LongStream.range(0, ONE_MILLION).boxed().collect(Collectors.toSet()));

    // D) LinkedList[1M] =  MB because:
    // measureHeap(() -> (LinkedList<Long>) LongStream.range(0, ONE_MILLION).boxed().collect(Collectors.toCollection(LinkedList::new)));

    // E) Long[1M] =  MB because:
    // measureHeap(() -> (Long[]) LongStream.range(0, ONE_MILLION).boxed().toArray(Long[]::new));

    // F) int[1M] =  MB because:
    // measureHeap(() -> (int[]) IntStream.range(0, ONE_MILLION).toArray());

    // TODO extra experiment .clear the collections and see if memory is released
  }

  public static void measureHeap(Callable<Object> allocation) {
    var allocationResult = PerformanceUtil.measureAllocation(allocation);
    System.out.println(PerformanceUtil.objectToString(allocationResult.result()) +
                       " [" + N_ELEMENTS + " elements] " +
                       " occupies: " + allocationResult.deltaHeapBytes() / 1024 / 1024 + " MB");
  }



}
