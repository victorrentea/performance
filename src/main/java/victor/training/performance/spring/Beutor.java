package victor.training.performance.spring;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.annotation.Timed;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
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

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static victor.training.performance.util.PerformanceUtil.sleepq;

@Slf4j
@Component
public class Beutor implements CommandLineRunner {
   @Autowired
   private Barman barman;

   @Override
   public void run(String... args) throws Exception { // runs at app startup
//      log.debug("Got " + orderDrinks());
   }
//   public static final ExecutorService pool = Executors.newFixedThreadPool(4);

   @Autowired
   ThreadPoolTaskExecutor beerPool;
   @Autowired
   ThreadPoolTaskExecutor vodkaPool;

   public CompletableFuture<DillyDilly> orderDrinks() throws ExecutionException, InterruptedException {
      log.debug("Requesting drinks to my friend the barman: {}...", barman.getClass());
      long t0 = System.currentTimeMillis();


      CompletableFuture<Beer> futureBeer = CompletableFuture.supplyAsync( () -> barman.pourBeer() , beerPool);
      CompletableFuture<Vodka> futureVodka =  CompletableFuture.supplyAsync( () -> barman.pourVodka() , vodkaPool);

//      Beer beer = futureBeer.get(); // antipattern:  nu ai voie sa faci .get pe un CompletableFuture

//      CompletableFuture<VodkaWithIce> vodkaWithIceCompletableFuture = futureVodka.thenApply(v -> addIce(v));


      CompletableFuture<DillyDilly> futureDilly = futureBeer
          .thenCombineAsync(futureVodka, (b, v) -> new DillyDilly(b, v));

      barman.injura("*!$!^!*%(#!*%*!(^!*#(&$!&@% Mos Nicolae &!#%&&");
      log.debug("Fug repede");
      log.debug("La mine acasa in patuc");

      long t1 = System.currentTimeMillis();
      log.debug("Ies din functie in {} ms ", t1 - t0);
      return futureDilly;
   }

   private VodkaWithIce addIce(Vodka v) {
      return new VodkaWithIce(v, "Gheata");
   }
}

@Slf4j
@Data
class DillyDilly {
   private final Beer beer;
   private final Vodka vodka;

   public DillyDilly(Beer beer, Vodka vodka) {
      this.beer = beer;
      this.vodka = vodka;
      log.debug("Ameste cocktail");
      sleepq(1000);
   }
}

class VodkaWithIce {
  private final  Vodka vodka;
   private final  String gheata;

   VodkaWithIce(Vodka vodka, String gheata) {
      this.vodka = vodka;
      this.gheata = gheata;
   }

   public String getGheata() {
      return gheata;
   }
}

@Service
@Slf4j
class Barman {

   @Timed("beer time")
   public Beer pourBeer() {
      log.debug("Pouring Beer..."); // 1 bere odata poate fi turnata !
      sleepq(1000); // simulez un call de retea cu RestTemplate sau JAXWS sau TCP
      return new Beer();
   }

   public Vodka pourVodka() {
      log.debug("Pouring Vodka...");
      sleepq(1000);
      return new Vodka();
   }

   @Async
   public void injura(String uratura) {
      if (uratura != null) {
         /// send email
         // persist stuff
         throw new IllegalArgumentException("Iti fac buzunar!");
      }

   }
}
//@Service
//@Slf4j
//class Barman {
//
//   @Timed("beer time")
//   @Async("beerPool")
//   public CompletableFuture<Beer> pourBeer() {
//      log.debug("Pouring Beer..."); // 1 bere odata poate fi turnata !
////      if (true) {
////         throw new IllegalStateException("NU MAI E BERE!");
////      }
//      sleepq(1000); // simulez un call de retea cu RestTemplate sau JAXWS sau TCP
//      return CompletableFuture.completedFuture(new Beer());
//   }
//
//   @Async("vodkaPool")
//   public CompletableFuture<Vodka> pourVodka() {
//      log.debug("Pouring Vodka...");
//      sleepq(1000);
//      return CompletableFuture.completedFuture(new Vodka());
//   }
//
//   @Async
//   public void injura(String uratura) {
//      if (uratura != null) {
//         /// send email
//         // persist stuff
//         throw new IllegalArgumentException("Iti fac buzunar!");
//      }
//
//   }
//}

@Data
class Beer {
   private final String type = "blond";
}
@Data
class Vodka {
   private final String brand = "Absolut";
}

// TODO when called from web, protect the http thread
@Slf4j
@RequestMapping("bar/drink")
@RestController
class BarController {
   //<editor-fold desc="Web">
   @Autowired
   private Beutor service;

//   @GET
   public void getAsyncDrinks(HttpServletRequest response) throws ExecutionException, InterruptedException {
      AsyncContext asyncContext = response.startAsync();

      service.orderDrinks()
          .thenAccept(dilly -> {
             try {
                new ObjectMapper().writeValue(asyncContext.getResponse().getOutputStream(), dilly);
             } catch (IOException e) {
                throw new RuntimeException(e);
             }
          });

   }
   @GetMapping
   public CompletableFuture<DillyDilly> getDrinks() throws Exception {
      return service.orderDrinks();
   }
   @GetMapping("unde/buda")
   public String undeBuda() throws Exception {
      return "in fund pe dreapta";
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
   public ThreadPoolTaskExecutor beerPool(@Value("${beer.barman.count}") int barmanCount) {
      ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
      executor.setCorePoolSize(barmanCount);
      executor.setMaxPoolSize(barmanCount);
      executor.setQueueCapacity(500);
      executor.setThreadNamePrefix("beer-");
//      executor.setRejectedExecutionHandler(new );
      executor.initialize();
      executor.setWaitForTasksToCompleteOnShutdown(true);
      return executor;
   }
   @Bean
   public ThreadPoolTaskExecutor vodkaPool(@Value("${vodka.barman.count}") int barmanCount) {
      ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
      executor.setCorePoolSize(barmanCount);
      executor.setMaxPoolSize(barmanCount);
      executor.setQueueCapacity(500);
      executor.setThreadNamePrefix("vodka-");
//      executor.setRejectedExecutionHandler(new );
      executor.initialize();
      executor.setWaitForTasksToCompleteOnShutdown(true);
      return executor;
   }
   //</editor-fold>
}