package victor.training.performance.interview;

import victor.training.performance.util.PerformanceUtil;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class CollectionsSize {
  public static final int ONE_MILLION = 1_000_000;

  public static void main(String[] args) {
    // TODO order the following from lowest->highest memory consumption

    // F) int[1M] = 3 MB because:
//    int[] existingIds20M; int 2.000.000.000
    measureHeap(() -> (int[]) IntStream.range(0, ONE_MILLION).toArray());

    // B) long[1M] = 8 MB because: twice
    measureHeap(() -> (long[]) LongStream.range(0, ONE_MILLION).toArray());

    // E) Long[1M] = 27 MB because: new Long(~20b) on heap=20MB
    measureHeap(() -> (Long[]) LongStream.range(0, ONE_MILLION).boxed().toArray(Long[]::new));

//    List<String> list = Arrays.asList(new String[]{"a"});
//    new ArrayList<>(100_000);// initial capacity
    //.add(e) ~O(1) <=> when full it DOUBLE its capacity
//     new Vector<>(10,10).add(1);//  ~O(N) it increases capacity with constant factor+ it's sync

//     A)ArrayList<Long>[1M] = 31=11me+20heap MB because: of extra capacity
    measureHeap(() -> (ArrayList<Long>) LongStream.range(0, ONE_MILLION).boxed().collect(Collectors.toList()));

    // D) LinkedList[1M] = 46=26mb+20heap MB because: more object on heap+pointers
    measureHeap(() -> (LinkedList<Long>) LongStream.range(0, ONE_MILLION).boxed().collect(Collectors.toCollection(LinkedList::new)));

//     arrayList.remove(e); => use LinkedList.

    // C) HashSet[1M] = 70 MB because: buckets
    measureHeap(() -> (HashSet<Long>) LongStream.range(0, ONE_MILLION).boxed().collect(Collectors.toSet()));


    // Premature Optimization is the Root of all Evil - Donald Knuth
    // Optimize after it's clean.
    // Optimize with numbers in front of your.

    // Measure, don't Guess.

//    StringBuilder sb = new StringBuilder();
//    sb.append("a");
//    sb.append(args);
//    sb.append("b");
//    System.out.println(sb.toString());


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
