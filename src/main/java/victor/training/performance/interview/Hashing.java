package victor.training.performance.interview;

import victor.training.performance.util.PerformanceUtil;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class Hashing {
   private String name;
   private LocalDate creationDate = LocalDate.now();

   public Hashing(String name, LocalDate creationDate) {
      this.name = name;
      this.creationDate = creationDate;
   }

   public static void main(String[] args) {
      System.out.println("Calling in a loop the same code, until ENTER pressed");
      PerformanceUtil.onEnterExit();
      while (true) {
         intersectCollections();
      }
   }
   private static Collection<String> generate(int n) {
      System.out.printf("Generating shuffled sequence of %,d elements...%n", n);
      List<String> result = IntStream.rangeClosed(1, n)
              .mapToObj(i -> "A" + i)
              .collect(toList());
      Collections.shuffle(result);
      return result;
   }

   // -----------------

   public static void intersectCollections() {
      System.out.println("\nIteration");
      Collection<String> importedIds = generate(20_000);
      Collection<String> allIds = generate(18_000);

      countIntersection(importedIds, allIds); // TODO #1 optimize
//      countNew(importedIds, allIds); // TODO #3 one day, imported.size() < all.size()
   }

   private static void countIntersection(Collection<?> importedIds, Collection<?> allIds) {
      System.out.println("Intersecting...");
      long t0 = System.currentTimeMillis();
      int n = 0;
      for (Object a : importedIds) {
         if (allIds.contains(a)) {
            n++;
         }
      }
      long t1 = System.currentTimeMillis();
      System.out.printf("Intersected: n=" + n + ", took = %,d%n", t1 - t0);
   }

   private static <T> void countNew(Collection<T> importedIds, Collection<T> allIds) {
      System.out.println("Intersecting...");
      long t0 = System.currentTimeMillis();
      List<T> copy = new ArrayList<>(importedIds); // TODO #2 optimize
      copy.removeAll(allIds);
      int n = copy.size();
      long t1 = System.currentTimeMillis();
      System.out.printf("New: n=" + n + ", took = %,d%n", t1 - t0);
   }


}
