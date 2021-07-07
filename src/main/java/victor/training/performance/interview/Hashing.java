package victor.training.performance.interview;

import victor.training.performance.PerformanceUtil;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
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
         someRequest();
      }
   }

   public static void someRequest() {
      System.out.println("\nIteration");
      Collection<?> targetIds = generate(20_000);
      Collection<?> allIds = generate(20_000);

      match(targetIds, allIds);
   }

   private static Collection<?> generate(int max) {
      System.out.printf("Generating shuffled sequence of %,d elements...%n", max);
      List<String> result = IntStream.rangeClosed(1, max)
          .mapToObj(i -> "A" + i)
          .collect(toList());
      Collections.shuffle(result);
      return result;
   }

   private static void match(Collection<?> targetIds, Collection<?> allIds) {
      System.out.println("Matching...");
      long t0 = System.currentTimeMillis();
      int n = 0;
      for (Object a : targetIds) {
         if (allIds.contains(a)) {
            n++;
         }
      }
      long t1 = System.currentTimeMillis();
      System.out.println("Got: " + n);
      System.out.printf("Matching Took = %,d%n", t1 - t0);
   }

   public void setCreationDate(LocalDate creationDate) {
      this.creationDate = creationDate;
   }


}
