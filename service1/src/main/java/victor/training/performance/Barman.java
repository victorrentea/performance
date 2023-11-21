package victor.training.performance;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import victor.training.performance.drinks.Beer;
import victor.training.performance.drinks.DillyDilly;
import victor.training.performance.drinks.Vodka;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.lang.System.currentTimeMillis;

@RestController
@Slf4j
public class Barman {
  @Autowired
  private RestTemplate rest;

  @GetMapping("/drink")
  public CompletableFuture<DillyDilly> drink() throws ExecutionException, InterruptedException, TimeoutException {
    long t0 = currentTimeMillis();

    //  🛑 independent tasks executed sequentially ~> parallelize
    // promise === CompletableFuture pt ca extinde Future
    CompletableFuture<Beer> beerPromise = CompletableFuture.supplyAsync(() ->
        rest.getForObject("http://localhost:9999/beer", Beer.class));
    CompletableFuture<Vodka> vodkaPromise = CompletableFuture.supplyAsync(() ->
        rest.getForObject("http://localhost:9999/vodka", Vodka.class));

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
}
