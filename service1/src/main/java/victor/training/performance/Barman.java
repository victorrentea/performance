package victor.training.performance;

import brave.http.HttpServerResponse;
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

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.*;

import static java.lang.System.currentTimeMillis;
import static java.util.concurrent.CompletableFuture.supplyAsync;

@RestController
@Slf4j
public class Barman {
  @Autowired
  private RestTemplate rest;

//  private static final ExecutorService threadPool = Executors.newFixedThreadPool(2);
  @Autowired
  private ThreadPoolTaskExecutor barPool;

  @GetMapping("/drink")
  public CompletableFuture<DillyDilly> drink() throws ExecutionException, InterruptedException {
    long t0 = currentTimeMillis();

    //  🛑 independent tasks executed sequentially ~> parallelize

    // 0) new Thread < periculos OOME

    // 1) Thread Pooluri JavaSE
//    private static final ExecutorService threadPool = Executors.newFixedThreadPool(2);
//    Future<Beer> futureBeer = threadPool.submit(() -> rest.getForObject("http://localhost:9999/beer", Beer.class));

      // 2) Spring ThreadPoolTaskExecutor
//      Future<Beer> futureBeer = barPool.submit(() -> fetchBeer());
//      Vodka vodka = rest.getForObject("http://localhost:9999/vodka", Vodka.class); // BLOCK
//      Beer beer = futureBeer.get(); // BLOCK

      // 3) CompletableFuture === promise(JS) - non blocking async (ca sa nu starvezi th poolul Tomcatului)
    // nu ai voie .get()

    // NICIODATA in App Spring sa nu folosesti CompletableFuture.xxxAsync fara sa ii dai executor parametru
    /// (adica sa nu rulezi niciodata pe ForkJoinPool.commonPool
    // - il starvezi (abuzezi) are N-1 th - f putin
    // - pierzi metadatele de pe ThreadLocal-ul original daca nu submitezi pe un Th Pool injectat de Spring
    CompletableFuture<Beer> beerPromise = supplyAsync(() -> fetchBeer(), barPool);
    CompletableFuture<Vodka> vodkaPromise = supplyAsync(() -> rest.getForObject("http://localhost:9999/vodka", Vodka.class)
        ,barPool);

    CompletableFuture<DillyDilly> dillyPromise = beerPromise.thenCombineAsync(vodkaPromise,
        (beer, vodka) -> new DillyDilly(beer, vodka));

    altaClasa.fireAndForget();

    long t1 = currentTimeMillis();
    log.info("HTTP thread blocked for millis: " + (t1 - t0));
    return dillyPromise;
  }

//  public void ceFaceSpringuPeSub(HttpServletRequest r) {
//    AsyncContext asyncContext = r.startAsync();
//    drink().thenAccept(dilly -> {
//      asyncContext.getResponse().getWriter().write(toJson(dilly));
//      asyncContext.complete();
//    });
//  }
  @Autowired
  private AltaClasa altaClasa;

  private Beer fetchBeer() {
    log.info("Cer bere!");
//    if (true) {
//      throw new IllegalArgumentException("NU MAI E BERE");
//    }
    return rest.getForObject("http://localhost:9999/beer", Beer.class);
  }
}

@Service
@Slf4j
class AltaClasa {
  // logeaza automat erorile aparute.
  @Async("barPool") // nu merg proxyurile pe apelurile locale in aceeasi clasa
  @SneakyThrows
  public void fireAndForget() {
    log.info("Start processing a file (takes long)");
    Thread.sleep(2000);
    if (true) throw new IllegalArgumentException("EROARE");
    log.info("Done FILE");
  }
}
