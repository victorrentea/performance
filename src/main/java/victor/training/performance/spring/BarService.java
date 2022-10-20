package victor.training.performance.spring;


import io.micrometer.core.instrument.MeterRegistry;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.spring.metrics.MonitorQueueWaitingTimeTaskDecorator;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.*;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static victor.training.performance.util.PerformanceUtil.sleepq;

@RestController
@Slf4j
public class BarService  {
   @Autowired
   private Barman barman;


//   private static final ExecutorService threadPool = Executors.newCachedThreadPool();
   // prea multe threaduri

//   private static final ExecutorService threadPool = Executors.newFixedThreadPool(10);
   // ce poa sa mearga rau daca coada e infinita?
   // - astept prea mult: clientul n-are atata rabdare
   // - OOME daca cine da in tine nu se blocheaza dupa rezultat  = "fire and forget"

//   private static final ExecutorService pool =
//              new ThreadPoolExecutor(
//                      10,
//                      10,
//                      2, TimeUnit.SECONDS, // worker threadul moare dupa 2 sec de inactiviate
//                      new ArrayBlockingQueue<>(10) // max 10 in coada,
//                      , new ThreadPoolExecutor.CallerRunsPolicy()
//              );
   // ce poa sa mearga rau?
   // - sa te REFUZE executorul: n-avem nici workeri nici loc in coada:
   //    expui catre caller exceptie.

   @Autowired
   ThreadPoolTaskExecutor pool;


   @GetMapping("drink")
   public CompletableFuture<DillyDilly> orderDrinks() throws ExecutionException, InterruptedException {
      log.debug("Requesting drinks...");
      long t0 = System.currentTimeMillis();

      // ( daca faci DOAR procesor, NU iti creezi thread pooluri noi, ci folosesti
      // a) CompletableFuture.supplyAsync(() -> cpuWork());
      // b) list.parallelStream()
      // ambele ruleaza pe ForkJoinPool.commonPool() <
      // locu unic in JVM unde submiti DOAR munca de CPU (size = N_cpu - 1)

      // promise-uri din js === CompletableFuture din Java
      CompletableFuture<Beer> futureBeer = supplyAsync(barman::pourBeer, pool);
      CompletableFuture<Vodka> futureVodka = supplyAsync(barman::pourVodka, pool);


      // folosirea thread pool pentru THROTTLING
      // CompletableFuture.supplyAsync( ()-> repo.fatPig(), // max 4 in parallel te rog
      //      executorCuDoar4ThreaduriCaSaProtejamBazaDePorci())
      //      .get();

      CompletableFuture<DillyDilly> futureDilly = futureBeer
              .thenCombine(futureVodka, DillyDilly::new);

      long t1 = System.currentTimeMillis();
      log.debug("Threadul tomcatului se intoarce in piscina dupa doar {} ms", t1 - t0);
      return futureDilly;
   }

//   public void istorie(HttpServletRequest request) throws ExecutionException, InterruptedException {
//      AsyncContext fff = request.startAsync();
//
//
//      orderDrinks().thenAccept(dilly -> {
//         fff.getResponse().getWriter().write(dilly.toString());
//         fff.complete();
//         // callback
//      });
//      // threadul e liber, dar pe promise a ramas un callback
//   }
}



@Service
@Slf4j
class Barman {

   public Beer pourBeer() {
//      if (true) {
//         throw new IllegalArgumentException(" NU MAI E BERE 😨");
//      }
      log.debug("Pouring Beer...");
      sleepq(1000); // HTTP REST CALL
      log.debug("Beer done");
      return new Beer("blond");
   }

   public Vodka pourVodka() {
      log.debug("Pouring Vodka...");
      sleepq(1000); // 'fat sql' / WSDL call, apel de desenat rosia pe cantar.
      log.debug("Vodka done");
      return new Vodka();
   }
}

@Data
class DillyDilly {
   private final Beer beer;
   private final Vodka vodka;
}

@Data
class Beer {
   private final String type;
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


   //</editor-fold>
}


@Configuration
class BarConfig {
   //<editor-fold desc="Spring Config">
   @Bean
   public ThreadPoolTaskExecutor pool(MeterRegistry meterRegistry, @Value("${barPool.size}") int nustiu) {
      ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
      executor.setCorePoolSize(nustiu);
      executor.setMaxPoolSize(nustiu);
      executor.setQueueCapacity(500);
      executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
      executor.setThreadNamePrefix("barman-");
      executor.setTaskDecorator(new MonitorQueueWaitingTimeTaskDecorator(meterRegistry.timer("barman-queue-time")));
      executor.initialize();
      return executor;
   }
   //</editor-fold>
}