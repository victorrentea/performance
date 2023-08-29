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

import java.util.concurrent.*;

import static java.lang.System.currentTimeMillis;

@RestController
@Slf4j
public class Barman {
  @Autowired
  private RestTemplate rest;

//  private static final ExecutorService threadPool = Executors.newFixedThreadPool(2);
  @Autowired
  private ThreadPoolTaskExecutor barPool;

  @GetMapping("/drink")
  public DillyDilly drink() throws ExecutionException, InterruptedException {
    long t0 = currentTimeMillis();

    //  🛑 independent tasks executed sequentially ~> parallelize

    // 0) new Thread < periculos OOME

    // 1) Thread Pooluri JavaSE
//    private static final ExecutorService threadPool = Executors.newFixedThreadPool(2);
//    Future<Beer> futureBeer = threadPool.submit(() -> rest.getForObject("http://localhost:9999/beer", Beer.class));

    // 2) Spring ThreadPoolTaskExecutor
      Future<Beer> futureBeer = barPool.submit(() -> fetchBeer());
    // 3) CompletableFuture

//    Beer beer = rest.getForObject("http://localhost:9999/beer", Beer.class);
    Vodka vodka = rest.getForObject("http://localhost:9999/vodka", Vodka.class);
    Beer beer = futureBeer.get();


    long t1 = currentTimeMillis();
    log.info("HTTP thread blocked for millis: " + (t1 - t0));
    return new DillyDilly(beer,vodka);
  }

  private Beer fetchBeer() {
    log.info("Cer bere!");
    return rest.getForObject("http://localhost:9999/beer", Beer.class);
  }
}
