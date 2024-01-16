package victor.training.performance;

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
  private ThreadPoolTaskExecutor barPool; // implem Springului de ThreadPool care
  // trebuie mereu folsita in loc de ThreadPoolExecutor din JDK daca esti pe Spring

//  private static final ThreadPoolExecutor barPool =
//      new ThreadPoolExecutor(2, 2,
//          1, TimeUnit.SECONDS,
//          new ArrayBlockingQueue<>(600));

  @GetMapping("/drink")
  public CompletableFuture<DillyDilly> drink() throws ExecutionException, InterruptedException {
    long t0 = currentTimeMillis();
    // codul asta face un thread leak: dupa fiecare apel raman pornite pe vecie 2 thread-uri, care NU se inchid la finalul request-ului
    //  🛑 independent tasks executed sequentially

    // promise (altii) === CompletableFuture (java)
    // fetch().then() React
    // $http.get().then( (response) => { ... } ).then( (response) => { ... } ).catch( (error) => { ... } ) Angular

    // 1) nu se mai propaga TraceID ca inainte cand faceam barPool.submit(()->{})
    // 2) acum taskul meu ruleaza pe ForkJoinPool.commonPool() laolalta cu oricine altcineva din acest JVM
    //    face CompletableFuture.supplyAsync sau .parallelStream() -> voi concura cu el la cele N-1 threaduri din ForkJoinPool.commonPool()
    //    poate sa duca la Thread Starvation

    // Spring iti da aici imediat inapoi un CF pe care il va termina cand si functia termina executia si intoarce
    CompletableFuture<Beer> beerPromise = altaClasa.fetchBeer1s(); // non-blocking call, just starts the task

    CompletableFuture<Vodka> vodkaPromise = CompletableFuture.supplyAsync(() -> fetchVodka1s(), barPool); // non-blocking call

    CompletableFuture<DillyDilly> dillyPromise = beerPromise.thenCombine(vodkaPromise,
        (beer, vodka) -> new DillyDilly(beer, vodka));

    //Fire-and-Forget Pattern:eg process uploaded files, audit, send emails
    altaClasa.processUploadedFile("import.csv");
//    CompletableFuture.runAsync(()-> doStuff)

    long t1 = currentTimeMillis();
    log.info("HTTP thread blocked for millis: " + (t1 - t0));
    // acum threadul lui Tomcat este blocat pentru 1 secunda la linia 40
    // - si ce daca?
    // exista sisteme supuse unui load infernal (nu tipic in banci) 10000/s, 500/s
    // in astfel de sisteme nu-ti permiti sa tii blocat threadul Tomcat.
    // ==> non-blocking concurrency (CompletableFuture, RxJava, Reactor)
    return dillyPromise;
  }

  @Autowired
  AltaClasa altaClasa;

  private Vodka fetchVodka1s() {
    return rest.getForObject("http://localhost:9999/vodka", Vodka.class);
  }


}


@Slf4j
@Service
class AltaClasa {
  @Autowired
  private RestTemplate rest;

  @Async //lanseaza metoda in alt thread! DAR! TZEAPA: Adnotarea nu merge pentru ca:
  // logheaza automat orice exceptie daca functia @Async intoarce VOID
  public void processUploadedFile(String fileName) {
    log.info("Start processing file");
    PerformanceUtil.sleepMillis(3000);
    if (true) throw new IllegalArgumentException("Oups!");
    log.info("DONE");
  }

  @Async
  public CompletableFuture<Beer> fetchBeer1s() {
    log.info("Cer berea");
    Beer beer = rest.getForObject("http://localhost:9999/beer", Beer.class);
    return CompletableFuture.completedFuture(beer);
  }
}