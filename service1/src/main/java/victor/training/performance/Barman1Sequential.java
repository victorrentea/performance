package victor.training.performance;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
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
import java.util.concurrent.ThreadPoolExecutor;

import static java.lang.System.currentTimeMillis;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.supplyAsync;

@RestController
@Slf4j
public class Barman1Sequential {
  @Autowired
  private RestTemplate rest;

  @Autowired
  private ThreadPoolTaskExecutor pool;

  //  private static final ExecutorService pool = Executors.newFixedThreadPool(25);
  @GetMapping({"/drink/sequential", "/drink"})
  public CompletableFuture<DillyDilly> drink() throws ExecutionException, InterruptedException {
    long t0 = currentTimeMillis();

    CompletableFuture<Beer> beerPromise = supplyAsync(() -> altServiciu.pourBeer(), pool)
            .exceptionally(e -> new Beer().setType("draft"));
    CompletableFuture<Vodka> vodkaPromise = altServiciu.pourVodka();

    CompletableFuture<DillyDilly> dillyPromise = beerPromise.thenCombine(vodkaPromise, (b, v) -> new DillyDilly(b, v));
    // best practice, orice ...Async method tre sa ia param un thread pool manangeuit de Spring

    log.info("HTTP thread blocked for millis: " + (currentTimeMillis() - t0));
    return dillyPromise;
  }

  public void springuInSpate(HttpServletRequest request) throws ExecutionException, InterruptedException {
    AsyncContext asyncContext = request.startAsync();
    CompletableFuture<DillyDilly> dillyPromise = drink();
    dillyPromise.thenAccept(dilly -> {
      try {
        asyncContext.getResponse().getWriter().write(dilly.toString());
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      asyncContext.complete();
    });
  }

  @Autowired
  private AltServiciu altServiciu;
}
@Service
@Slf4j
class AltServiciu {
  @Autowired
  private RestTemplate rest;

  @Async("pool")
  public CompletableFuture<Vodka> pourVodka() {
    log.info("Torn vodka");
    return completedFuture(rest.getForObject("http://localhost:9999/vodka", Vodka.class));
  }

  public Beer pourBeer() {
    log.info("Torn bere");
    return rest.getForObject("http://localhost:9999/beer", Beer.class);
  }
}


@Configuration
class MyConfig {
  @Bean
  public ThreadPoolTaskExecutor pool(@Value("${pool.size}") int barPoolSize) {
    // spring's thread pool
    ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
    pool.setCorePoolSize(barPoolSize);
    pool.setMaxPoolSize(barPoolSize);
    pool.setQueueCapacity(100);
    pool.setWaitForTasksToCompleteOnShutdown(true);
    pool.setAwaitTerminationSeconds(10);
    pool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    pool.initialize();
    return pool;
  }
}