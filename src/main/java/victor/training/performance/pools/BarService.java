package victor.training.performance.pools;


import lombok.SneakyThrows;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import victor.training.performance.pools.drinks.Beer;
import victor.training.performance.pools.drinks.Vodka;

import java.util.concurrent.CompletableFuture;

import static victor.training.performance.PerformanceUtil.sleepq;

@Component
@Slf4j
public class BarService implements CommandLineRunner {
   @Autowired
   private Barman barman;

   @Autowired
   private MyRequestContext requestContext;

   @Override
   public void run(String... args) throws Exception {
//      requestContext.setCurrentUser("jdoe");
//      log.debug("" + orderDrinks());
   }

   @Autowired
   ThreadPoolTaskExecutor pool;

   //global, per app
   // Requ: optimize drinking sa beu mai repede.
   @SneakyThrows
   public CompletableFuture<DillyDilly> orderDrinks() {
      log.debug("Submitting my order");
      long t0 = System.currentTimeMillis();


      // promise-uri din JS

      CompletableFuture<Beer> futureBeer = CompletableFuture.supplyAsync(() -> barman.pourBeer());
      CompletableFuture<Vodka> futureVodka = CompletableFuture.supplyAsync(() -> barman.pourVodka());
//      Future<Beer> futureBeer = pool.submit(() -> barman.pourBeer());
//      Future<Vodka> futureVodka = pool.submit(() -> barman.pourVodka());
      log.debug("Pleaca chelnerul");

//futureBeer.thenAccept(beer -> System.out.println("Beu " +beer));
      CompletableFuture<DillyDilly> futureDilly = futureBeer.thenCombineAsync(futureVodka, DillyDilly::new);

      log.debug("ACum scapa threadul de HTTP");
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
      sleepq(100); // imagine:  REST API/ soap
      return new Beer();
   }

   public Vodka pourVodka() {
      log.debug("Pouring Vodka...");
      sleepq(100); //  imagine:  DB SELECT heavy
      return new Vodka();
   }
}
