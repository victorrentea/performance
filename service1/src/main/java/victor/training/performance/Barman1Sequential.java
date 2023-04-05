package victor.training.performance;

import lombok.extern.slf4j.Slf4j;
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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import static java.lang.System.currentTimeMillis;

@RestController
@Slf4j
public class Barman1Sequential {
  @Autowired
  private RestTemplate rest;

  @Autowired
  private ThreadPoolTaskExecutor pool ;

  //  private static final ExecutorService pool = Executors.newFixedThreadPool(25);
  @GetMapping({"/drink/sequential","/drink"})
  public DillyDilly drink() throws ExecutionException, InterruptedException {
    long t0 = currentTimeMillis();

    Future<Beer> futureBeer = pool.submit(() -> pourBeer());
    Future<Vodka> futureVodka = pool.submit(() -> pourVodka());

    Beer beer = futureBeer.get(); // aici sta threadul http 1 secunda
    Vodka vodka = futureVodka.get(); // aici sta threadul http 0 sec

    long t1 = currentTimeMillis();
    log.info("HTTP thread blocked for millis: " + (t1 - t0));
    return new DillyDilly(beer,vodka);
  }
  // new Thread()
  // Executor
  // CompletableFuture
  // @Async
  // JoinFork

  private Vodka pourVodka() {
    log.info("Torn vodka");
    return rest.getForObject("http://localhost:9999/vodka", Vodka.class);
  }

  private Beer pourBeer() {
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