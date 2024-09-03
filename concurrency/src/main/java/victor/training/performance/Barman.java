package victor.training.performance;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import victor.training.performance.drinks.Beer;
import victor.training.performance.drinks.DillyDilly;
import victor.training.performance.drinks.Vodka;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static java.lang.System.currentTimeMillis;

@Slf4j
@RestController
public class Barman {
  @Autowired
  private RestTemplate rest;
  /// guice:
  /// @Inject
//    ExecutorService pool;
  @Autowired
  ThreadPoolTaskExecutor poolBar;

  @GetMapping("/drink")
  public DillyDilly drink() throws ExecutionException, InterruptedException {
    long t0 = currentTimeMillis();

    MDC.put("tenantId", "tenant-" + new Random().nextInt(100));
    log.info("Start");
    // #1 it is inefficient to spawn 2 threads to keep them blocked for the API call duration
    // usa an async client: AsyncRestTemplate, WebClient (reactor)
    // to call network without blocking any thread for the call duration

    // #2 I forgot the close the pool
    Future<Beer> futureBeer = poolBar.submit(() -> beer());
    Future<Vodka> futureVodka = poolBar.submit(() -> rest.getForObject("http://localhost:9999/vodka", Vodka.class));

    Beer beer = futureBeer.get(); // 1s blocks Tomcat's thread (1/200, 0.5%) = bad.
    // BAD iff your system is under heavy fire (10k rps)
    // if you measure!!! a problem: waiting time for tomcat threads, memory usage
    // then try non-blocking concurrency:
    // a) CompletableFuture < today this
    // b) WebFlux (Flux/Mono)/ Observable (rxJava). not just using webClient
    //   to stay away from reactive hell,
    //   use webClient.toFuture():CompletableFuture
    Vodka vodka = futureVodka.get(); // 0s because the vodka is ready already

    // goal: don't block tomcat thread!!

    DillyDilly dilly = new DillyDilly(beer, vodka);
    log.info("HTTP thread blocked for {} durationMillis", currentTimeMillis() - t0);
    return dilly;
  }

  private Beer beer() {
    log.info("Calling beer");
    return rest.getForObject("http://localhost:9999/beer", Beer.class);
  }
}
