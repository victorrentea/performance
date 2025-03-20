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

    ExecutorService threadPool = Executors.newFixedThreadPool(2); // #2 I create NEW threads every. I don't reuse
    // #3 traceIds are not propagated from Tomcat's thread to worker thread
    Future<Beer> futureBeer = threadPool.submit(() -> getBeer());
    // #4 I waste resources: the tomcat's thread sleeps for 1 second doing nothing wasting memory.
    Future<Vodka> futureVodka = threadPool.submit(() -> getVodka());
    Beer beer = futureBeer.get();
    Vodka vodka = futureVodka.get();

    DillyDilly dilly = new DillyDilly(beer, vodka);
//    threadPool.shutdown(); // #1 - thread leak leading to OOME in 2 days > restart every night💖
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
