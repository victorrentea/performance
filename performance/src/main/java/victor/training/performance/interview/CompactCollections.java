package victor.training.performance.interview;

import victor.training.performance.PerformanceUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class CompactCollections {

   public static void main(String[] args) {
//      List<Long> collect = LongStream.range(0, 1_000_000).boxed().collect(Collectors.toList());

//      long[] list = LongStream.range(0, 1_000_000).toArray();
      // ArrayList<Long> 35.3  <<<< ArrayList grows by
      // Long[] 34.5
      // long[] 11.8 M

      // DEAR MAINTAINER: I am sorry for this [], but there are 2M elements to keep in memory for 3hours (batch exec). This is the most compact datastructure to avoid OOM Error
//      int[] ids = IntStream.range(0, 1_000_000).toArray();
      // TODO HashSet 70
      // TODO LinkedList 46 vs ArrayList
      LinkedList<Integer> collect = IntStream.range(0, 1_000_000).boxed().collect(Collectors.toCollection(LinkedList::new));

      // TODO int[] 7

      System.out.println("Allocated!");

      System.out.println(PerformanceUtil.getUsedHeap());
   }

}
