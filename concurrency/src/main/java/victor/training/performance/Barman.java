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

@Slf4j
@RestController
public class Barman {
  @Autowired
  private RestTemplate rest;

  @GetMapping("/drink")
  public DillyDilly drink() throws ExecutionException, InterruptedException {
    long t0 = currentTimeMillis();

    ExecutorService pool = Executors.newCachedThreadPool();
    try {
      // #1 it is inefficient to spawn 2 threads to keep them blocked for the API call duration
      // usa an async client: AsyncRestTemplate, WebClient (reactor)
      // to call network without blocking any thread for the call duration

      // #2 I forgot the close the pool
      Future<Beer> futureBeer = pool.submit(() -> rest.getForObject("http://localhost:9999/beer", Beer.class));
      Future<Vodka> futureVodka = pool.submit(() -> rest.getForObject("http://localhost:9999/vodka", Vodka.class));

      Beer beer = futureBeer.get();
      Vodka vodka = futureVodka.get();
      DillyDilly dilly = new DillyDilly(beer, vodka);
      log.info("HTTP thread blocked for {} durationMillis", currentTimeMillis() - t0);
      return dilly;
    } finally {
      pool.shutdown();
    }

  }
}
