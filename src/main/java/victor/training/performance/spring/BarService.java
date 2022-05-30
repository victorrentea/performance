package victor.training.performance.spring;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static java.util.Arrays.asList;
import static victor.training.performance.util.PerformanceUtil.sleepq;

@RestController
@Slf4j
@RequestMapping("bar/drink")
public class BarService implements CommandLineRunner {
   @Autowired
   private Barman barman;

   @Override
   public void run(String... args) throws Exception { // runs at app startup
//      log.debug("Got " + orderDrinks());
   }

   @Autowired
   ThreadPoolTaskExecutor pool;

   @GetMapping
   public CompletableFuture<List<Object>> orderDrinks() throws ExecutionException, InterruptedException {
      log.debug("Requesting drinks...");
      long t0 = System.currentTimeMillis();

//      Future<Beer> futureBeer = pool.submit(() -> barman.pourBeer());
//      Future<Vodka> futureVodka = pool.submit(() -> barman.pourVodka());

      // daca nu mentionezi un executor ca ultim param, metodele CompletableFuture executa by default pe ForkJoinPool.commonPool
      // asta e nativ in orice JVM 8+, si are exact size=N_CPU-1
      CompletableFuture<Beer> futureBeer = CompletableFuture.supplyAsync(() ->barman.pourBeer() ,pool);
      CompletableFuture<Vodka> futureVodka = CompletableFuture.supplyAsync(() ->barman.pourVodka() ,pool);
      // in java CompletableFuture = = = promise.

      log.debug("Mi-a luat comanda");

//      Beer beer = futureBeer.get(); // cat sta main aici ? ~ 1 sec
//      Vodka vodka = futureVodka.get(); // cat sta main aici blocat ? ~0

      CompletableFuture<List<Object>> futureDrinks = futureBeer.thenCombine(futureVodka, (beer, vodka) -> List.of(beer, vodka));

      long t1 = System.currentTimeMillis();
//      List<Object> drinks = asList(beer, vodka);
      log.debug("Got my order in {} ms : ", t1 - t0);
      return futureDrinks;//.thenAccept(list-> asyncOntext...);
   }



}

class Anii2000 implements Servlet {

   @Override
   public void init(ServletConfig servletConfig) throws ServletException {

   }

   @Override
   public ServletConfig getServletConfig() {
      return null;
   }

   @Override
   public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
      HttpServletRequest request = (HttpServletRequest) servletRequest;
      AsyncContext asyncContext = request.startAsync();


      {// in alt thread
         asyncContext.getResponse().getWriter().write("Gata");
         asyncContext.complete();
      }
   }


   @Override
   public String getServletInfo() {
      return null;
   }

   @Override
   public void destroy() {

   }
}

@Service
@Slf4j
class Barman {

   public Beer pourBeer() {
      log.debug("Pouring Beer...");
//      WebClient (webflux) - Flux/Mono
//      CompletableFuture<ResponseEntity<Object>> completable = new AsyncRestTemplate().exchange().completable();
      sleepq(10000); // GET
      return new Beer();
   }

   public Vodka pourVodka() {
      log.debug("Pouring Vodka...");
      sleepq(1000); // POST., select
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
//@Slf4j
//@RequestMapping("bar/drink")
//@RestController
//class BarController {
//   //<editor-fold desc="Web">
//   @Autowired
//   private BarService service;
//
//   @GetMapping
//   public String getDrinks() throws Exception {
//      return "" + service.orderDrinks();
//   }
//   //</editor-fold>
//}

// TODO The Foam Problem: https://www.google.com/search?q=foam+beer+why

@Configuration
class BarConfig {
   //<editor-fold desc="Spring Config">
   //   @Autowired
//   private PropagateThreadScope propagateThreadScope;

   @Bean
   public ThreadPoolTaskExecutor pool(@Value("${barman.thread.count}")int barmanCount) {
      ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
      executor.setCorePoolSize(barmanCount);
      executor.setMaxPoolSize(barmanCount);
      executor.setQueueCapacity(500);
      executor.setThreadNamePrefix("barman-");
      executor.initialize();
//      executor.setTaskDecorator(propagateThreadScope);
      executor.setWaitForTasksToCompleteOnShutdown(true);
      return executor;
   }
   //</editor-fold>
}