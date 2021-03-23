package victor.training.performance.pools;


import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import victor.training.performance.pools.drinks.Beer;
import victor.training.performance.pools.drinks.Vodka;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;

import static java.util.Arrays.asList;
import static victor.training.performance.ConcurrencyUtil.sleepq;

@Component
@Slf4j
public class BarService implements CommandLineRunner {
   @Autowired
   private Barman barman;

   @Autowired
   private MyRequestContext requestContext;

   @Override
   public void run(String... args) {
      requestContext.setCurrentUser("jdoe");
      log.debug("" + orderDrinks());
   }

   @SneakyThrows
   public List<Object> orderDrinks() {
      log.debug("Submitting my order");

//      ExecutorService pool = Executors.newFixedThreadPool(2);
//      ExecutorService pool = Executors.newCachedThreadPool();

      ExecutorService pool = new ThreadPoolExecutor(
          2, 4,
          5, TimeUnit.MILLISECONDS,
          new ArrayBlockingQueue<>(2),
          new CallerRunsPolicy()
      );

      Future<Beer> futureBeer = pool.submit(() -> barman.pourBeer());
      Future<Vodka> futureVodka = pool.submit(() -> barman.pourVodka());

      log.debug("Fata a plecat pe cal sa-mi aduca comanda");

      Beer beer = futureBeer.get();
      Vodka vodka = futureVodka.get();

      log.debug("Got my order: " + asList(beer, vodka));
      return null;
   }

}

@Service
@Slf4j
class Barman {
   @Autowired
   private MyRequestContext requestContext;

   public Beer pourBeer() {
      String currentUsername = null; // TODO ThreadLocals... , requestContext.getCurrentUser()
      log.debug("Pouring Beer to " + currentUsername + "...");
      sleepq(2000); // REST / DB call
      return new Beer();
   }

   public Vodka pourVodka() {
      log.debug("Pouring Vodka...");
      sleepq(1000);
      return new Vodka();
   }
}
