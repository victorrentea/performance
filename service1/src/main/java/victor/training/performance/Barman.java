package victor.training.performance;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import victor.training.performance.drinks.Beer;
import victor.training.performance.drinks.DillyDilly;
import victor.training.performance.drinks.Vodka;

import java.util.concurrent.*;

import static java.lang.System.currentTimeMillis;
import static java.util.concurrent.CompletableFuture.supplyAsync;

@RestController
@Slf4j
public class Barman {
  @Autowired
  private RestTemplate rest;
  @Autowired
  ThreadPoolTaskExecutor barPool;
  // beneficii: 1) poate fi configurat in framework cu proprietati de config
  // 2) propaga TraceID
  // 3) shutdown automat la oprirea app

  @PostMapping("acceptBet")
  public void method() {

  }

  // Endpoint REST non-blocant acolo unde doare pentru a nu starva th poolul Tomcat,
  // sa lasi alte endpointuri sa ruleze
  @GetMapping("/drink")
  public CompletableFuture<DillyDilly> drink() throws ExecutionException, InterruptedException {
    long t0 = currentTimeMillis();
    CompletableFuture<Beer> promiseBeer = supplyAsync(() -> fetchBeer(), barPool);
    CompletableFuture<Vodka> promiseVodka = supplyAsync(() -> fetchVodka(), barPool);
    // promise(JS) === CompletableFuture

    CompletableFuture<DillyDilly> promiseDilly = promiseBeer.thenCombine(promiseVodka,
        (beer, vodka) -> new DillyDilly(beer, vodka));

    long t1 = currentTimeMillis();
    log.info("HTTP thread blocked for millis: " + (t1 - t0));
    return promiseDilly;
  }

  private Vodka fetchVodka() {
    log.info("Pouring vodka...");
    return rest.getForObject("http://localhost:9999/vodka", Vodka.class);
  }

  private Beer fetchBeer() {
    if (true) {
      throw new IllegalArgumentException("Nu mai e bere");
    }
    log.info("Pouring beer...");
    return rest.getForObject("http://localhost:9999/beer", Beer.class);
  }
}
