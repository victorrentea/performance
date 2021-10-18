package victor.training.performance.pools;


import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import victor.training.performance.pools.drinks.Beer;
import victor.training.performance.pools.drinks.Vodka;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static victor.training.performance.PerformanceUtil.sleepq;

@Component
@Slf4j
public class BarService {
   @Autowired
   private Barman barman;


   public CompletableFuture<DillyDilly> orderDrinks() throws ExecutionException, InterruptedException {
      log.debug("Submitting my order");

//      Future<Beer> futureBeer = pool.submit(() -> barman.pourBeerApi());
//      Future<Vodka> futureVodka = pool.submit(() -> barman.pourVodkaOtherApi());
//

      System.out.println(barman.getClass() + " is a proxy");

      CompletableFuture<Beer> futureBeer = barman.pourBeerApi();
      CompletableFuture<Vodka> futureVodka = barman.pourVodkaOtherApi();

      CompletableFuture<DillyDilly> futureDilly = futureBeer.thenCombine(futureVodka, (b, v) -> new DillyDilly(b, v));

      // see Venkat for more :
      /// https://www.youtube.com/watch?v=0hQvWIdwnw4
      return futureDilly;
   }
   // TODO
   exceptions
   how many threads ?
   how executors work under the hood, queue

}

@Value
class DillyDilly {
   Beer beer;
   Vodka vodka;
}

@Service
@Slf4j
class Barman {

   @Async("beerBarman")
   public CompletableFuture<Beer> pourBeerApi() {
      log.debug("Pouring Beer to ...");
      sleepq(1000); // calling a REST API
      return CompletableFuture.completedFuture(new Beer());
   }

   @Async("vodkaBarman")
   public CompletableFuture<Vodka> pourVodkaOtherApi() {
      log.debug("Pouring Vodka...");
      sleepq(1000); // calling a LONG SELECT
      return CompletableFuture.completedFuture(new Vodka());
   }
}
