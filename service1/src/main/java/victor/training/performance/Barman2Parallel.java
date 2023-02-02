package victor.training.performance;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
//import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
//import org.jooq.lambda.Unchecked;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import victor.training.performance.drinks.Beer;
import victor.training.performance.drinks.DillyDilly;
import victor.training.performance.drinks.Vodka;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static java.lang.System.currentTimeMillis;

@RestController
@Slf4j
public class Barman2Parallel {
  @Autowired
  private RestTemplate rest;
  @Autowired
  private ThreadPoolTaskExecutor barPool;

  @GetMapping("/drink/parallel")
  public DillyDilly drink() throws ExecutionException, InterruptedException {
    long t0 = currentTimeMillis();
    Future<Beer> futureBeer = barPool.submit(() -> rest.getForObject("http://localhost:9999/beer", Beer.class));
    Future<Vodka> futureVodka = barPool.submit(() -> rest.getForObject("http://localhost:9999/vodka", Vodka.class));

    //  🛑 blocking http threads ~> non-blocking calls (AsyncRestTemplate or Reactive)
    Beer beer = futureBeer.get();
    Vodka vodka = futureVodka.get();
    long t1 = currentTimeMillis();
    log.debug("HTTP thread blocked for millis: " + (t1 - t0));
    return new DillyDilly(beer, vodka);
  }

  //<editor-fold desc="History Lesson: Async Servlets">
  @GetMapping("/drink-raw")
  public void underTheHood_asyncServlets(HttpServletRequest request) throws ExecutionException, InterruptedException {
    long t0 = currentTimeMillis();
    AsyncContext asyncContext = request.startAsync(); // I will write the response async

//    var futureDrinks = orderDrinks();
    var futureDrinks = new CompletableFuture<>();
    futureDrinks.thenAccept(dilly -> {
      try {
        String json = new ObjectMapper().writeValueAsString(dilly);
        asyncContext.getResponse().getWriter().write(json);// the connection was kept open
        asyncContext.complete(); // close the connection to the client
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });
    log.info("Tomcat's thread is free in {} ms", currentTimeMillis() - t0);
  }
  //</editor-fold>

  @GetMapping("buy")
  public void acceptMoneyFromClient() {
    log.debug("This is NOT allowed to timeout!!! because it takes the client's money");
  }


  @Configuration
  public static class BarPoolConfig {
    @Bean
    public ThreadPoolTaskExecutor barPool(/*MeterRegistry meterRegistry,*/ @Value("${bar.pool.size}") int barPoolSize) {
      ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
      executor.setCorePoolSize(barPoolSize);
      executor.setMaxPoolSize(barPoolSize);
      executor.setQueueCapacity(500);
      executor.setThreadNamePrefix("bar-");
//      executor.setTaskDecorator(new MonitorQueueWaitingTime(meterRegistry.timer("barman-queue-time")));
      executor.initialize();
      return executor;
    }
  }

}


