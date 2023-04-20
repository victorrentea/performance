package victor.training.performance;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import victor.training.performance.drinks.Beer;
import victor.training.performance.drinks.DillyDilly;
import victor.training.performance.drinks.Vodka;

import java.util.concurrent.*;

import static java.lang.System.currentTimeMillis;
import static java.util.concurrent.CompletableFuture.supplyAsync;

@RestController
@Slf4j
public class Barman1Sequential {
  @Autowired
  private RestTemplate rest;

  //  private static final ExecutorService threadPool = Executors.newFixedThreadPool(2);
  @Autowired
  ThreadPoolTaskExecutor barPool;

  @GetMapping({"/drink/sequential"})
  public CompletableFuture<DillyDilly> drink() throws ExecutionException, InterruptedException {
    long t0 = currentTimeMillis();

    //    CompletableFuture<Beer> beerPromise = CompletableFuture.supplyAsync(() -> pourBeer()); // looses the Spring magic
    CompletableFuture<Beer> beerPromise = supplyAsync(this::pourBeer, barPool)
            .exceptionally(e -> {
              log.error("Error in beer", e);
              return null;
            }); // <- always do this
    CompletableFuture<Vodka> vodkaPromise = supplyAsync(otherService::pourVodka, barPool); // <- always do this

    // ordered both
    // this method is invoked in a thred from the server's thread pool (Tomcat's size = 200 default)
    //    Beer beer = beerPromise.get(); // NEVER BLOCK on CompletableFuture.get() !!!

    CompletableFuture<DillyDilly> dillyPromise = beerPromise.thenCombine(vodkaPromise, DillyDilly::new);

//    CompletableFuture.runAsync(()->otherService.fireAndForget(),barPool);//.exceptionally(e->{log.error("ERROR:"+e);throw new ExEe;});
    fireAndForget();
    long t1 = currentTimeMillis();
    log.info("HTTP thread usedx for millis: " + (t1 - t0));
    return dillyPromise;
  }



  private Beer pourBeer() {
    if (true) throw new IllegalArgumentException("out of beer!");
    return rest.getForObject("http://localhost:9999/beer", Beer.class);
  }

  @Async // pitfall, the annotation is ignored because
  // proxies do not work if you call the method from the same class
  @SneakyThrows
  public void fireAndForget()  {
    log.info("Processing the file uploaded by user, sending emails to 10k email addresses, cleanup, etc.");
    Thread.sleep(3000);
//    if (true) {
//        throw new IllegalArgumentException("BUUM"); // never logged!! OMG
//    }
    log.info("DONE!");
  }
  @Autowired
  private OtherService otherService;
}
@RequiredArgsConstructor
@Service
@Slf4j
class OtherService {
  private final RestTemplate rest;

  private final MeterRegistry meterRegistry;

  @Timed("vodkapour")
  public Vodka pourVodka() {

//    meterRegistry.
    log.info("Requesting vodka... ");
    return rest.getForObject("http://localhost:9999/vodka", Vodka.class);
  }
}


