package victor.training.performance.pools;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.SimpleThreadScope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.*;

import static java.util.concurrent.CompletableFuture.*;
import static victor.training.performance.ConcurrencyUtil.sleepq;

@EnableAsync(proxyTargetClass = true)
@SpringBootApplication(exclude = {
    DataSourceAutoConfiguration.class,
    DataSourceTransactionManagerAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class
})
public class BarmanApp implements CommandLineRunner{
   public static void main(String[] args) {
      SpringApplication.run(BarmanApp.class, args)
          ; // Note: .close to stop executors after CLRunner finishes
   }


   @Bean
   public static CustomScopeConfigurer defineThreadScope() {
      CustomScopeConfigurer configurer = new CustomScopeConfigurer();
      configurer.addScope("thread", new SimpleThreadScope()); // WARNING: Leaks memory. Prefer 'request' scope or read here: https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/support/SimpleThreadScope.html
      return configurer;
   }

   @Autowired
   private PropagateRequestContext propagateRequestContext;

   @Bean
   public ThreadPoolTaskExecutor executor() {
      ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
      executor.setCorePoolSize(1);
      executor.setMaxPoolSize(1);
      executor.setQueueCapacity(500);
      executor.setThreadNamePrefix("barman-");
      executor.initialize();
      executor.setTaskDecorator(propagateRequestContext);
      executor.setWaitForTasksToCompleteOnShutdown(true);
      return executor;
   }
   @Bean
   public ThreadPoolTaskExecutor myCustomExecutor() {
      ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
      executor.setCorePoolSize(1);
      executor.setMaxPoolSize(1);
      executor.setQueueCapacity(500);
      executor.setThreadNamePrefix("CUSTOM-");
      executor.initialize();
      executor.setTaskDecorator(propagateRequestContext);
      executor.setWaitForTasksToCompleteOnShutdown(true);
      return executor;
   }

   @Autowired
   private DrinkerService drinkerService;
   public void run(String... args) throws Exception {
      drinkerService.orderDrinks();
   }
}

class ThreadLocalHolder {
   static public ThreadLocal<String> threadUsername = new ThreadLocal<>();
}

@RequiredArgsConstructor
@Component
@Slf4j
class DrinkerService {
   private final Barman barman;

   public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
      new DrinkerService(new Barman()).orderDrinks();
      log.debug("Sent shutdown request");
      log.debug("exit main");
      sleepq(4000);
   }
   @Autowired
   private MyRequestContext requestContext;

   public CompletableFuture<DillyDilly> orderDrinks() throws ExecutionException, InterruptedException {
      String currentUsername = ThreadLocalHolder.threadUsername.get();
//      String currentUsername = requestContext.getCurrentUser();
      log.debug("Submitting order of {}  : to " + barman.getClass(), currentUsername);


      ForkJoinPool icePool = new ForkJoinPool();

//      CompletableFuture<Void> drinks = runAsync(() -> pay("drinks"));

      CompletableFuture<Beer> futureBeer = barman.pourBlondBeer()
//          .exceptionally(t -> {
////             t.printStackTrace();
//             if (t instanceof CompletionException) {
//                return barman.pourDarkBeer();
//             }
//             throw new RuntimeException(t);
//          });
      ;
      CompletableFuture<Vodka> futureVodka = barman.pourVodka()
          .thenApplyAsync(vodka -> {
             log.debug("Add ice: heavy CPU Intensive task");
             return vodka;
          }, icePool);

      return futureBeer.thenCombine(futureVodka, DillyDilly::new);
   }

   public void pay(String comand) {
      //sometime throws
   }


}

@Value
@Slf4j
class DillyDilly {
   Beer beer;
   Vodka vodka;

   public DillyDilly(Beer beer, Vodka vodka) {
      log.debug("Mixing Dilly Dilly");
      sleepq(1000);
      this.beer = beer;
      this.vodka = vodka;
   }
}

@Service
@Slf4j
class Barman {
   @Async
   public CompletableFuture<Beer> pourBlondBeer() {
      log.debug("Pouring Beer to " + ThreadLocalHolder.threadUsername.get() +"...");
//      if (true) {
//         throw new IllegalStateException("Out of blond beer!!");
//      }
      sleepq(1000);
      log.debug("End pouring");
      return completedFuture(new Beer());
   }
   @Async("myCustomExecutor")
   public CompletableFuture<Beer> pourDarkBeer() {
      log.debug("Pouring Beer to ");// + requestContext.getCurrentUser()+"...");
      sleepq(1000); // imagine network call HTTP
      log.debug("End pouring");
      return completedFuture(new Beer());
   }

   @Async
   public CompletableFuture<Vodka> pourVodka() {
      log.debug("Pouring Vodka...");
      sleepq(900); // JDBC call/ cassard
      return completedFuture(new Vodka());
   }
}

@Data
class Beer {
   private final  String type = "beer";
}

@Data
class Vodka {
   private final  String type = "stalinskaya";
}


// TODO hard-core more DeferredResult

@Slf4j
@RestController
class BarController{
   @Autowired
   private DrinkerService service;
   @Autowired
   private MyRequestContext requestContext;

   @GetMapping
   public CompletableFuture<DillyDilly> getDrinks() throws ExecutionException, InterruptedException, TimeoutException {
      try {
         ThreadLocalHolder.threadUsername.set("jdoe");
//      requestContext.setCurrentUser("jdoe");
         return service.orderDrinks();
      } finally {
         ThreadLocalHolder.threadUsername.remove();
      }
   }

}