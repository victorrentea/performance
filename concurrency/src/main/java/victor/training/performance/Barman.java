package victor.training.performance;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
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
  @Autowired
  private WebClient webClient;

  private static final ExecutorService executor = Executors.newFixedThreadPool(24);

  @PreDestroy
  public void onShutdown() throws InterruptedException {
//    executor.shutdown();
    executor.shutdownNow(); // daca sunt doar geturi running
    executor.awaitTermination(1, TimeUnit.MINUTES); // kill SIGTERM
  }

  // http://localhost:8080/drink
  @GetMapping("/drink")
  public DillyDilly drink() throws ExecutionException, InterruptedException {
    long t0 = currentTimeMillis();

    CompletableFuture<Beer> futureBeer = CompletableFuture.supplyAsync(this::fetchBeer,executor);
    Vodka vodka = fetchVodka(); // tomcat merge dupa vodka
    Beer beer = futureBeer.get(); // threadu nou intre timp dupa bere

    DillyDilly dilly = new DillyDilly(beer, vodka);

    log.info("HTTP thread blocked for {}ms", currentTimeMillis() - t0);
    return dilly;
  }

  private Vodka fetchVodka() {
    return rest.getForObject("http://localhost:9999/vodka", Vodka.class);
  }

  private Beer fetchBeer() {
    return rest.getForObject("http://localhost:9999/beer", Beer.class);
  }
}
