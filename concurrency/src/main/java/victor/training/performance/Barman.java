package victor.training.performance;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import victor.training.performance.drinks.Beer;
import victor.training.performance.drinks.DillyDilly;
import victor.training.performance.drinks.Vodka;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.lang.System.currentTimeMillis;
import static java.util.concurrent.CompletableFuture.*;

@Slf4j
@RestController
public class Barman {
  @Autowired
  private RestTemplate rest;

  @GetMapping("/drink")
  public DillyDilly drink() throws ExecutionException, InterruptedException {
    long t0 = currentTimeMillis();
    CompletableFuture<Beer> futureBeer = supplyAsync(() -> fetchBeer());
    CompletableFuture<Vodka> futureVodka = supplyAsync(() -> fetchVodka());
    var beer = futureBeer.get();
    var vodka = futureVodka.get();
    DillyDilly dilly = new DillyDilly(beer, vodka);
    log.info("HTTP thread blocked for {} durationMillis", currentTimeMillis() - t0);
    return dilly;
  }

  private Vodka fetchVodka() {
    return rest.getForObject("http://localhost:9999/vodka", Vodka.class);
  }

  private Beer fetchBeer() {
    return rest.getForObject("http://localhost:9999/beer", Beer.class);
  }
}
