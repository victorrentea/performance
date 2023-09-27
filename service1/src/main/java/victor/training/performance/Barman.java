package victor.training.performance;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
  private RestTemplate rest; // mandatory to call them via a 'decorated' Rest Template/Message Sender so that it's
  // capable to add the headers on the outgoing message/request
  @Autowired
  private ThreadPoolTaskExecutor threadPool; // Spring's tread pool

    // Java Standard, do NOT use this in a Spring app
//    ExecutorService threadPool = Executors.newFixedThreadPool(2);
  @GetMapping("/drink")
  public CompletableFuture<DillyDilly> drink() throws ExecutionException, InterruptedException {
    long t0 = currentTimeMillis();

    // every submit to another thread pool should happen ONBLY to decorated Spring beans.
    // aka Promise in FE
    CompletableFuture<Beer> beerPromise = CompletableFuture.supplyAsync(() -> getMyBeer(), threadPool);
    CompletableFuture<Vodka> vodkaPromise = CompletableFuture.supplyAsync(() -> rest.getForObject("http://localhost:9999/vodka", Vodka.class), threadPool);

//    Beer beer = futureBeer.get(); // BLOCKS a thread from Tomcat. imagine all 200 Tomcat threads blocked at this line
//    // making all other endpoints fail with 503
//    Vodka vodka = futureVodka.get();

    CompletableFuture<DillyDilly> dillyPromise = beerPromise.thenCombine(vodkaPromise, DillyDilly::new);
//    dillyPromise.get() // DON:T DEFEATS THE PURPOSE: BLOCKS TOMCAT THREAD

    long t1 = currentTimeMillis();
    log.info("HTTP thread blocked for millis: " + (t1 - t0));
    return dillyPromise;
  }


  @PostMapping("pay")
  public void acceptPayment() { // 503

  }


  private Beer getMyBeer() {
    log.info("Do you know what TraceID is ? (Open telemetry / Sleuth) ");
    // still hangs a thread (0.5..1 MB RAM=stack) for the duration of the call
    return rest.getForObject("http://localhost:9999/beer", Beer.class);

    // solution: use WebClient......toFuture():CompletableFuture/Mono <- non-blocable REST API CALL
  }
}
