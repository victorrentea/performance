package victor.training.performance.pools;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import victor.training.performance.pools.drinks.Beer;
import victor.training.performance.pools.drinks.Vodka;

import java.util.List;
import java.util.concurrent.*;

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
   public void run(String... args) throws ExecutionException, InterruptedException {
      requestContext.setCurrentUser("jdoe");
      log.debug("" + orderDrinks());
   }
//   ExecutorService pool = Executors.newFixedThreadPool(4);
   @Autowired
   ThreadPoolTaskExecutor pool;

   public List<Object> orderDrinks() throws ExecutionException, InterruptedException {
      log.debug("Submitting my order");


      Future<Beer> futureBeer = pool.submit(() -> barman.pourBeer());
      Future<Vodka> futureVodka = pool.submit(() -> barman.pourVodka());

//      futureVodka.cancel(true);

      Beer beer = futureBeer.get(); // main blocks here for 1s
      Vodka vodka = futureVodka.get(); // main doesn't block here

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
      sleepq(1000);
      return new Beer();
   }

   public Vodka pourVodka() {
      log.debug("Pouring Vodka..."); // WS call
      sleepq(1000);
      return new Vodka();
   }
}
