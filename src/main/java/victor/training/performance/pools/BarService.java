package victor.training.performance.pools;


import lombok.SneakyThrows;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Async;
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


   //global, per app
   // Requ: optimize drinking sa beu mai repede.
   @SneakyThrows
   public CompletableFuture<DillyDilly> orderDrinks() {
      log.debug("Submitting my order");
      long t0 = System.currentTimeMillis();


      // promise-uri din JS

      CompletableFuture<Beer> futureBeer = barman.pourBeer();
      CompletableFuture<Vodka> futureVodka = barman.pourVodka();
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


   // ei NU ACCEPTA MAI MULT DE 2 requesturi in paralel
   // sau constati ca acel sistem extern se comporta optim la 10 req in paralel maxim
   @Async("beerPool")
   public CompletableFuture<Beer> pourBeer() {
      String currentUsername = null; // TODO ThreadLocals... , requestContext.getCurrentUser()
      log.debug("Pouring Beer to " + currentUsername + "...");
      sleepq(100); // imagine:  REST API/ soap
      return CompletableFuture.completedFuture(new Beer());
   }
   // aveam niste query-uri de search care rulau cat 10 min.
   // daca aveam ghinionul sa vina 20 de astfel de searchuri (fat pigs) simultan ,epuizam DB Conn pool

//   @Async("fatPigPool") // 2 max
//   public CompletableFuture<Vodka> fat() {
//      log.debug("Pouring Vodka...");
//      SELECT
//      return CompletableFuture.completedFuture(new Vodka());
//   }
   @Async("vodkaPool") // 50 max
   public CompletableFuture<Vodka> pourVodka() {
      log.debug("Pouring Vodka...");
      sleepq(100); //  imagine:  DB SELECT heavy
      return CompletableFuture.completedFuture(new Vodka());
   }
}
