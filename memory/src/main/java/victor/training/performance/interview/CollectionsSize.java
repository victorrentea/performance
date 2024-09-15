package victor.training.performance.interview;

import victor.training.performance.util.PerformanceUtil;

import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class CollectionsSize {
  public static final int ONE_MILLION = 1_000_000;

  public static void measureHeap(Supplier<Object> allocation) {
    long heap0 = PerformanceUtil.getUsedHeapBytes();
    Object x = allocation.get();
    long heap1 = PerformanceUtil.getUsedHeapBytes();
    System.out.println(
        "Collection(size=" + ONE_MILLION + ")"
        + PerformanceUtil.objectToString(x) +
        " occupies: " + (heap1 - heap0) / 1024 / 1024 + " MB");
  }

  public static void main(String[] args) {
    long heap0 = PerformanceUtil.getUsedHeapBytes();
    // low->high

    // int[] 3 MB (30x smaller than Set<Long>)
    measureHeap(() -> (int[]) IntStream.range(0, ONE_MILLION).toArray());

    // long[] 8 MB
    measureHeap(() -> (long[]) LongStream.range(0, ONE_MILLION).toArray());

    // Long[] 27 MB because of boxing (more objects on heap)
    measureHeap(() -> (Long[]) LongStream.range(0, ONE_MILLION).boxed().toArray(Long[]::new));
//      int[] x = IntStream.range(0, ONE_MILLION).toArray();
//      long[] x = LongStream.range(0, ONE_MILLION).toArray();
//      Long[] x = LongStream.range(0, ONE_MILLION).boxed().toArray(Long[]::new);
    // 31 MB because ArrayList keeps extra capacity
//      List<Long> x = LongStream.range(0, ONE_MILLION).boxed().collect(Collectors.toList());
    // 46 MB
//      LinkedList<Long> x = LongStream.range(0, ONE_MILLION).boxed().collect(Collectors.toCollection(LinkedList::new));
    // 69 MB because HashSet
    Set<Long> x = LongStream.range(0, ONE_MILLION).boxed().collect(Collectors.toSet());
    //if  SHA512 (previuoslyReceivedXml).equals(SHA512 (newXml)) { skip }
//      x.toArr

//    long heap1 = PerformanceUtil.getUsedHeapBytes();
//    System.out.println("Collection(size=" + ONE_MILLION + ")" + PerformanceUtil.objectToString(x) + " occupies: " + (heap1 - heap0) / 1024 / 1024 + " MB");
//
//    x.clear();
//    long heap2 = PerformanceUtil.getUsedHeapBytes();
//    System.out.println("Collection.clear()ed) still occupies: " + (heap2 - heap0) / 1024 / 1024 + " MB");
  }

}
