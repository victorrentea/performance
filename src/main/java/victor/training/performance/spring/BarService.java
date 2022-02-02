package victor.training.performance.spring;


import io.micrometer.core.annotation.Timed;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static victor.training.performance.util.PerformanceUtil.sleepq;

@Component
@Slf4j
public class BarService implements CommandLineRunner {
   @Autowired
   private Barman barman;

   @Override
   public void run(String... args) throws Exception { // runs at app startup
//      log.debug("Got " + orderDrinks());
   }

//   @Autowired
//   private ThreadPoolTaskExecutor poolBar;
//@Value
//   private  final ExecutorService threadPool = Executors.newFixedThreadPool(6); // JDK threadpool

   public CompletableFuture<DillyDilly> orderDrinks() throws ExecutionException, InterruptedException {
      log.debug("Requesting drinks...");
      long t0 = System.currentTimeMillis();

      // sa intelegi CompletableFuture : https://www.youtube.com/watch?v=0hQvWIdwnw4
      // aka Promise (Deferred din js/ts),
      CompletableFuture<Beer> futureBeer = barman.pourBeer();
      CompletableFuture<Vodka> futureVodka = barman.pourVodka();

      CompletableFuture<DillyDilly> futureDilly = futureBeer.thenCombineAsync(futureVodka, DillyDilly::new);

      barman.proceseazaFisier();

      long t1 = System.currentTimeMillis();
      log.debug("in {} ms  ACUM PLEC, eliberez threadul de http ", t1 - t0);
      return futureDilly;
   }
}

// ce obtii daca combini bere cu vodka ?
class DillyDilly {
   private static final Logger log = LoggerFactory.getLogger(DillyDilly.class);
   private final Beer beer;
   private final Vodka vodka;

   DillyDilly(Beer beer, Vodka vodka) {
      this.beer = beer;
      this.vodka = vodka;
      log.debug("Amestec");
      sleepq(1000);
//      new AsyncRestTemplate().getForObject()
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

   @Async // fire and forget
   public void proceseazaFisier() {
      log.debug("Process file");
      sleepq(1000); // REST
   }
   @Async("beerPool")
   public CompletableFuture<Beer> pourBeer() {
      log.debug("Pouring Beer...");
      alta.apelExtern();

      // QUERY FOARTE LUNG
      return CompletableFuture.completedFuture(new Beer());
   }

   @Autowired
   private Alta alta;


   @Async("vodkaPool")
   public CompletableFuture<Vodka> pourVodka() {
      log.debug("Pouring Vodka...");
      sleepq(1000); // DB
      return CompletableFuture.completedFuture(new Vodka());
   }
}
@Component
class Alta {
   @Timed("apel.extern")
   public void apelExtern() {
      sleepq(1000); // REST
   }
}

@Data
class Beer {
   private final String type = "blond";
}
class Vodka {
   private final String brand = "Absolut";

   public String getBrand() {
      return brand;
   }
}

// TODO when called from web, protect the http thread
@Slf4j
@RequestMapping("bar/drink")
@RestController
class BarController {
   //<editor-fold desc="Web">
   @Autowired
   private BarService service;

   @GetMapping
   public CompletableFuture<DillyDilly> getDrinks() throws Exception {
      return service.orderDrinks();
   }
   //</editor-fold>
}

// TODO The Foam Problem: https://www.google.com/search?q=foam+beer+why

@Configuration
class BarConfig {
   //<editor-fold desc="Spring Config">
   //   @Autowired
//   private PropagateThreadScope propagateThreadScope;

   @Bean
   public ThreadPoolTaskExecutor beerPool(@Value("${beer.thread.size}") int poolSize) {
      ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
      executor.setCorePoolSize(poolSize);
      executor.setMaxPoolSize(poolSize);
      executor.setQueueCapacity(500);
      executor.setThreadNamePrefix("beer-");
      executor.initialize();
//      executor.setTaskDecorator(propagateThreadScope);
      executor.setWaitForTasksToCompleteOnShutdown(true);
      return executor;
   }
   @Bean
   public ThreadPoolTaskExecutor vodkaPool(@Value("${vodka.thread.size}") int poolSize) {
      ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
      executor.setCorePoolSize(poolSize);
      executor.setMaxPoolSize(poolSize);
      executor.setQueueCapacity(500);
      executor.setThreadNamePrefix("vodka-");
//      executor.setRejectedExecutionHandler(new CallerRunsPolicy());
      executor.initialize();
//      executor.setTaskDecorator(propagateThreadScope);
      executor.setWaitForTasksToCompleteOnShutdown(true);
      return executor;
   }
   //</editor-fold>
}