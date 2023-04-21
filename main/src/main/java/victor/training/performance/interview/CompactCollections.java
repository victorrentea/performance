package victor.training.performance.interview;

import victor.training.performance.util.PerformanceUtil;

import java.util.ArrayList;
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
    long heap0 = PerformanceUtil.getUsedHeapBytes();

    // === ORDER THE FOLLOWING ASCENDING BY EXPECTED SIZE

    // 8 MB because: 64bit
//     int[] x = IntStream.range(0, 1_000_000).toArray();

//    System.out.println(Integer.MAX_VALUE);
//    System.out.println(Long.MAX_VALUE);
    // 8 MB because:
//     long[] x = LongStream.range(0, 1_000_000).toArray();

    // 28 MB because: 20MB extra = the instances of new Long on the heap
//     Long[] x = LongStream.range(0, 1_000_000).boxed().toArray(Long[]::new);

    // 31  MB because ArrayList: +3 MBfor extra capacity
//     List<Long> x = LongStream.range(0, 1_000_000).boxed().collect(toList());

    // 28 MB because ArrayList:
//    List<Long> x = LongStream.range(0, 1_000_000).boxed().collect(Collectors.toCollection(
//             () -> new ArrayList<>(1000000)));

    // 45 MB because: NODE{prev/next} pointers
//     LinkedList<Long> x = LongStream.range(0, 1_000_000).boxed().collect(toCollection(LinkedList::new));

    //  65 MB because: the buckets of the internal HashMap
    Set<Long> x = LongStream.range(0, 1_000_000).boxed().collect(Collectors.toSet());




    long heap1 = PerformanceUtil.getUsedHeapBytes();
    System.out.println((heap1 - heap0) / 1024 / 1024 + " MB for " + objectToString(x));
  }

  private static String objectToString(Object x) {
    return x.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(x));
  }
  // Use-case: you have to keep a large number of IDs throughout a long memory-intensive batch
}
