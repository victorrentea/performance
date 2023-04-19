package victor.training.performance;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import victor.training.performance.drinks.Beer;
import victor.training.performance.drinks.DillyDilly;
import victor.training.performance.drinks.Vodka;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.lang.System.currentTimeMillis;
import static java.util.concurrent.CompletableFuture.supplyAsync;

@RestController
@Slf4j
public class BarmanReactive {
  @Autowired
  private WebClient webClient;

  @GetMapping("/drink")
  public CompletableFuture<DillyDilly> drink() throws ExecutionException, InterruptedException {
    long t0 = currentTimeMillis();

    CompletableFuture<Beer> beerPromise = pourBeer();
    CompletableFuture<Vodka> vodkaPromise = pourVodka();

    CompletableFuture<DillyDilly> dillyPromise = beerPromise
            .thenCombine(vodkaPromise, DillyDilly::new);
    long t1 = currentTimeMillis();
    log.info("HTTP thread usedx for millis: " + (t1 - t0));
    return dillyPromise;
  }

  private CompletableFuture<Vodka> pourVodka() {
    log.info("Requesting vodka... ");
    return webClient.get() // Non-blocking HTTP requests
            .uri("http://localhost:9999/vodka")
            .retrieve()
            .bodyToMono(Vodka.class)
            .toFuture();
  }

  private CompletableFuture<Beer> pourBeer() {
    return webClient.get()
            .uri("http://localhost:9999/beer")
            .retrieve()
            .bodyToMono(Beer.class)
            .toFuture();
  }

}


