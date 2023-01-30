package victor.training.performance.spring;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jooq.lambda.Unchecked;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import victor.training.performance.spring.metrics.MonitorQueueWaitingTime;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.*;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;

import static java.lang.System.currentTimeMillis;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@RestController
@Slf4j
public class BarService {
   @Autowired
   private Barman barman;
   // java SE 10y ago

   @Autowired
   ThreadPoolTaskExecutor barPool;

   @GetMapping("drink")
   public CompletableFuture<DillyDilly> orderDrinks() throws ExecutionException, InterruptedException {
      log.debug("Requesting drinks...");
      long t0 = System.currentTimeMillis();

      CompletableFuture<Beer> beerPromise = barman.pourBeer();
      CompletableFuture<Vodka> vodkaPromise = supplyAsync(() -> barman.pourVodka(), barPool);
//      CompletableFuture<Beer> beerPromise = null; // feels like a normal method call. But it's not !!!
//      try {
//         beerPromise = barman.pourBeer();
//      } catch (IllegalStateException e) {
//         throw new RuntimeException("HANDLE"+ e); // never runs!!!
//      }
//      CompletableFuture<Vodka> vodkaPromise = barman.pourVodka(); // feels like a normal method call. But it's not !!!
      CompletableFuture<DillyDilly> futureDilly = beerPromise.thenCombine(vodkaPromise,
              (b, v) -> {
                  log.info("Now combining drinks"); // http
                 return new DillyDilly(b, v);
              });
//      DataX data = restTemplate.get();// blocked for the REST

      long t1 = System.currentTimeMillis();
      log.debug("Got my order in {} ms", t1 - t0);
      return futureDilly;
   }

   //<editor-fold desc="History Lesson: Async Servlets">
   @GetMapping("/drink-raw")
   public void underTheHood_asyncServlets(HttpServletRequest request) throws ExecutionException, InterruptedException {
      long t0 = currentTimeMillis();
      AsyncContext asyncContext = request.startAsync(); // I will write the response async

      //var futureDrinks = orderDrinks();
      var futureDrinks = supplyAsync(() -> {
         sleepMillis(2000);
         return new Beer().setType("blond");
      });
      futureDrinks.thenAccept(Unchecked.consumer(dilly -> {
         String json = new ObjectMapper().writeValueAsString(dilly);
         asyncContext.getResponse().getWriter().write(json);// the connection was kept open
         asyncContext.complete(); // close the connection to the client
      }));
      log.info("Tomcat's thread is free in {} ms", currentTimeMillis() - t0);
   }
   //</editor-fold>

   //<editor-fold desc="Starve ForkJoinPool">
   @GetMapping("starve")
   public String starveForkJoinPool() {
      int tasks = 10 * Runtime.getRuntime().availableProcessors();
      for (int i = 0; i < tasks; i++) {
         CompletableFuture.runAsync(() -> sleepMillis(1000));
      }
      // OR
      // List<Integer> list = IntStream.range(0, tasks).boxed().parallel()
      //       .map(i -> {sleepq(1000);return i;}).collect(toList());
      return "ForkJoinPool.commonPool blocked for 10 seconds";
   }
   //</editor-fold>
}

@Service
@Slf4j
class Barman {

//   @Async("barPool")
//   public CompletableFuture<Beer> pourBeer() {
   public CompletableFuture<Beer> pourBeer() {
      log.debug("Pouring Beer...");
//      if (true) {
//         throw new IllegalStateException("no beer omg!");
//      }

      CompletableFuture<Beer> beer = new AsyncRestTemplate().getForEntity("http://localhost:9999/api/beer", Beer.class).completable()
              .thenApply(e->e.getBody())
              ; // blocking I still kill threads

//      CompletableFuture<Beer> futureBeer = WebClient.create().get()........toFuture();
      // the above line is able to call rest without blocking this thread

//      sleepMillis(1000); // imagine slow REST call
      log.debug("Beer done");
      return beer;
   }

//@Async("barPool")
   public Vodka pourVodka() {
      log.debug("Pouring Vodka...");
      sleepMillis(1000); // long query
      log.debug("Vodka done");
      return new Vodka();
   }
}

@lombok.Value
class DillyDilly {
   Beer beer;
   Vodka vodka;
}
@Data
class Beer {
   private String type;
}
@Data
class Vodka {
   private final String brand = "Absolut";
}

@Configuration
class BarConfig {
   //<editor-fold desc="Custom thread pool">
   @Bean
   public ThreadPoolTaskExecutor barPool(MeterRegistry meterRegistry,
                                         @Value("${barman.thread.count}")int count) {
      ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
      executor.setCorePoolSize(count);
      executor.setMaxPoolSize(count);
      executor.setQueueCapacity(500);
      executor.setThreadNamePrefix("barman-");
      executor.setTaskDecorator(new MonitorQueueWaitingTime(meterRegistry.timer("barman-queue-time")));
      executor.setRejectedExecutionHandler(new CallerRunsPolicy());
      executor.initialize();
      return executor;
   }
   @Bean
   public ThreadPoolTaskExecutor barPool2(MeterRegistry meterRegistry,
                                         @Value("${barman.thread.count}")int count) {
      ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
      executor.setCorePoolSize(count);
      executor.setMaxPoolSize(count);
      executor.setQueueCapacity(500);
      executor.setThreadNamePrefix("barman2-");
      executor.setTaskDecorator(new MonitorQueueWaitingTime(meterRegistry.timer("barman-queue-time")));
      executor.initialize();
      return executor;
   }
   //</editor-fold>
}