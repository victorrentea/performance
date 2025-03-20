package victor.training.performance;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import victor.training.performance.drinks.Beer;
import victor.training.performance.drinks.DillyDilly;
import victor.training.performance.drinks.Vodka;
import victor.training.performance.util.PerformanceUtil;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static java.lang.System.currentTimeMillis;

@Slf4j
@RestController
public class Barman {
  @Autowired
  private RestTemplate rest;
//  ExecutorService threadPool = Executors.newFixedThreadPool(200);
  @Autowired
  private ThreadPoolTaskExecutor threadPool;

  @GetMapping("/drink")
  public DillyDilly drink() throws ExecutionException, InterruptedException {
    long t0 = currentTimeMillis();

    // #3 traceIds are not propagated from Tomcat's thread to worker thread
    // fork some work in a second thread, but I need its results
    Future<Beer> futureBeer = threadPool.submit(() -> getBeer());
    Vodka vodka = getVodka();
    Beer beer = futureBeer.get();

    // CR: you need to publish a kafka message for audit purposes
    // fire-and-forget
    threadPool.submit(()->audit("dilly"));
    // Risks:
    // - audit() not called at all because of a JVM crash/kill
    // - exceptions might not be logged

    DillyDilly dilly = new DillyDilly(beer, vodka);
    log.info("HTTP thread blocked for {} durationMillis", currentTimeMillis() - t0);
    return dilly;
  }

  private void audit(String dilly) {
    try {
      log.info("Sending a message to Kafka for analytics");
      PerformanceUtil.sleepMillis(100); // delays
      if (Math.random()<.5) throw new RuntimeException("Kafka is down"); // errors
      log.info("finished sending");
    } catch (RuntimeException e) {
      throw new RuntimeException(e);
    }
  }

  private Vodka getVodka() {
    return rest.getForObject("http://localhost:9999/vodka", Vodka.class);
  }

  private Beer getBeer() {
    return rest.getForObject("http://localhost:9999/beer", Beer.class);
  }
}

@Slf4j
@RequiredArgsConstructor
@Service
class SomeOtherClass {

}