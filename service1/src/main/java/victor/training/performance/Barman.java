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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeoutException;

import static java.lang.System.currentTimeMillis;

@RestController
@Slf4j
public class Barman {
  @Autowired
  private RestTemplate rest;
@Autowired
ThreadPoolTaskExecutor barPool;
  @GetMapping("/drink")
  public CompletableFuture<DillyDilly> drink() throws ExecutionException, InterruptedException, TimeoutException {
    long t0 = currentTimeMillis();

    //  🛑 independent tasks executed sequentially ~> parallelize
    // promise === CompletableFuture pt ca extinde Future
    CompletableFuture<Beer> beerPromise = CompletableFuture.supplyAsync(this::pourBeer, barPool);
    CompletableFuture<Vodka> vodkaPromise = CompletableFuture.supplyAsync(() ->
        rest.getForObject("http://localhost:9999/vodka", Vodka.class), barPool);

    CompletableFuture<DillyDilly> dillyPromise = beerPromise.thenCombine(vodkaPromise,
        (beer, vodka) -> new DillyDilly(beer, vodka));

    long t1 = currentTimeMillis();
    log.info("HTTP thread blocked for millis: " + (t1 - t0));
//    return dillyPromise.get(); // blocheaza indefinit - fragil

//    return dillyPromise.get(10, TimeUnit.SECONDS); // NU e recomandat sa faci completableFuture.get() sau Mono/Flux.block(), pt ca
    // blocheaza threadul Tomcatului

    // ❤️ dar DA! foloseste .get() si .block() in @Test! eventual cu @Timeout pe @Test

    return dillyPromise; // returnez un promise Springului.
    // ce se intampla pe sub? Springu stie sa agate requestul HTTP asincron fara sa blocheze threaduri
  }

  private Beer pourBeer() {
    log.info("pe ce thread pool rulez aici?");
    // ForkJoinPool.commonPool un thread pool global shareuit cu toti din JVM
    // pericolul este:
    // 💣1) sa nu blochezi/starvezi acel threadpool
    // Scenariu: in alta parte faci un .parallelStream() sau tot CF.supplyAsync si dincolo nu mai gassesti threaduri libere
    // pt ca le-am blocat eu pe toate.
    // Starvation = unfair sa tin eu cele size=(NCPU-1) threaduri blocate  obligandu-i sa astepte
    //  NOTA: daca faci doar CPU, dar MULT CPU (taskuri care rup procesorul 1 secunda fiecare)
    //  Ce fac? =>
    //  a) imi fac propriul ForkJoinPool
    // var pool =new ForkJoinPool(10); => OS va forta context-switch intre cele 10 + 9 threaduri
    //  b) imi fragmentz (daca pot) taskurile in piese mai mici

    // 💣2) pierzi thread local data in Spring: ThraceId, SecurityContextHolder, Logback MDC
    // daca te duci pe un thread pool ne-manageuit de Spring

    return rest.getForObject("http://localhost:9999/beer", Beer.class);
  }
}
