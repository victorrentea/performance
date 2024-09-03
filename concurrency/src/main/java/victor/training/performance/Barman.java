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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
  public CompletableFuture<DillyDilly> drink() throws ExecutionException, InterruptedException {
    long t0 = currentTimeMillis();

    MDC.put("tenantId", "tenant-" + new Random().nextInt(100));
    log.info("Start");
    // #1 it is inefficient to spawn 2 threads to keep them blocked for the API call duration
    // usa an async client: AsyncRestTemplate, WebClient (reactor)
    // to call network without blocking any thread for the call duration

    // #2 I forgot the close the pool

    // In Spring you must always submit your tasks to an executor,
    // even if you use completable future (ThreadLocal: MDC, traceId, SecurityContext)
    CompletableFuture<Beer> futureBeer = CompletableFuture.supplyAsync(this::beer);
    CompletableFuture<Vodka> futureVodka = CompletableFuture.supplyAsync(this::getVodka);
    // goal: don't block tomcat thread!!
    CompletableFuture<DillyDilly> futureDilly = futureBeer.thenCombine(futureVodka, DillyDilly::new);

//    DillyDilly dilly = futureDilly.get();
    log.info("HTTP thread blocked for {} durationMillis", currentTimeMillis() - t0);
    return futureDilly;// return to the web framework a promise of the response.
    // the framework is gonna do KungFu not to block any threads while waiting.
    // but keep the network socket open until the response is ready
  }

  private Vodka getVodka() {
    return rest.getForObject("http://localhost:9999/vodka", Vodka.class);
  }

  private Beer beer() {
    log.info("Calling beer");
    return rest.getForObject("http://localhost:9999/beer", Beer.class);
  }
}
