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
  public CompletableFuture<DillyDilly> drink() throws ExecutionException, InterruptedException {
    log.info("Start");
    long t0 = currentTimeMillis();
    //  🛑 2 independent tasks executed sequentially. What TODO ?
    // CompletableFuture(java)===promise(JS)
    CompletableFuture<Beer> beerPromise = CompletableFuture.supplyAsync(() -> {
      log.info("cer BERE");
      return restTemplate.getForObject("http://localhost:9999/beerX", Beer.class);
    }/*, executor*/)
//        .exceptionally(ex -> new Beer().setType("faraCOOH"))
        ;
    CompletableFuture<Vodka> vodkaPromise = CompletableFuture.supplyAsync(() -> {
      log.info("Cer VODKA");
      return restTemplate.getForObject("http://localhost:9999/vodka", Vodka.class);
    }/*, executor*/);
    // DAR e imoral sa faci .get() pe un CompletableFuture. Defeats its purpose
//    Beer beer = beerPromise.get(); // threadul Tomcat sta aici blocat 1 sec
//    Vodka vodka = vodkaPromise.get(); // threadul Tomcat sta aici blocat 0 sec
    // vodka e deja turnata cand am berea in mana. Ca tot 1 sec a durat IN PARALEL CU MINE
    CompletableFuture<DillyDilly> dillyPromise =
        beerPromise.thenCombine(vodkaPromise, (beer, vodka)->new DillyDilly(beer, vodka))
            .exceptionally( ex -> {
              log.info("Oare pe ce thread sunt");
              return null;
            })
        ;
//            .exceptionally(ex -> new DillyDilly(new Beer("No Beer"), new Vodka("No Vodka"))
//            .thenAccept(dillyDilly -> log.);
//    DillyDilly dilly = dillyPromise.get(); // threadul Tomcat sta 1 sec degeaba. 1/200

    log.info("HTTP thread blocked for millis: " + (currentTimeMillis() - t0));
    return dillyPromise;
  }
  // threadul tomcat este eliberat iar spring agata de 'finalizarea' lui dillyPromise
  // un callback care scrie JSONul catre client si inchide apoi conex

  // cand merita asa cod imputzit?
  // dOAR daca ai LOAD NEBUN pe server si vezi ca ai conexiun in asteptare,
  // pentru ca ti s-a starvat thread poolul Tomcatului

  // DAR in java 21 CompletableFuture pt acest use case
  // sunt interzise. Foloseste Virtual Threads.
}
