package victor.training.performance.pools;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

@Slf4j
public class FluxuriPePooluriSeparate {
   public static void main(String[] args) throws ExecutionException, InterruptedException {

      ForkJoinPool dbPool = new ForkJoinPool(6);
      ForkJoinPool apiPool = new ForkJoinPool(3);


      CompletableFuture.supplyAsync(()->db("A"),dbPool)
         .thenApplyAsync(FluxuriPePooluriSeparate::cpu)
         .thenApplyAsync(FluxuriPePooluriSeparate::api, apiPool)
      .get();
   }

   public static String db(String in) {
      log.debug("Aici");
      return in.toLowerCase();
   }
   public static String cpu(String in) {
      log.debug("Aici");
      return in + "CPU";
   }
   public static String api(String in) {
      log.debug("Aici");
      return in.toUpperCase();
   }
}
