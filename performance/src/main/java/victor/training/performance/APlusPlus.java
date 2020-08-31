package victor.training.performance;

import lombok.extern.slf4j.Slf4j;
import victor.training.performance.java8.CompletableFutures;

import java.util.concurrent.*;
import java.util.function.Supplier;

import static java.util.concurrent.CompletableFuture.supplyAsync;

@Slf4j
public class APlusPlus {
   private static int population;
   private static final Object MONITOR = new Object();

   public static class Taska implements Supplier<Integer> {
      @Override
      public Integer get() {
         int localPopulation = 0;
         for (int i = 0; i < 100_000; i++) {
            localPopulation++;
         }
         return localPopulation;
      }
   }

   public static class Taskb implements Supplier<Integer> {
      @Override
      public Integer get() {
         int localPopulation = 0;
         for (int i = 0; i < 100_000; i++) {
            localPopulation++;
         }
         return localPopulation;
      }
   }

   // TODO (bonus): ConcurrencyUtil.useCPU(1)


   public static void main(String[] args) throws InterruptedException, ExecutionException {
      Taska threadA = new Taska();
      Taskb threadB = new Taskb();

      long t0 = System.currentTimeMillis();

      Integer r = supplyAsync(new Taska())
              .thenCombineAsync(supplyAsync(new Taskb()), (r1, r2) -> {
                 log.debug("Plus");
                 return r1 + r2;
              })
          .get();


      long t1 = System.currentTimeMillis();
      System.out.println("Total = " + r);
      System.out.println("Took = " + (t1 - t0));
   }
}
