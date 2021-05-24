package victor.training.performance.pools;


import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import victor.training.performance.pools.drinks.Beer;
import victor.training.performance.pools.drinks.Vodka;

import java.util.concurrent.*;

import static victor.training.performance.PerformanceUtil.sleepq;

@Component
@Slf4j
public class BarService {
   @Autowired
   private Barman barman;

   @Autowired
   private MyRequestContext requestContext;

//   ExecutorService pool = Executors.newFixedThreadPool(4);
   @Autowired
   ThreadPoolTaskExecutor pool;

   public CompletableFuture<DillyDilly> orderDrinks() throws ExecutionException, InterruptedException {
      log.debug("Submitting my order");


      CompletableFuture<Beer> futureBeer = CompletableFuture.supplyAsync(() -> barman.pourBeer());
      CompletableFuture<Vodka> futureVodka = CompletableFuture.supplyAsync(() -> barman.pourVodka());

      CompletableFuture<DillyDilly> futureDilly = futureBeer.thenCombineAsync(futureVodka, (b, v) -> new DillyDilly(b, v));

//      Beer beer = futureBeer.get(); // main blocks here for 1s
//      Vodka vodka = futureVodka.get(); // main doesn't block here

//      DillyDilly dilly = futureDilly.get();

//      log.debug("Got my order: " + dilly);
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
   @Autowired
   private MyRequestContext requestContext;

   public Beer pourBeer() {
      String currentUsername = null; // TODO ThreadLocals... , requestContext.getCurrentUser()
      log.debug("Pouring Beer to " + currentUsername + "...");
      sleepq(1000);
      return new Beer();
   }

   public Vodka pourVodka() {
      log.debug("Pouring Vodka..."); // WS call
      sleepq(1000);
      return new Vodka();
   }
}
