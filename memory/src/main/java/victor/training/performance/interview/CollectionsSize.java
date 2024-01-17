package victor.training.performance.interview;

import victor.training.performance.util.PerformanceUtil;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class CollectionsSize {

   public static final int ONE_MILLION = 1_000_000;

   public static void main(String[] args) {
      // Use-case: you have to keep a large number of IDs throughout a long memory-intensive batch
      long heap0 = PerformanceUtil.getUsedHeapBytes();
// ordonati urmatoarele colectii de la CEA MAI MULTA memorie ocupata ---> cea mai compacta.


      // HashSet = 69 MB because: ia bucket, pt ca in HashSet e un HashMap, si in HashMap e un Entry, si in Entry sunt 3 referinte
      HashSet<Long> x = (HashSet<Long>) LongStream.range(0, ONE_MILLION).boxed().collect(Collectors.toSet());

      // LinkedList = 45 MB because +14m=referinte la next,prev
//      LinkedList<Long> x = LongStream.range(0, ONE_MILLION).boxed().collect(Collectors.toCollection(LinkedList::new));


      // ArrayList = 31 MB because =+ 5mb pt ca are capacitate alocata in plus
//      List<Long> x = LongStream.range(0, ONE_MILLION).boxed().collect(Collectors.toList());

      // Long[] = 26 MB because am si  1M de instante de Long pe heap => 18M / 1M = 18 bytes / Long
//      Long[] x = LongStream.range(0, ONE_MILLION).boxed().toArray(Long[]::new);

      // long[] = 8 MB because 8b vs 4b
//      long[] x = LongStream.range(0, ONE_MILLION).toArray();

//        int[] = 3 MB because
//      int[] x = IntStream.range(0, ONE_MILLION).toArray();

      long heap1 = PerformanceUtil.getUsedHeapBytes();
      System.out.println("Collection(size="+ONE_MILLION+")" + PerformanceUtil.objectToString(x) + " occupies: " + (heap1 - heap0) / 1024 / 1024 + " MB");

//      x.clear();
      long heap2 = PerformanceUtil.getUsedHeapBytes();
      System.out.println("Collection.clear()ed) still occupies: " + (heap2 - heap0) / 1024 / 1024 + " MB");
   }

}
