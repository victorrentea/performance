package victor.training.performance;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import victor.training.performance.drinks.Beer;
import victor.training.performance.drinks.DillyDilly;
import victor.training.performance.drinks.Vodka;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static java.lang.System.currentTimeMillis;

@Slf4j
@RestController
public class Barman {
  @Autowired
  private RestTemplate rest;
  @Autowired
  ThreadPoolTaskExecutor poolBar;

  @GetMapping("/drink")
  public DillyDilly drink() throws ExecutionException, InterruptedException {
    long t0 = currentTimeMillis();

    // never execute network calls in the internal JVM ForkJoinPool.commonPool()
    // 1) starvation
    // 2) thread metadata TODO
    // Rule in a BE system, all methods ...Async on CompletableFuture should take as last argument the executor to run on
    Future<Beer> futureBeer = CompletableFuture.supplyAsync(
        () -> fetchBeer(), poolBar);

    Future<Vodka> futureVodka = CompletableFuture.supplyAsync(() ->
        rest.getForObject("http://localhost:9999/vodka", Vodka.class),
        poolBar);

    DillyDilly dilly = new DillyDilly(futureBeer.get(), futureVodka.get());

    log.info("HTTP thread blocked for {} durationMillis", currentTimeMillis() - t0);
    return dilly;
  }

  private Beer fetchBeer() {
    log.info("Where am I");
    return rest.getForObject("http://localhost:9999/beer", Beer.class);
  }
}
