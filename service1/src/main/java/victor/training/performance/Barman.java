package victor.training.performance;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import victor.training.performance.drinks.Beer;
import victor.training.performance.drinks.DillyDilly;
import victor.training.performance.drinks.Vodka;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.lang.System.currentTimeMillis;
import static java.util.concurrent.CompletableFuture.supplyAsync;

@RestController
@Slf4j
@RequiredArgsConstructor
public class Barman {
  private final RestTemplate rest;
  private final ThreadPoolTaskExecutor barPool;

  @GetMapping("/drink")
  //
  public CompletableFuture<DillyDilly> drink() throws ExecutionException, InterruptedException {
    log.info("Start");
    long t0 = currentTimeMillis();

    // promises(JS) === CompletableFuture (Java)
    // Why you should always pass a Spring-injected Executor as the last arg of any CF.xxxxxAsync method
    // 1) we want to use a thread from a thread pool WE control, not from the global FJP (to avoid starving others)
    CompletableFuture<Beer> beerPromise = supplyAsync(this::getBeer, barPool);
    CompletableFuture<Vodka> vodkaPromise = supplyAsync(this::getVodka, barPool);
    // runs in Tomcat's tread (1/200)
//    Beer beer = beerPromise.get(); // on CF it's not recommended to .get()
    // block Tomcat's threads for 1 sec.
    // can lead to Thread Pool Starvation
    // not fair vs another critical flow like
    // POST /place-bet
//    Vodka vodka = vodkaPromise.get();
    CompletableFuture<DillyDilly> dillyPromise = beerPromise
        .thenCombine(vodkaPromise, (beer, vodka) -> new DillyDilly(beer, vodka));

    long t1 = currentTimeMillis();
    log.info("HTTP thread blocked for millis: " + (t1 - t0));
    return dillyPromise; // framework will send the response via a callback added on your CF.
    // without blocking any threads
  }

  private Vodka getVodka() {
    log.info("Where am I? by default in ForkJoinPool.commonPool(size=n-1=9, GLOBAL)");

    return rest.getForObject("http://localhost:9999/vodka", Vodka.class); // blocking code
    // unlike WebClient
    // < Java21 0.5 MB of RAM + 1 OS thread waits blocked here.
  }

  private Beer getBeer() {
    return rest.getForObject("http://localhost:9999/beer", Beer.class);
  }
}
