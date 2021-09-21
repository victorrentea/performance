package victor.training.performance.pools;


import lombok.SneakyThrows;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

      CompletableFuture<Beer> futureBeer = barman.pourBeer()
          .exceptionally(e -> {
             log.error("EROARE: " + e, e);
             if (e.getCause() instanceof NuMaiEBerEeBlonda) {
                return new Beer("bruna");
             } else {
                throw new RuntimeException(e);
             }
          }) // catch(NuMaiBereBlonda)
          .thenApply(beer -> beer.addIce())
          ;

      CompletableFuture<Vodka> futureVodka = barman.pourVodka();
      log.debug("Pleaca chelnerul");


      CompletableFuture<DillyDilly> futureDilly = futureBeer.thenCombineAsync(futureVodka, DillyDilly::new);

      //store file in temp folder
      barman.injura("%^$#&!%**!&#&!*@&%"/*, file*/); // fire and forget : chemi o metoda @Async care return void

      // return la client "AM PRIMIT: uite uploadID-ul tau"
      log.debug("ACum scapa threadul de HTTP");
      return futureDilly;
   }
}

@Value
class DillyDilly {
   Beer beer;
   Vodka vodka;
}
class NuMaiEBerEeBlonda  extends RuntimeException{

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
      if (true) {
         throw new NuMaiEBerEeBlonda();
      }
      sleepq(100); // imagine:  REST API/ soap
      return CompletableFuture.completedFuture(new Beer("blond"));
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
   @Async
   public void injura(String uratura) {

      if (StringUtils.isNotBlank(uratura)) {
         throw new IllegalArgumentException("Te casez/ iti fac buzunar!");
      }
   }
}
