package victor.training.performance.spring;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.*;

import static victor.training.performance.util.PerformanceUtil.sleepq;

@Component
@Slf4j
public class BarService implements CommandLineRunner {
   @Autowired
   private Barman barman;

   @Override
   public void run(String... args) throws Exception { // runs at app startup
      log.debug("Got " + orderDrinks());
   }

   public CompletableFuture<DillyDilly> orderDrinks() throws ExecutionException, InterruptedException, TimeoutException {
      log.debug("Requesting drinks...");
      long t0 = System.currentTimeMillis();

      ExecutorService threadPool = Executors.newFixedThreadPool(2);

      // fix promise-ul din nodeJS
      // Mono.fromSupplier()
      CompletableFuture<Beer> beerPromise = CompletableFuture.supplyAsync(() -> barman.pourBeer());// cat sta aici = 0
      CompletableFuture<Vodka> vodkaPromise = CompletableFuture.supplyAsync(() -> barman.pourVodka());// cat sta aici = 0

//      Beer beer = beerPromise.get();
//      Vodka vodka = vodkaPromise.get(); // cat timp sta main aici in asteptare = 0 sec, ca deja a trecut acea secunda


      threadPool.submit(() -> {
         // FIre and forget: (log in DB, send email...):
         // nu astepti dupa procesare
         // dar nici nu poti sa afli daca a reusit sau nu ==> problema: sa nu scapi exceptii => Logging de errori
         barman.injura("861&$!#&$^!&^&!@%$");
      });


      CompletableFuture<DillyDilly> dillyPromise = beerPromise.thenCombineAsync(vodkaPromise, (b, v) -> new DillyDilly(b, v));

      long t1 = System.currentTimeMillis();
//      DillyDilly drinks = dillyPromise.get();
      log.debug("Got my order in {} ms :", t1 - t0);
      return dillyPromise;
   }
}


@lombok.Value
class DillyDilly {
   Beer beer;
   Vodka vodka;
}

@Service
@Slf4j
class Barman {

   public Beer pourBeer() {
      log.debug("Pouring Beer...");
//      if (true) {
//         throw new IllegalArgumentException("NU mai e bere!");
//      }
      sleepq(1000); // REST/SOAP/RMI call
      return new Beer();
   }

   public Vodka pourVodka() {
      log.debug("Pouring Vodka...");
      sleepq(1000); // SQL
      return new Vodka();
   }

   public void injura(String s) {
      if (s != null) {
         System.err.println("CHIAR SE ARUNCA");
         throw new IllegalArgumentException("Iti fac buzunar!");
      }
   }
}

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
   public ThreadPoolTaskExecutor pool(@Value("${barman.count}") int barmanCount) {
      ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
      executor.setCorePoolSize(barmanCount);
      executor.setMaxPoolSize(barmanCount);
      executor.setQueueCapacity(500);
      executor.setThreadNamePrefix("barman-");
//      executor.settask
      executor.initialize();
//      executor.setKeepAliveSeconds();
//      executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
//      executor.setTaskDecorator(propagateThreadScope);
      executor.setWaitForTasksToCompleteOnShutdown(true);
      return executor;
   }
   //</editor-fold>
}