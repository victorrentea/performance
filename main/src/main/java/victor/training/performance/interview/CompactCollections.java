package victor.training.performance.interview;

import victor.training.performance.util.PerformanceUtil;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class CompactCollections {

  public static void main(String[] args) {
    long heap0 = PerformanceUtil.getUsedHeapBytes();

    // === ORDER THE FOLLOWING ASCENDING BY EXPECTED SIZE

    //   MB because:
    Set<Long> x = LongStream.range(0, 1_000_000).boxed().collect(Collectors.toSet());

    //   MB because ArrayList:
    // List<Long> x = LongStream.range(0, 1_000_000).boxed().collect(toList());

    //  MB because:
    // Long[] x = LongStream.range(0, 1_000_000).boxed().toArray(Long[]::new);

    //  MB because:
    // long[] x = LongStream.range(0, 1_000_000).toArray();

    //  MB because:
    // int[] x = IntStream.range(0, 1_000_000).toArray();

    //  MB because:
    // LinkedList<Long> x = LongStream.range(0, 1_000_000).boxed().collect(toCollection(LinkedList::new));

    long heap1 = PerformanceUtil.getUsedHeapBytes();
    System.out.println((heap1 - heap0) / 1024 / 1024 + " MB for " + objectToString(x));
  }

  private static String objectToString(Object x) {
    return x.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(x));
  }
  // Use-case: you have to keep a large number of IDs throughout a long memory-intensive batch
}
