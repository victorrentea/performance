package victor.training.performance.pools;


import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
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

   @Autowired
   private ThreadPoolTaskExecutor pool;

   public CompletableFuture<DillyDilly> orderDrinks() throws ExecutionException, InterruptedException {
      log.debug("Submitting my order");

//      Future<Beer> futureBeer = pool.submit(() -> barman.pourBeerApi());
//      Future<Vodka> futureVodka = pool.submit(() -> barman.pourVodkaOtherApi());
//
      CompletableFuture<Beer> futureBeer = CompletableFuture.supplyAsync(() -> barman.pourBeerApi());
      CompletableFuture<Vodka> futureVodka = CompletableFuture.supplyAsync(() -> barman.pourVodkaOtherApi());

      CompletableFuture<DillyDilly> futureDilly = futureBeer.thenCombine(futureVodka, (b, v) -> new DillyDilly(b, v));

      // see Venkat for more :
      /// https://www.youtube.com/watch?v=0hQvWIdwnw4
      return futureDilly;
   }
}

@Value
class DillyDilly {
   Beer beer;
   Vodka vodka;
}

@Service
@Slf4j
class Barman {

   public Beer pourBeerApi() {
      log.debug("Pouring Beer to ...");
      sleepq(1000); // calling a REST API
      return new Beer();
   }

   public Vodka pourVodkaOtherApi() {
      log.debug("Pouring Vodka...");
      sleepq(1000); // calling a LONG SELECT
      return new Vodka();
   }
}
