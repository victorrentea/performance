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

import static java.lang.System.currentTimeMillis;

@Slf4j
@RestController
public class Barman {
  @Autowired
  private RestTemplate restTemplate;

  @GetMapping("/drink")
  public DillyDilly drink() throws ExecutionException, InterruptedException {
    log.info("Start");
    long t0 = currentTimeMillis();

    //  🛑 2 independent tasks executed sequentially. What TODO ?
    // CompletableFuture(java)===promise(JS)
    CompletableFuture<Beer> beerPromise = CompletableFuture.supplyAsync(() ->
        restTemplate.getForObject("http://localhost:9999/beer", Beer.class));
    CompletableFuture<Vodka> vodkaPromise = CompletableFuture.supplyAsync(() ->
        restTemplate.getForObject("http://localhost:9999/vodka", Vodka.class));

    Beer beer = beerPromise.get(); // threadul Tomcat sta aici blocat 1 sec
    Vodka vodka = vodkaPromise.get(); // threadul Tomcat sta aici blocat 0 sec
    // vodka e deja turnata cand am berea in mana. Ca tot 1 sec a durat IN PARALEL CU MINE

    DillyDilly dilly = new DillyDilly(beer, vodka);

    log.info("HTTP thread blocked for millis: " + (currentTimeMillis() - t0));
    return dilly;
  }
}
