package victor.training.performance.spring;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.concurrent.*;

import static java.lang.System.currentTimeMillis;
import static victor.training.performance.util.PerformanceUtil.sleepq;

@RestController
@Slf4j
public class BarService implements CommandLineRunner {
   @Autowired
   private Barman barman;

   @Override
   public void run(String... args) throws Exception { // runs at app startup
      orderDrinks();
   }
   private static final ExecutorService beerPool = Executors.newFixedThreadPool(1, namedFactory("beer"));
   private static final ExecutorService vodkaPool = Executors.newFixedThreadPool(1, namedFactory("vodka"));
   private static final ExecutorService uiThreadExecutor = Executors.newFixedThreadPool(1, namedFactory("ui"));


   private static ThreadFactory namedFactory(String name) {
      return r -> {
         Thread thread = new Thread(r);
         thread.setName(name);
         return thread;
      };
   }


   @GetMapping
   public void asyncServlet(HttpServletRequest request) throws ExecutionException, InterruptedException {
      AsyncContext asyncContext = request.startAsync();

      orderDrinks().thenAccept(dilly -> {
         try {
            new ObjectMapper().writeValue(asyncContext.getResponse().getWriter(), dilly);
            asyncContext.complete(); // closes the TCP conncetion
         } catch (IOException e) {
            throw new RuntimeException(e);
         }
      });
      // you exit the method ==> the conn to the client is not closed
   }

   @GetMapping("drink")
   // please imagine that this flow is a button click handler running in UIThread. at the end display to console the DillyDilly in ui thread
   public CompletableFuture<DillyDilly> orderDrinks() throws ExecutionException, InterruptedException {
      log.debug("I had some race bugs and deadlocks today, so I want to have a drink to forget about it...");
      long t0 = currentTimeMillis();



//      CompletableFuture.`w

      CompletableFuture<Beer> futureBeer =  CompletableFuture
          .supplyAsync(() -> barman.pourBeer("blond")/*, beerPool*/)
          .exceptionally(ex -> {
             ex.printStackTrace(); // ex.cause instanceof IllegalStateException  else { throw ex }rrr
             return barman.pourBeer("dark");
          })
          ;
      CompletableFuture<Vodka> futureVodka = CompletableFuture.supplyAsync(Barman::pourVodka, vodkaPool);

      log.debug("The waiter left with my 2 order");

      // NEVER do completableFuture.get()

      CompletableFuture<DillyDilly> futureDilly = futureBeer.thenCombine(futureVodka, (b, v) -> new DillyDilly(b, v));

//      futureDilly.thenAcceptAsync(dilly-> {
//         log.debug("Presenting in UI: " + dilly);
//      }, uiThreadExecutor)
//         .exceptionally(throwable -> {
//            throwable.printStackTrace();
//            return null;
//         });

      CompletableFuture.runAsync(() -> barman.curse("&^$!&^&@!^&!^%!^")); // fire and forget
      System.out.println("getting to bed");
      long t1 = currentTimeMillis();

      log.debug("Got my order in {} ms ", t1 - t0);
      return futureDilly;
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
// 1 max in parallel - device that only takes 1 call at a time
   public static Beer pourBeer(String type) {
      if (type.equals("blond")) {
         throw new IllegalStateException("Out of blond beer");
      }
//      if (true) {
//         throw new RuntimeException("Ugly!!");
//      }
      log.debug("Pouring Beer...");
      sleepq(1000); // CPU
      return new Beer(type);
   }

   // 4 max req in parallel
   public static Vodka pourVodka() {
      log.debug("Pouring Vodka...");
      sleepq(1000);
      return new Vodka();
   }
//   @Async("customPoolBeanName")
//   public CompletableFuture<Vodka> pourVodkaAsyncViaSpring() {
//      log.debug("Pouring Vodka...");
//      sleepq(1000);
//      return CompletableFuture.completedFuture(new Vodka());
//   }

   @Async
   public void curse(String curse) {
      if (curse != null) {
         throw new IllegalArgumentException("stash you");
      }
   }
}

class Beer {
   private final String type ;

   Beer(String type) {
      this.type = type;
   }

   public String getType() {
      return type;
   }


   @Override
   public String toString() {
      return "Beer{" +
             "type='" + type + '\'' +
             '}';
   }
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
   public String getDrinks() throws Exception {
      return "";
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