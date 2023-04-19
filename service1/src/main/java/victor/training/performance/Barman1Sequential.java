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

  private static final ExecutorService threadPool = Executors.newFixedThreadPool(2);

  @GetMapping({"/drink/sequential","/drink"})
  public DillyDilly drink() throws ExecutionException, InterruptedException {
    long t0 = currentTimeMillis();

    Future<Beer> futureBeer = threadPool.submit(() -> pourBeer());
    Future<Vodka> futureVodka = threadPool.submit(() -> pourVodka());
    // ordered both
    // this method is invoked in a thred from the server's thread pool (Tomcat's size = 200 default)
    Beer beer = futureBeer.get(); // blocked here? 1s
    Vodka vodka = futureVodka.get(); // blocked here? 0s

    long t1 = currentTimeMillis();

    log.info("HTTP thread blocked for millis: " + (t1 - t0));
    return new DillyDilly(beer,vodka);
  }

  private Vodka pourVodka() {
    log.info("Requesting vodka... ");
    return rest.getForObject("http://localhost:9999/vodka", Vodka.class);
  }

  private Beer pourBeer() {
    return rest.getForObject("http://localhost:9999/beer", Beer.class);
  }
}
