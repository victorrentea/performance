package victor.training.performance.spring;


import lombok.Data;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.ToString;
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

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.lang.System.currentTimeMillis;
import static victor.training.performance.util.PerformanceUtil.sleepq;

@RestController
@Slf4j
public class BarService implements CommandLineRunner {
   @Autowired
   private Barman barman;

   @Override
   public void run(String... args) throws Exception { // runs at app startup
//      log.debug("Got " + orderDrinks());
   }

   // fix asta face spring pe sub
   @SneakyThrows
   @GetMapping("doamen fereeste")
   public void method(HttpServletRequest request) {
      AsyncContext asyncContext = request.startAsync();
      orderDrinks().thenAccept(dilly -> { // callback based
         try {
            asyncContext.getResponse().getWriter().write("ss " + dilly);
            asyncContext.complete(); // se inchide TCP tunnel cu clientul
         } catch (IOException e) {
            throw new RuntimeException(e);
         }
      });
   }

//   async function f() {  async/await
//      let user:User =await repo.loadById(1);
//      console.log(user);
   //   user.name
//   }


   @Autowired
   private ThreadPoolTaskExecutor threadPool; // recent, daca exista 2 beanuri posibil de injectat, se ia Springul dupa numele campului
   @GetMapping("drink")
   public CompletableFuture<DillyDilly> orderDrinks() throws ExecutionException, InterruptedException {
      log.debug("Requesting drinks...");
      long t0 = currentTimeMillis();

      // 2 mari directii pt procesare non blocanta super-scalabila;
      // 1) WebFlux (reactor) Spring 5 : Mono/Flux < cea mai grea paradigma de programare din lume "reactive programming"
      // 2) CompletableFuture Java8
      // 3) NU in java: async/await in JS/TS/C#, coroutine in Kotlin/Swift/C++, Akka Actors in Scala.
      // 4) vis de vara: Green Thread in Java (project Loom)

      // pe numele lui adevarat cunoscut ca "promise" (din FE)

      CompletableFuture<Void> futurePayment = CompletableFuture.runAsync(() -> acceptPayment());

      CompletableFuture<Beer> futureBeer = futurePayment.thenApplyAsync(v -> barman.pourBeer());
      CompletableFuture<Vodka> futureVodka = futurePayment.thenApplyAsync(v -> barman.pourVodka())
          .thenApply(Vodka::puneGheata);

      CompletableFuture<DillyDilly> futureDilly = futureBeer
          .thenCombine(futureVodka, (beer, vodka) -> new DillyDilly(beer, vodka));
      // - @Async
      // ================
      // Thread Pool

      long t1 = currentTimeMillis();
      log.debug("Got my order in {} ms", t1 - t0);
      log.debug("ACum ies din functie");
      return null;
   }

   private void acceptPayment() {
      sleepq(1000); // payU payment gateway
      log.debug("Payment done");
   }
}
@Slf4j
@ToString
@Getter
class DillyDilly {
   private final Beer beer;
   private final Vodka vodka;

   DillyDilly(Beer beer, Vodka vodka) {
      this.beer = beer;
      this.vodka = vodka;
      log.debug("Amestec licorile");
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
class Vodka {
   private final String brand = "Absolut";
   private final boolean gheata;

   public Vodka(boolean gheata) {
      this.gheata = gheata;
   }
   public Vodka() {
      this(false);
   }

   public Vodka puneGheata() {
      sleepq(1000);
//      this.gheata = true; // NICIODATA in multithreaded code sa nu MUTEZI STARE!
      return new Vodka(true);
   }

   public String getBrand() {
      return this.brand;
   }

   public boolean isGheata() {
      return this.gheata;
   }

   public String toString() {
      return "Vodka(brand=" + this.getBrand() + ", gheata=" + this.isGheata() + ")";
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