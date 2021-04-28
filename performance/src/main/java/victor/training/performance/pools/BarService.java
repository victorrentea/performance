package victor.training.performance.pools;


import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import victor.training.performance.pools.drinks.Beer;
import victor.training.performance.pools.drinks.Vodka;

import java.util.concurrent.CompletableFuture;

import static victor.training.performance.ConcurrencyUtil.sleepq;

@Component
@Slf4j
public class BarService {
   @Autowired
   private Barman barman;

   @Autowired
   private MyRequestContext requestContext;


   //   static ExecutorService pool = Executors.newFixedThreadPool(2);
   @Autowired
   private ThreadPoolTaskExecutor pool;

   @SneakyThrows
   public CompletableFuture<DillyDilly> orderDrinks() {
      log.debug("Submitting my order to barman: " + barman.getClass());

      CompletableFuture<Beer> futureBeer = barman.pourBeer();
      CompletableFuture<Vodka> futureVodka = barman.pourVodka();

//      Beer beer = futureBeer.get(); // blocheaza main() pt 1 sec pana e gata berea
//      Vodka vodka = futureVodka.get(); // ia uite ! vodka e deja gata. si nu mai blocheaza nimic.

      CompletableFuture<DillyDilly> futureDilly = futureBeer.thenCombine(futureVodka,
          (beer, vodka) -> new DillyDilly(beer, vodka));


      return futureDilly;
   }
}

@Data
@Slf4j
class DillyDilly {
   private final Beer beer;
   private final Vodka vodka;

   public DillyDilly(Beer beer, Vodka vodka) {
      log.debug("Amestec {} cu {}", beer, vodka);
      this.beer = beer;
      this.vodka = vodka;
   }
}

@Service
@Slf4j
class Barman {
   @Autowired
   private MyRequestContext requestContext;

   @Async
   public CompletableFuture<Beer> pourBeer() {
      String currentUsername = null; // TODO ThreadLocals... , requestContext.getCurrentUser()
      log.debug("Pouring Beer to " + currentUsername + "...");
      sleepq(1000);
      return CompletableFuture.completedFuture(new Beer());
   }

   @Async
   public CompletableFuture<Vodka> pourVodka() {
      log.debug("Pouring Vodka...");
      sleepq(1000);
      return CompletableFuture.completedFuture(new Vodka());
   }
}
