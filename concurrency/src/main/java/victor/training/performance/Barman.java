package victor.training.performance;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import victor.training.performance.drinks.Beer;
import victor.training.performance.drinks.DillyDilly;
import victor.training.performance.drinks.Vodka;

import java.util.concurrent.*;

import static java.lang.System.currentTimeMillis;

@Slf4j
@RestController
public class Barman {
  @Autowired
  private RestTemplate rest;
  private static final ThreadPoolExecutor threadPool =
      new ThreadPoolExecutor(2, 2,
          1, TimeUnit.SECONDS,
          new ArrayBlockingQueue<>(600));

  @GetMapping("/drink")
  public DillyDilly drink() throws ExecutionException, InterruptedException {
    long t0 = currentTimeMillis();

    // codul asta face un thread leak: dupa fiecare apel raman pornite pe vecie 2 thread-uri, care NU se inchid la finalul request-ului
    //  🛑 independent tasks executed sequentially
    Future<Beer> beerFuture = threadPool.submit(() ->
        rest.getForObject("http://localhost:9999/beer", Beer.class));
    Future<Vodka> vodkaFuture = threadPool.submit(() ->
        rest.getForObject("http://localhost:9999/vodka", Vodka.class));

    Beer beer = beerFuture.get();
    Vodka vodka = vodkaFuture.get();

    DillyDilly dilly = new DillyDilly(beer, vodka);

    long t1 = currentTimeMillis();
    log.info("HTTP thread blocked for millis: " + (t1 - t0));
    return dilly;
  }
}
