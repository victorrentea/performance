package victor.training.performance;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import victor.training.performance.drinks.Beer;
import victor.training.performance.drinks.DillyDilly;
import victor.training.performance.drinks.Vodka;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.System.currentTimeMillis;

@Slf4j
@RestController
public class Barman {
  @Autowired
  private RestTemplate rest;
  @Autowired
  private WebClient webClient;

  // http://localhost:8080/drink
  @GetMapping("/drink")
  public DillyDilly drink() throws ExecutionException, InterruptedException {
    long t0 = currentTimeMillis();

    ExecutorService executor = Executors.newFixedThreadPool(2);
    var futureBeer = executor.submit(() -> fetchBeer());
    var futureVodka = executor.submit(() -> fetchVodka());
    Beer beer = futureBeer.get();
    Vodka vodka = futureVodka.get();
    // #1 vulnerabil la spike
    // #2 Thread/Mem Leak (n-ai facut shut down)

    DillyDilly dilly = new DillyDilly(beer, vodka);

    log.info("HTTP thread blocked for {}ms", currentTimeMillis() - t0);
    return dilly;
  }

  private Vodka fetchVodka() {
    return rest.getForObject("http://localhost:9999/vodka", Vodka.class);
  }

  private Beer fetchBeer() {
    return rest.getForObject("http://localhost:9999/beer", Beer.class);
  }
}
