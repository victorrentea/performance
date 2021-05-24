package victor.training.performance.pools;


import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import victor.training.performance.pools.drinks.Beer;
import victor.training.performance.pools.drinks.Vodka;

import javax.servlet.http.HttpServletRequest;
import java.io.FileReader;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static victor.training.performance.PerformanceUtil.sleepq;

@Component
@Slf4j
public class BarService {
   @Autowired
   private Barman barman;

   @Autowired
   private MyRequestContext requestContext;

//   ExecutorService pool = Executors.newFixedThreadPool(4);

   public CompletableFuture<DillyDilly> orderDrinks() throws ExecutionException, InterruptedException {
      log.debug("Submitting my order to " + barman.getClass());


      CompletableFuture<Void> futurePayment = CompletableFuture.runAsync(() -> log.debug("Accept payment"));

//      futurePayment.thenApply(v -> barman.pourBeer());
//      futurePayment.thenApply(v -> barman.pourVodka());

      CompletableFuture<Beer> futureBeer = barman.pourBeer();
      futureBeer.cancel(true);
      CompletableFuture<Vodka> futureVodka = barman.pourVodka();

      CompletableFuture<DillyDilly> futureDilly = futureBeer
          .thenCombineAsync(futureVodka, (b, v) -> new DillyDilly(b, v));

      barman.curse("!&$^@!&^@!&$^&@!^$");
      System.out.println("Do I still get back home safely ");

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

   @Async("beerPool")
   public CompletableFuture<Beer> pourBeer() {
      String currentUsername = null; // TODO ThreadLocals... , requestContext.getCurrentUser()
      log.debug("Pouring Beer to " + currentUsername + "...");
      sleepq(1000);
      return completedFuture(new Beer());
   }

   @Async("vodkaPool")
   public CompletableFuture<Vodka> pourVodka() {
      log.debug("Pouring Vodka..."); // WS call
      sleepq(1000);
      return completedFuture(new Vodka());
   }

   @Async
   public void curse(String curse) {
      if (curse != null) {
         throw new IllegalArgumentException("Te mato!");
      }
   }
}
