package victor.training.performance;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
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
  private ThreadPoolTaskExecutor barPool; // implem Springului de ThreadPool care
  // trebuie mereu folsita in loc de ThreadPoolExecutor din JDK daca esti pe Spring

//  private static final ThreadPoolExecutor barPool =
//      new ThreadPoolExecutor(2, 2,
//          1, TimeUnit.SECONDS,
//          new ArrayBlockingQueue<>(600));

  @GetMapping("/drink")
  public DillyDilly drink() throws ExecutionException, InterruptedException {
    long t0 = currentTimeMillis();

    // codul asta face un thread leak: dupa fiecare apel raman pornite pe vecie 2 thread-uri, care NU se inchid la finalul request-ului
    //  🛑 independent tasks executed sequentially

    // promise (altii) === CompletableFuture (java)
    // $http.get().then( (response) => { ... } ).then( (response) => { ... } ).catch( (error) => { ... } )

    // 1) nu se mai propaga TraceID ca inainte cand faceam barPool.submit(()->{})
    // 2) acum taskul meu ruleaza pe ForkJoinPool.commonPool() laolalta cu oricine altcineva din acest JVM
    //    face CompletableFuture.supplyAsync sau .parallelStream() -> voi concura cu el la cele N-1 threaduri din ForkJoinPool.commonPool()
    //    poate sa duca la Thread Starvation
    Future<Beer> beerFuture = CompletableFuture.supplyAsync(() -> fetchBeer1s()); // non-blocking call, just starts the task
    Future<Vodka> vodkaFuture = CompletableFuture.supplyAsync(() -> fetchVodka1s()); // non-blocking call

    Beer beer = beerFuture.get(); // blocking call for 1 sec
    Vodka vodka = vodkaFuture.get(); // 0 waiting time aici pentru ca deja s-a terminat task-ul

    DillyDilly dilly = new DillyDilly(beer, vodka);

    long t1 = currentTimeMillis();
    log.info("HTTP thread blocked for millis: " + (t1 - t0));
    // acum threadul lui Tomcat este blocat pentru 1 secunda la linia 40
    // - si ce daca?
    // exista sisteme supuse unui load infernal (nu tipic in banci) 10000/s, 500/s
    // in astfel de sisteme nu-ti permiti sa tii blocat threadul Tomcat.
    // ==> non-blocking concurrency (CompletableFuture, RxJava, Reactor)
    return dilly;
  }

  private Vodka fetchVodka1s() {
    return rest.getForObject("http://localhost:9999/vodka", Vodka.class);
  }

  private Beer fetchBeer1s() {
    log.info("Cer berea");
    return rest.getForObject("http://localhost:9999/beer", Beer.class);
  }
}
