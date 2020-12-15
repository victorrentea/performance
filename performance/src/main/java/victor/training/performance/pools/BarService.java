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

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Supplier;

import static java.util.Arrays.asList;
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
   public void run(String... args) {
      requestContext.setCurrentUser("jdoe");
      log.debug("" + orderDrinks());
   }

   @SneakyThrows
   public List<Object> orderDrinks() {
      log.debug("Submitting my order");

//      ExecutorService pool = Executors.newFixedThreadPool(2);

      log.debug("A plecat fata/baietul cu comanda");

      supplyAsync(barman::pourBeer)
          .thenCombineAsync(supplyAsync(barman::pourVodka), DillyDilly::new)
          .thenAccept(this::consumeDilly);
      return null;
   }

   private void consumeDilly(DillyDilly dilly) {
      log.debug("Got my order! Thank you lad! " + dilly);
   }
}

@Slf4j
@Value
class DillyDilly {
   Beer beer;
   Vodka vodka;

   public DillyDilly(Beer beer, Vodka vodka) {
      this.beer = beer;
      this.vodka = vodka;
      log.debug("Amestec bere cu vodka!!!");
      sleepq(1000);
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
