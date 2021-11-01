package victor.training.performance.spring;


import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;

import static java.util.Arrays.asList;
import static victor.training.performance.util.PerformanceUtil.sleepq;

@Component
@Slf4j
@RestController("bar")
public class BarService implements CommandLineRunner {
   @Autowired
   private Barman barman;

   @Override
   public void run(String... args) throws Exception { // runs at app startup
//      log.debug("Got " + orderDrinks());
   }

   @Autowired
   ThreadPoolTaskExecutor pool;

   @GetMapping("drink")
   @SneakyThrows
   public List<Object> orderDrinks() {
      log.debug("Requesting drinks...");
      long t0 = System.currentTimeMillis();

      Future<Beer> futureBeer = pool.submit(() -> barman.pourBeer());
      Future<Vodka> futureVodka = pool.submit(() -> barman.pourVodka());

      Beer beer = futureBeer.get(); // http thread sta aici 1 sec
      Vodka vodka = futureVodka.get(); // main nu mai sta deloc aici, + 1 ca a stat taskul in coada


      long t1 = System.currentTimeMillis();
      List<Object> drinks = asList(beer, vodka);

//      pool.submit(() -> barman.injura("!$&!%@!%$^%^@!*^#"));

      log.debug("Got my order in {} ms : {}", t1 - t0, drinks);
      return drinks;
   }

}

@Service
@Slf4j
class Barman {
   public Beer pourBeer() {
      log.debug("Pouring Beer...");
      sleepq(1000); // call de API REST
//      if (true) throw new IllegalArgumentException("Nu mai e bere!!! Drama!");
      log.debug("AM terminat berea");
      return new Beer();
   }

   public Vodka pourVodka() {
      log.debug("Pouring Vodka...");
      sleepq(1000); // SQL CRIMINAL
      return new Vodka();
   }

   public void injura(String uratura) {
      if (uratura != null) {
         log.error("Il omoara");
         throw new IllegalArgumentException("Iti fac buzunar");
      }
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
   public ThreadPoolTaskExecutor pool(@Value("${barman.count}") int barmanCount) {
      ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
      executor.setCorePoolSize(barmanCount);
      executor.setMaxPoolSize(barmanCount);
//      executor.setCorePoolSize(8);
//      executor.setMaxPoolSize(8); // mai repede <<< in general 95% din cazuri lasa MAX=CORE
//      executor.setMaxPoolSize(40); // mult mai incet decat daca-i chemai APIul cu max 8 in paralel.
      executor.setQueueCapacity(500);
      executor.setThreadNamePrefix("barman-");
      executor.initialize();
//      executor.setTaskDecorator(propagateThreadScope);
      executor.setWaitForTasksToCompleteOnShutdown(true);
      executor.setRejectedExecutionHandler(new CallerRunsPolicy());
      return executor;
   }
   //</editor-fold>




   // task avg duration = 1sec; am 20 threaduri; ==> 500 queue size ==> avg WAITING TIME
   // = 500 / 20 * 1 sec = 25 sec = e tolerabil  pt clienti ?
}