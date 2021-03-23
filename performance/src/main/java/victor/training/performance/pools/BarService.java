package victor.training.performance.pools;


import lombok.SneakyThrows;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import victor.training.performance.pools.drinks.Beer;
import victor.training.performance.pools.drinks.Vodka;

import java.util.concurrent.*;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static victor.training.performance.ConcurrencyUtil.sleepq;

@Component
@Slf4j
public class BarService implements CommandLineRunner {
   @Autowired
   private Barman barman;

   @Autowired
   private MyRequestContext requestContext;

   @Override
   public void run(String... args) throws ExecutionException, InterruptedException {
      requestContext.setCurrentUser("jdoe");
      log.debug("" + orderDrinks().get());
   }

   @SneakyThrows
   public CompletableFuture<DillyDilly> orderDrinks() {
      log.debug("Submitting my order");

//      ExecutorService pool = Executors.newFixedThreadPool(2);
      ExecutorService beerPool = Executors.newFixedThreadPool(1, r -> new Thread(r,"beer"));
      ExecutorService vodkaPool = Executors.newFixedThreadPool(4, r -> new Thread(r,"vodka"));
//      ExecutorService pool = Executors.newCachedThreadPool();

      ExecutorService pool = new ThreadPoolExecutor(
          2, 4,
          5, TimeUnit.MILLISECONDS,
          new ArrayBlockingQueue<>(2),
          new CallerRunsPolicy()
      );

      CompletableFuture<Void> payFuture = CompletableFuture.runAsync(() -> pay());

      CompletableFuture<Beer> futureBeer = payFuture.thenApplyAsync(p -> barman.pourBeer("dark"),beerPool )
          .exceptionally(ex -> barman.pourBeer("blond"));

      CompletableFuture<Vodka> futureVodka = payFuture.thenApplyAsync(p -> barman.pourVodka(), vodkaPool);


      CompletableFuture<DillyDilly> futureDilly = futureBeer.thenCombineAsync(futureVodka,
          (beer, vodka) -> new DillyDilly(beer, vodka));

      log.info("Metoda se termina ");
      return futureDilly;
   }

   private void pay() {

   }
}

// Vinul dupa bere e placere
// Berea dupa vin e un CHIN

@Slf4j
@Value
class DillyDilly {
   Beer beer;
   Vodka vodka;

   public DillyDilly(Beer beer, Vodka vodka) {
      this.beer = beer;
      this.vodka = vodka;
      log.debug("Amestec..");
      sleepq(1000);
   }
}

@Service
@Slf4j
class Barman {
   @Autowired
   private MyRequestContext requestContext;


    // accepta sistemul extern maxim 1 request odata
   public Beer pourBeer(String dark) {
      String currentUsername = null; // TODO ThreadLocals... , requestContext.getCurrentUser()
      log.debug("Pouring Beer to " + currentUsername + "...");
      if ("dark"==dark) {
         throw new IllegalArgumentException("Nu mai e bere!");
      }
      sleepq(2000); // REST / DB call
      return new Beer();
   }

   // accepta sistemul extern 4 max paralele.
   public Vodka pourVodka() {
      log.debug("Pouring Vodka...");
      sleepq(1000);
      return new Vodka();
   }
}
