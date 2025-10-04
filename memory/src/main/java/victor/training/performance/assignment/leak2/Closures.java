package victor.training.performance.assignment.leak2;

import victor.training.performance.util.PerformanceUtil;
import victor.training.performance.leak.obj.Big100MB;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static java.time.LocalDateTime.now;

public class Closures {
   // DON'T REMOVE THIS
   Big100MB big = new Big100MB();

   // TODO change only this method to make the leak go away
   public Supplier<String> method(int id) {
      return new Supplier<String>() {
         final String toPrint = "Hello " + id;
         @Override
         public String get() {
            PerformanceUtil.sleepMillis(20_000); // KEEP
            return toPrint + now();
         }
      };
   }

   public static void main(String[] args) {
      for (int i = 0; i < 10; i++) {
         CompletableFuture.supplyAsync(new Closures().method(i));
      }
      System.out.println("Used heap: " + PerformanceUtil.getUsedHeapPretty());
      if (PerformanceUtil.getUsedHeapBytes() > 50_000_000) {
         System.err.println("GOAL NOT MET. LEAK STILL PRESENT");
      }
      System.out.println("Take a heap dump");
      PerformanceUtil.waitForEnter();
   }
}
