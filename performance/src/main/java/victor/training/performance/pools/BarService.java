package victor.training.performance.pools;


import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import victor.training.performance.pools.drinks.Beer;
import victor.training.performance.pools.drinks.Vodka;

import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.completedFuture;
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
   public CompletableFuture<DillyDilly> orderDrinks() {
      log.debug("Submitting my order to " + barman.getClass());



      CompletableFuture<Vodka> futureVodka = barman.pourVodka();
      CompletableFuture<Beer> futureBeer = barman.pourBeer();


      CompletableFuture<DillyDilly> futureDilly = futureBeer.thenCombineAsync(futureVodka, DillyDilly::new);

      //swt, awt, swing, javaFX, android, -UI

      futureDilly.thenAccept(dilly -> log.debug("Got my order! Thank you lad! " + dilly));
      method();

      barman.injura("%^Q#%^%$^^!^$!^@^@!%#^%");
      System.out.println("Acasa, ma bag in patuc!");

      barman.callFrGov().get();

      return futureDilly;
   }

   public void method() {
      String currentUsername = UserHolderPeThread.currentUserName.get(); // TODO ThreadLocals... , requestContext.getCurrentUser()
      log.debug("Pouring Beer to " + currentUsername + "...");
   }
}
// Dupa pauza:
// 4) Flux mai complex
// 1) exceptii
@Service
@Slf4j
class Barman {

   @Autowired
   private MyRequestContext requestContext;

   @Async("executor")
   public CompletableFuture<Beer> pourBeer() {
//      String currentUsername = UserHolderPeThread.currentUserName.get(); // TODO ThreadLocals... , requestContext.getCurrentUser()
      log.debug(">>>>Pouring Beer to " + "currentUsername" + "...");
//      if (true) {
//         throw new IllegalStateException("Nu mai e bere!");
//      }
      sleepq(1000);// SELECT
      return completedFuture(new Beer());
   }

   @Async("vodkaExecutor")
   public CompletableFuture<Vodka> pourVodka() {
      log.debug("Pouring Vodka...");
      sleepq(1000); // HTTP, Astepti dupa altu (POATE dupa o conex cu DB)
      return CompletableFuture.completedFuture(new Vodka());
   }

   @Async
   public void injura(String uratura) {
      if (uratura != null) {
         throw new IllegalArgumentException("Iti fac buzunar!@!#@#");
      }
   }
   @Async("frGov")
   public CompletableFuture<Long> callFrGov() {
      sleepq(1000);
      return completedFuture(1L);
   }
}

// TimeoutWaitingFor DB Connection Exception

// Tomcat cand trateaza HTTP requests aloca un HTTP
// request unui din workerii din thread pool (300)

// Baza saraca e fragila. Nu dai ca nesimtitul in ea cu 300 de conx, ca crapa.

// Cand vrei sa vb cu baza iei una din conx din DB Conn pool (20-80)
