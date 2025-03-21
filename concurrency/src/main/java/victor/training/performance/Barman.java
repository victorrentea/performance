package victor.training.performance;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
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
import victor.training.performance.util.PerformanceUtil;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.lang.System.currentTimeMillis;

@Slf4j
@RestController
public class Barman {
  @Autowired
  private RestTemplate rest;
//  ExecutorService threadPool = Executors.newFixedThreadPool(200);
  @Autowired
  private ThreadPoolTaskExecutor threadPool;
  @Autowired
  private SomeOtherClass someOtherClass;

  @GetMapping("/drink")
  public DillyDilly drink() throws ExecutionException, InterruptedException {
    MDC.put("USERID","jdoe"+new Random().nextInt(100));
    log.info("START");
    long t0 = currentTimeMillis();

    // #3 traceIds are not propagated from Tomcat's thread to worker thread
    // fork some work in a second thread, but I need its results
//    Future<Beer> futureBeer = threadPool.submit(() -> getBeer());
//    Future<Beer> futureBeer = CompletableFuture.supplyAsync(() -> getBeer());
    CompletableFuture<Beer> futureBeer = CompletableFuture.supplyAsync(() -> getBeer())
        .thenApplyAsync(beer -> beer.setType("ginger"));
    Vodka vodka = getVodka();
    Beer beer = futureBeer.get();
    beer.setType("ginger");


    // CR: you need to publish a kafka message for audit purposes
    // fire-and-forget
//    threadPool.submit(()->audit("dilly"));
    someOtherClass.audit("dilly");
    // Risks:
    // - audit() not called at all because of a JVM crash/kill
    // - exceptions might not be logged

    DillyDilly dilly = new DillyDilly(beer, vodka);
    log.info("HTTP thread blocked for {} durationMillis", currentTimeMillis() - t0);
    return dilly;
  }



  @GetMapping("/drink-nonblocking")
  public CompletableFuture<DillyDilly> drinkNonBlocking() throws ExecutionException, InterruptedException {
    CompletableFuture<Beer> futureBeer = CompletableFuture.supplyAsync(() -> getBeer());
    CompletableFuture<Vodka> futureVodka = getVodkaNonBLocking();
    return futureBeer.thenCombine(futureVodka, DillyDilly::new);
  }


  @GetMapping("/drink-nonblocking")
  public void drinkNonBlockingServlet(HttpServletRequest request) throws ExecutionException, InterruptedException {
    AsyncContext asyncContext = request.startAsync();
    CompletableFuture<Beer> futureBeer = CompletableFuture.supplyAsync(() -> getBeer());
    CompletableFuture<Vodka> futureVodka = getVodkaNonBLocking();
    // this is Servlet 3.0 10+ years ago: async servlet JEP
//    futureBeer.thenCombine(futureVodka, DillyDilly::new)
//        .thenAccept(dilly -> {
//          asyncContext.getResponse().getWriter().write(dilly + "");
//          asyncContext.complete(); // now the conn is closed.
//        });
  }


  private CompletableFuture<Vodka> getVodkaNonBLocking() {
    return CompletableFuture.completedFuture(getVodka());
  }


  private Vodka getVodka() {
    return rest.getForObject("http://localhost:9999/vodka", Vodka.class);
  }

  private Beer getBeer() {
    log.info("beggign for beer");
    return rest.getForObject("http://localhost:9999/beer", Beer.class);
  }
}

@Slf4j
@RequiredArgsConstructor
@Service
class SomeOtherClass {
  @Async("threadPool") // magic but useful
  public void audit(String dilly) {
    log.info("Sending a message to Kafka for analytics");
    PerformanceUtil.sleepMillis(100); // delays
    if (Math.random()<.5) throw new RuntimeException("Kafka is down"); // errors
    log.info("finished sending");
  }
}