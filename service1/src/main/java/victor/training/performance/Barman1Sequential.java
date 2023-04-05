package victor.training.performance;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import victor.training.performance.drinks.Beer;
import victor.training.performance.drinks.DillyDilly;
import victor.training.performance.drinks.Vodka;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.lang.System.currentTimeMillis;

@RestController
@Slf4j
public class Barman1Sequential {
  @Autowired
  private RestTemplate rest;

  private static final ExecutorService pool = Executors.newFixedThreadPool(2);
  @GetMapping({"/drink/sequential","/drink"})
  public DillyDilly drink() throws ExecutionException, InterruptedException {
    long t0 = currentTimeMillis();

    Future<Beer> futureBeer = pool.submit(() -> pourBeer());
    Future<Vodka> futureVodka = pool.submit(() -> pourVodka());

    Beer beer = futureBeer.get();
    Vodka vodka = futureVodka.get();

    long t1 = currentTimeMillis();
    log.debug("HTTP thread blocked for millis: " + (t1 - t0));
    return new DillyDilly(beer,vodka);
  }
  // new Thread()
  // CompletableFuture
  // Executor
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
