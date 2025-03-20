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
  ExecutorService threadPool = Executors.newFixedThreadPool(200);

  @GetMapping("/drink")
  public DillyDilly drink() throws ExecutionException, InterruptedException {
    long t0 = currentTimeMillis();

    // #3 traceIds are not propagated from Tomcat's thread to worker thread
    Future<Beer> futureBeer = threadPool.submit(() -> getBeer());
    Vodka vodka = getVodka();
    Beer beer = futureBeer.get();

    DillyDilly dilly = new DillyDilly(beer, vodka);
    log.info("HTTP thread blocked for {} durationMillis", currentTimeMillis() - t0);
    return dilly;
  }

  private Vodka getVodka() {
    return rest.getForObject("http://localhost:9999/vodka", Vodka.class);
  }

  private Beer getBeer() {
    return rest.getForObject("http://localhost:9999/beer", Beer.class);
  }
}
