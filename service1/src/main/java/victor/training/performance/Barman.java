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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.lang.System.currentTimeMillis;

@RestController
@Slf4j
public class Barman {
  @Autowired
  private RestTemplate rest;
  @Autowired
  ThreadPoolTaskExecutor barPool;
  // beneficii: 1) poate fi configurat in framework cu proprietati de config
  // 2) propaga TraceID
  // 3) shutdown automat la oprirea app

  @GetMapping("/drink")
  public DillyDilly drink() throws ExecutionException, InterruptedException {
    long t0 = currentTimeMillis();

    //  🛑 independent tasks executed sequentially ~> parallelize
//    ExecutorService threadPool = Executors.newFixedThreadPool(2);

    Future<Beer> futureBeer = barPool.submit(() -> fetchBeer());
    Future<Vodka> futureVodka = barPool.submit(() -> fetchVodka());

    Beer beer = futureBeer.get(); // blochez threadul Tomcat 1 sec
    Vodka vodka = futureVodka.get(); // blochez threadul Tomcat 0 sec, ca deja e gata vodka cand berea e turnata

//    threadPool.shutdown();
    // acum ai max 200 + 400 th total pornite (memory issue)

    long t1 = currentTimeMillis();
    log.info("HTTP thread blocked for millis: " + (t1 - t0));
    return new DillyDilly(beer,vodka);
  }

  private Vodka fetchVodka() {
    log.info("Pouring vodka...");
    return rest.getForObject("http://localhost:9999/vodka", Vodka.class);
  }

  private Beer fetchBeer() {
    log.info("Pouring beer...");
    return rest.getForObject("http://localhost:9999/beer", Beer.class);
  }
}
