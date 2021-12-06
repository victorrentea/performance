package victor.training.performance.spring;


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

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static java.util.Arrays.asList;
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

//   @Autowired
//   ThreadPoolTaskExecutor pool;

   public List<Object> orderDrinks() throws ExecutionException, InterruptedException {
      log.debug("Requesting drinks to my friend the barman: {}...", barman.getClass());
      long t0 = System.currentTimeMillis();


      Future<Beer> futureBeer = barman.pourBeer();
      Future<Vodka> futureVodka = barman.pourVodka();

      Beer beer = futureBeer.get();
      Vodka vodka = futureVodka.get(); // cate secunde stau aici: ~0

      long t1 = System.currentTimeMillis();
      List<Object> drinks = asList(beer, vodka);
      log.debug("Got my order in {} ms : {}", t1 - t0, drinks);
      // TODO #1: reduce the waiting time (latency)
      return drinks;
   }
}

@Service
@Slf4j
class Barman {

   @Timed("beer time")
   @Async("beerPool")
   public CompletableFuture<Beer> pourBeer() {
      log.debug("Pouring Beer..."); // 1 bere odata poate fi turnata !
      sleepq(1000); // simulez un call de retea cu RestTemplate sau JAXWS sau TCP
      return CompletableFuture.completedFuture(new Beer());
   }

   @Async("vodkaPool")
   public CompletableFuture<Vodka> pourVodka() {
      log.debug("Pouring Vodka...");
      sleepq(1000);
      return CompletableFuture.completedFuture(new Vodka());
   }
}

@Data
class Beer {
   private final String type = "blond";
}
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

   @GetMapping
   public String getDrinks() throws Exception {
      return "" + service.orderDrinks();
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