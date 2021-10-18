package victor.training.performance.pools;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import victor.training.performance.pools.drinks.Beer;
import victor.training.performance.pools.drinks.Vodka;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.util.Arrays.asList;
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
      log.debug("" + orderDrinks());
   }



   public List<Object> orderDrinks() throws ExecutionException, InterruptedException {
      log.debug("Submitting my order");
      long t0 = System.currentTimeMillis();

      ExecutorService pool = Executors.newFixedThreadPool(2); // TERRIBLY WRONG

      Future<Beer> futureBeer = pool.submit(() -> barman.pourBeerApi());
      Future<Vodka> futureVodka = pool.submit(() -> barman.pourVodkaOtherApi());

      Beer beer = futureBeer.get();
      // line 1 = delta 1 sec
      Vodka vodka = futureVodka.get();
      // line 2  - line 1 = delta 0 sec

      long t1 = System.currentTimeMillis();
      log.debug("Got my order in {} ms : {}", t1-t0, asList(beer, vodka));
      return null;
   }

}

@Service
@Slf4j
class Barman {
   @Autowired
   private MyRequestContext requestContext;

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
