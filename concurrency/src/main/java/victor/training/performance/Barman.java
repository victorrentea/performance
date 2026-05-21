package victor.training.performance;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import victor.training.performance.drinks.Beer;
import victor.training.performance.drinks.DillyDilly;
import victor.training.performance.drinks.Vodka;
import victor.training.performance.util.PerformanceUtil;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.lang.System.currentTimeMillis;

@Slf4j
@RestController
public class Barman {
  @Autowired
  private RestTemplate rest;
  @Autowired
  private WebClient webClient;
  @Autowired
  private ThreadPoolTaskExecutor poolBar;
  @Autowired
  private Hellllper hellllper;

  // http://localhost:8080/drink
  @GetMapping("/drink")
  public DillyDilly drink() throws ExecutionException, InterruptedException {
    long t0 = currentTimeMillis();


    Beer beer = null;
    try {
      beer = rest.getForObject("http://localhost:9999/beer", Beer.class);
    } catch (Exception e) { // "swallowing exceptions"
      // TODO
//      e.printStackTrace(); // system.err (usually not capture)
    }

    CompletableFuture<Vodka> vodka = CompletableFuture.supplyAsync(
        () -> rest.getForObject("http://localhost:9999/vodka", Vodka.class),
        poolBar);

    DillyDilly dilly = new DillyDilly(beer, vodka.get());


//    new Thread(() -> longerWork()).start(); // its stack takes: ~1MB, ☢️DOS if too many threads
//    Executors.newVirtualThreadPerTaskExecutor().submit(() -> longerWork()); // its stack takes: ~1KB, ☢️too much concurrency
//    executorManagedBySpring.submit(()->longerWork());✅
//    CompletableFuture.runAsync(() -> longerWork()/*,executorManagedBySpring*/)
//        .exceptionally(e -> { // ≈ catch; CompletableFutures have no future - @venkat_s
//          log.error("Error in longerWork", e);
//          return null;
//        })
    ;// ✅
    hellllper.longerWork();
    log.info("HTTP thread blocked for {}ms", currentTimeMillis() - t0);
    return dilly;
  }
}

@Slf4j
@RequiredArgsConstructor
@Service
class Hellllper {
  @Async("poolBar") // AOP does not work for local calls (within the same class)
  public void longerWork() { // fire-and-forget this!
    log.info("start");
    PerformanceUtil.sleepMillis(3000);
    if (true) throw new RuntimeException("BUG🐞");
    log.info("end");
  }
}
