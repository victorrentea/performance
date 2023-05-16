package victor.training.performance.interview;

import victor.training.spring.batch.util.PerformanceUtil;

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
         System.out.println("\nIteration");
         intersectCollections();
      }
   }
   private static List<String> generate(int n) {
      System.out.printf("Generating shuffled sequence of %,d elements...%n", n);
      List<String> result = IntStream.rangeClosed(1, n)
              .mapToObj(i -> "A" + i)
              .collect(toList());
      Collections.shuffle(result);
      return result;
   }

   // -----------------
   public static final List<String> IDS_IN_A_FILE = generate(51_000);
   public static final List<String> IDS_ALREADY_IN_DB = generate(50_000);
   // TODO one day, imported.size() < all.size()

   public static void intersectCollections() {
      System.out.println("Intersecting...");
      long t0 = System.currentTimeMillis();
      int n = countNew(IDS_IN_A_FILE, IDS_ALREADY_IN_DB);
      long t1 = System.currentTimeMillis();
      System.out.printf("Counted new elements: n=" + n + ", took = %,d%n", t1 - t0);
   }

   private static <T> int countNew(Collection<T> importedIds, Collection<T> allIds) {
      Set<T> copy = new HashSet<>(importedIds);
      // Optimized: created a hashSet to find elements to remove faster
      copy.removeAll(allIds);
      return copy.size();
   }


}
