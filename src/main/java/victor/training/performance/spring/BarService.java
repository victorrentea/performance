package victor.training.performance.spring;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import victor.training.performance.spring.threadscope.PropagateThreadScope;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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

//   @Autowired
//   ThreadPoolTaskExecutor pool;

   @GetMapping
   public CompletableFuture<List<Object>> orderDrinks() throws ExecutionException, InterruptedException {
      log.debug("Requesting drinks...");
      long t0 = System.currentTimeMillis();

//      Future<Beer> futureBeer = pool.submit(() -> barman.pourBeer());
//      Future<Vodka> futureVodka = pool.submit(() -> barman.pourVodka());

      // daca nu mentionezi un executor ca ultim param, metodele CompletableFuture executa by default pe ForkJoinPool.commonPool
      // asta e nativ in orice JVM 8+, si are exact size=N_CPU-1

      CompletableFuture<Void> payment = CompletableFuture.runAsync(() -> {
         log.debug("Payment");
         sleepq(1000);
      });

      CompletableFuture<Beer> futureBeer = payment.thenCompose(v -> barman.pourBeer()
//              .exceptionally(e -> new Beer("blonda"))
              );

      CompletableFuture<Vodka> futureVodka = payment.thenCompose(v -> barman.pourVodka());
      // in java CompletableFuture = = = promise.

      log.debug("Mi-a luat comanda");

//      Beer beer = futureBeer.get(); // cat sta main aici ? ~ 1 sec
//      Vodka vodka = futureVodka.get(); // cat sta main aici blocat ? ~0

      CompletableFuture<List<Object>> futureDrinks = futureBeer.thenCombine(futureVodka, (beer, vodka) -> List.of(beer, vodka));
//      CompletableFuture<List<Object>> futureDrinks = futureBeer.thenApply(beer -> List.of(beer, vodka));

      long t1 = System.currentTimeMillis();
//      List<Object> drinks = asList(beer, vodka);
      log.debug("Got my order in {} ms : ", t1 - t0);
      return futureDrinks;//.thenAccept(list-> asyncOntext... scrie pe HTTP response);
   }

   @PostMapping
   public String uploadANdProcessFile(MultipartFile file2G) throws IOException {
      File temp = Files.createTempFile("temmp", ".dat").toFile();
      try (FileOutputStream fos = new FileOutputStream(temp)) {
         IOUtils.copy(file2G.getInputStream(), fos);
      }
      barman.processFile(temp);
      return "AM primit; id= ceva";
       // + un endpoint de tracking de comenzi.
      // ori clientul imi da un HTTP/MQ endpoint la care sa trimit cand e gata.
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
   @Async
   public void processFile(File file) {
   //30 min
   }

   public void init() {
   }

   @Async("beerPool") // numele beanului de thread pool
   public CompletableFuture<Beer> pourBeer() {
      log.debug("Pouring Beer...");
//      if (true) throw new IllegalArgumentException("NU MAI EBERE !");
//      WebClient (webflux) - Flux/Mono
//      CompletableFuture<ResponseEntity<Object>> completable = new AsyncRestTemplate().exchange().completable();
      sleepq(1000); // GET
      return CompletableFuture.completedFuture(new Beer());
   }

   public void method() {
      pourVodka(); // NU RULEAZA ASYNC ca nu trece prin proxyuri
   }

   @Async("vodkaPool")
   public CompletableFuture<Vodka> pourVodka() {
      log.debug("Pouring Vodka...");
      sleepq(1000); // POST., select
      return CompletableFuture.completedFuture(new Vodka());
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
//      @Autowired
//   private PropagateThreadScope propagateThreadScope;

   @Bean
   public ThreadPoolTaskExecutor beerPool(@Value("${beer.thread.count}")int barmanCount) {
      ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
      executor.setCorePoolSize(barmanCount);
      executor.setMaxPoolSize(barmanCount);
      executor.setQueueCapacity(500);
      executor.setThreadNamePrefix("beer-");
      executor.initialize();
//      executor.setTaskDecorator(propagateThreadScope);
      executor.setWaitForTasksToCompleteOnShutdown(true);
      return executor;
   }
   @Bean
   public ThreadPoolTaskExecutor vodkaPool(@Value("${vodka.thread.count}")int barmanCount) {
      ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
      executor.setCorePoolSize(barmanCount);
      executor.setMaxPoolSize(barmanCount);
      executor.setQueueCapacity(500);
      executor.setThreadNamePrefix("vodka-");
      executor.initialize();
//      executor.setTaskDecorator(propagateThreadScope);
      executor.setWaitForTasksToCompleteOnShutdown(true);
      return executor;
   }
   //</editor-fold>
}