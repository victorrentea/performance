package victor.training.performance.spring;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;

import static victor.training.performance.util.PerformanceUtil.sleepq;

@Slf4j
@RestController
@RequestMapping("bar")
public class BarService implements CommandLineRunner {
   @Autowired
   private Barman barman;

   @Override
   public void run(String... args) throws Exception { // runs at app startup
//      log.debug("Got " + orderDrinks());
   }
   @GetMapping("drink")
   public CompletableFuture<DillyDilly> orderDrinks() {
      log.debug("Requesting drinks to {} ...", barman.getClass());

      CompletableFuture<Beer> futureBeer = barman.pourBeer("Heineken")
          .exceptionally(t -> {
             if (t.getCause() instanceof IllegalStateException) {
                return new Beer("Neumarkt");
             } else {
                throw new RuntimeException(t);
             }
          });

      // akka actors, vert.x, rxjava, reactor<Spring5 WebFlux

      CompletableFuture<Vodka> futureVodka = barman.pourVodka()
          .thenApplyAsync(Vodka::withGheata);

      CompletableFuture<DillyDilly> futureDilly = futureBeer
          .thenCombineAsync(futureVodka, DillyDilly::new)
          .exceptionally(t -> null);

      log.debug("Iese threadul de http. e liber.");
      return futureDilly;
   }
}

class DillyDilly {
   private final Beer beer;
   private final Vodka vodka;

   DillyDilly(Beer beer, Vodka vodka) {
      this.beer = beer;
      this.vodka = vodka;
   }

   public Beer getBeer() {
      return beer;
   }

   public Vodka getVodka() {
      return vodka;
   }

   @Override
   public String toString() {
      return "DillyDilly{" +
             "beer=" + beer +
             ", vodka=" + vodka +
             '}';
   }
}

@Service
@Slf4j
class Barman {
   @Async("beerPool") //1 simultan
   public CompletableFuture<Beer> pourBeer(String beerType) {
      log.debug("Pouring Beer...");
      sleepq(1000); // call de API REST
      if ("Heineken".equals(beerType)) {
         throw new IllegalStateException("Nu mai e bere!!! Drama!");
      }
      log.debug("AM terminat berea");
      return CompletableFuture.completedFuture(new Beer(beerType));
   }
   @Async("vodkaPool") // max 4 simultan, indiferent cate req de dilly dilly primesc.
   public CompletableFuture<Vodka> pourVodka() {
      log.debug("Pouring Vodka...");
      sleepq(1000); // SQL CRIMINAL
      return CompletableFuture.completedFuture(new Vodka(false));
   }

   public void injura(String uratura) {
      if (uratura != null) {
         log.error("Il omoara");
         throw new IllegalArgumentException("Iti fac buzunar");
      }
   }
}

@Data
class Beer {
   private final String type;
}
@Data
@Slf4j
class Vodka {
   private final String brand = "Absolut";
   private final boolean areGheata;

   public Vodka withGheata() {
      log.info("Adaug gheata");
      return new Vodka(true);
   }
}

// TODO when called from web, protect the http thread

// TODO The Foam Problem: https://www.google.com/search?q=foam+beer+why

@Configuration
class BarConfig {
   //<editor-fold desc="Spring Config">
   //   @Autowired
//   private PropagateThreadScope propagateThreadScope;

   @Bean
   public ThreadPoolTaskExecutor beerPool(@Value("${beer.pool.count:1}") int barmanCount) {
      ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
      executor.setCorePoolSize(barmanCount);
      executor.setMaxPoolSize(barmanCount);
      executor.setQueueCapacity(500);
      executor.setThreadNamePrefix("beerPool-");
      executor.initialize();
      executor.setWaitForTasksToCompleteOnShutdown(true);
      executor.setRejectedExecutionHandler(new CallerRunsPolicy());
      return executor;
   }
   @Bean
   public ThreadPoolTaskExecutor vodkaPool(@Value("${vodka.pool.count:4}") int barmanCount) {
      ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
      executor.setCorePoolSize(barmanCount);
      executor.setMaxPoolSize(barmanCount);
      executor.setQueueCapacity(500);
      executor.setThreadNamePrefix("vodkaPool-");
      executor.initialize();
      executor.setWaitForTasksToCompleteOnShutdown(true);
      executor.setRejectedExecutionHandler(new CallerRunsPolicy());
      return executor;
   }
   //</editor-fold>




   // task avg duration = 1sec; am 20 threaduri; ==> 500 queue size ==> avg WAITING TIME
   // = 500 / 20 * 1 sec = 25 sec = e tolerabil  pt clienti ?
}