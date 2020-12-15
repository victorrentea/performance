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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
      log.debug(orderDrinks().toString());
   }

   @SneakyThrows
   public List<Object> orderDrinks() {
      log.debug("Submitting my order");

      ExecutorService pool = Executors.newFixedThreadPool(2);

      Future<Vodka> futureVodka = pool.submit(() -> barman.pourVodka());
      Future<Beer> futureBeer = pool.submit(() -> barman.pourBeer());

      log.debug("A plecat fata/baietul cu comanda");
      Vodka vodka = futureVodka.get(); // cat timp asteapta main aici?: 1s
      Beer beer = futureBeer.get(); // cat timp asteapta main aici?: 0s

      log.debug("Got my order! Thank you lad! " + asList(beer, vodka));
      return asList(beer, vodka);
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
      sleepq(1000);// SELECT
      return new Beer();
   }

   public Vodka pourVodka() {
      log.debug("Pouring Vodka...");
      sleepq(1000); // HTTP, Astepti dupa altu (POATE dupa o conex cu DB)
      return new Vodka();
   }
}

// TimeoutWaitingFor DB Connection Exception

// Tomcat cand trateaza HTTP requests aloca un HTTP
// request unui din workerii din thread pool (300)

// Baza saraca e fragila. Nu dai ca nesimtitul in ea cu 300 de conx, ca crapa.

// Cand vrei sa vb cu baza iei una din conx din DB Conn pool (20-80)
