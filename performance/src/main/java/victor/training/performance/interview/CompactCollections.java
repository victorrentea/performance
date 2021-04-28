package victor.training.performance.interview;

import victor.training.performance.PerformanceUtil;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class CompactCollections {

   public static void main(String[] args) {
      List<Long> list = LongStream.range(0, 1_000_000).boxed().collect(Collectors.toList());
      // TODO HashSet
      // TODO LinkedList
      // TODO Long[]
      // TODO long[] .stream().mapToLong(i -> i).toArray();
      // TODO int[]
      System.out.println("Allocated!");

      System.out.println(PerformanceUtil.getUsedHeap());
   }

}
