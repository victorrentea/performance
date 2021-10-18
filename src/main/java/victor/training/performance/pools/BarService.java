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
import java.util.concurrent.Future;
import java.util.function.Supplier;

import static java.util.Arrays.asList;
import static victor.training.performance.PerformanceUtil.sleepq;

@Component
@Slf4j
public class BarService {
   @Autowired
   private Barman barman;

   @Autowired
   private ThreadPoolTaskExecutor pool;

   public String orderDrinks() throws ExecutionException, InterruptedException {
      log.debug("Submitting my order");
      long t0 = System.currentTimeMillis();


      Future<Beer> futureBeer = pool.submit(() -> barman.pourBeerApi());
      Future<Vodka> futureVodka = pool.submit(() -> barman.pourVodkaOtherApi());
//

//      CompletableFuture<Beer> futureBeer = CompletableFuture.supplyAsync(() -> barman.pourBeerApi());

      Beer beer = futureBeer.get();
      Vodka vodka = futureVodka.get();


      DillyDilly dilly = new DillyDilly(beer, vodka);

      long t1 = System.currentTimeMillis();
      log.debug("Got my order in {} ms : {}", t1-t0, asList(beer, vodka));
      return dilly.toString();
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
