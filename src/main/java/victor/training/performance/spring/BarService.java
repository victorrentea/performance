package victor.training.performance.spring;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.*;

import static java.lang.System.currentTimeMillis;
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
   private static final ExecutorService beerPool = Executors.newFixedThreadPool(1, namedFactory("beer"));
   private static final ExecutorService vodkaPool = Executors.newFixedThreadPool(4, namedFactory("vodka"));

   private static ThreadFactory namedFactory(String name) {
      return r -> {
         Thread thread = new Thread(r);
         thread.setName(name);
         return thread;
      };
   }


   public DillyDilly orderDrinks() throws ExecutionException, InterruptedException {
      log.debug("I had some race bugs and deadlocks today, so I want to have a drink to forget about it...");
      long t0 = currentTimeMillis();


      Future<Beer> futureBeer = beerPool.submit(() -> barman.pourBeer());
      Future<Vodka> futureVodka = vodkaPool.submit(() -> barman.pourVodka());

      log.debug("The waiter left with my 2 order");

      Beer beer = futureBeer.get(); // the main() waits for 1 seconds
      Vodka vodka = futureVodka.get(); // waits here for 0 seconds
      DillyDilly dilly = new DillyDilly(beer, vodka); // 1 more sec
      long t1 = currentTimeMillis();

      log.debug("Got my order in {} ms : {}", t1 - t0, dilly);
      // TODO #1: reduce the waiting time (latency)
      return dilly;
   }
}
class DillyDilly {
   private final Beer beer;
   private final Vodka vodka;

   DillyDilly(Beer beer, Vodka vodka) {
      this.beer = beer;
      this.vodka = vodka;
      sleepq(1000);
   }

   public Beer getBeer() {
      return beer;
   }

   public Vodka getVodka() {
      return vodka;
   }
}
@Service
@Slf4j
class Barman {
// 1 max in parallel - device that only takes 1 call at a time
   public static Beer pourBeer() {
      log.debug("Pouring Beer...");
      sleepq(1000);
      return new Beer();
   }

   // 4 max req in parallel
   public static Vodka pourVodka() {
      log.debug("Pouring Vodka...");
      sleepq(1000);
      return new Vodka();
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
   public ThreadPoolTaskExecutor pool() {
      ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
      executor.setCorePoolSize(1);
      executor.setMaxPoolSize(1);
      executor.setQueueCapacity(500);
      executor.setThreadNamePrefix("barman-");
      executor.initialize();
      executor.setTaskDecorator(new TaskDecorator() {
         @Override
         public Runnable decorate(Runnable originalTaskSubmitted) { // this runs in the caller thread, immediately at submit()
            long t0 = currentTimeMillis(); // submit time
//            MDC.capture;
            return () -> {
//               MDC.restore;
               long t1 = currentTimeMillis(); // the timestamp when the taks will actually start running.
               System.out.println(t1-t0); // = queue waiting time
               originalTaskSubmitted.run();
            };
         }
      });
      executor.setWaitForTasksToCompleteOnShutdown(true);
      return executor;
   }
   //</editor-fold>
}