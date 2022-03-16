package victor.training.performance.spring;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static java.util.Arrays.asList;
import static victor.training.performance.util.PerformanceUtil.sleepq;

@RestController
@Slf4j
public class BarService implements CommandLineRunner {
   @Autowired
   private Barman barman;

   @Override
   public void run(String... args) throws Exception { // runs at app startup
      log.debug("Got " + orderDrinks());
   }

   @Autowired
   private ThreadPoolTaskExecutor threadPool; // recent, daca exista 2 beanuri posibil de injectat, se ia Springul dupa numele campului
   @GetMapping
   public List<Object> orderDrinks() throws ExecutionException, InterruptedException {
      log.debug("Requesting drinks...");
      long t0 = System.currentTimeMillis();


      Future<Beer> futureBeer = threadPool.submit(() -> barman.pourBeer());
      Future<Vodka> futureVodka = threadPool.submit(() -> barman.pourVodka());

      log.debug("A plecat chelnerul cu comenzile");
      Beer beer = futureBeer.get(); // cat timp sta blocat aici threadul pe care s-a chemat orderDrinks() ? T=1sec
      Vodka vodka = futureVodka.get(); // T blocat = ~0


      long t1 = System.currentTimeMillis();
      List<Object> drinks = asList(beer, vodka);
      log.debug("Got my order in {} ms : {}", t1 - t0, drinks);
      return drinks;
   }

}

@Service
@Slf4j
class Barman {

   public Beer pourBeer() {
      log.debug("Pouring Beer GET HTTP...");
      boolean grav = true;
      if (grav) throw new IllegalArgumentException("Nu mai e bere");
      sleepq(1000);
      return new Beer();
   }

   public Vodka pourVodka() {
      log.debug("Pouring Vodka SELECT din DB...");
      sleepq(1000);
      return new Vodka();
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
   public ThreadPoolTaskExecutor threadPool(@Value("${barman.thread.count}") int barmanThreadCount) { // pune in spring un bean numit "threadPool"
      ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
      executor.setCorePoolSize(barmanThreadCount);
      executor.setMaxPoolSize(barmanThreadCount);
      executor.setQueueCapacity(500);
      executor.setThreadNamePrefix("barman-");
      executor.initialize();
//      executor.setTaskDecorator(propagateThreadScope);
      executor.setWaitForTasksToCompleteOnShutdown(true);
      return executor;
   }
   //</editor-fold>
}