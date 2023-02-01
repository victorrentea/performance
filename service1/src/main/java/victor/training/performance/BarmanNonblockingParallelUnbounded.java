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

@RestController
@Slf4j
public class BarmanNonblockingParallelUnbounded {
  @Autowired
  private RestTemplate rest;

  @GetMapping("/drink/nonblocking/parallel/unbounded")
  public CompletableFuture<DillyDilly> drink() {
    //  🛑 DANGER: accumulates all pending requests in an unbounded in-memory queue,
    // as the blocking call runs on the ForkJoinPool.commonPool by default (no executor argument)
    // ~> submit to a dedicated thread pool
    CompletableFuture<Beer> futureBeer = CompletableFuture.supplyAsync(() ->
            rest.getForObject("http://localhost:9999/beer", Beer.class));
    CompletableFuture<Vodka> futureVodka = CompletableFuture.supplyAsync(() ->
            rest.getForObject("http://localhost:9999/vodka", Vodka.class));
    return futureBeer.thenCombine(futureVodka, DillyDilly::new);
  }
}


