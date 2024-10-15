package victor.training.performance;

import lombok.SneakyThrows;
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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.lang.System.currentTimeMillis;
import static java.util.concurrent.CompletableFuture.supplyAsync;

@Slf4j
@RestController
public class Barman {
  @Autowired
  private RestTemplate rest;
  @Autowired
  private ThreadPoolTaskExecutor poolBar;

  @GetMapping("/drink")
  public DillyDilly drink() throws ExecutionException, InterruptedException {
    long t0 = currentTimeMillis();
    MDC.put("user","gigi"); // afisat %X{user} de LOG_PATTERN
    // am infipt metadate in threadul curent. si alte tehnici folosesc tot thread local:
    // 1) @Secured/@RolesAllowed/@PreAuthorized
    // 2) @Transactional - conexiunea curenta e tinuta pe Thread automat,
    // 3) TraceID / CorrelationID
    // OBLIGATORIU folosesti doar threaduri din ThreadPools manageuite de Spring,
    // ca sa nu pierzi cele de mai sus, si ca sa poti dimensiona usor din config nr de threaduri
    CompletableFuture<Vodka> futureVodka = supplyAsync(() -> fetchVodka(), poolBar);
    var beer = fetchBeer(); // 1sec sta HTTP thread aici
    var vodka = futureVodka.get(); //0 sec ca deja e gata vodka
    DillyDilly dilly = new DillyDilly(beer, vodka);
//    CompletableFuture.runAsync(() -> audit(dilly));// interzis in Spring
    altaClasa.audit(dilly);
    log.info("HTTP thread blocked for {} durationMillis", currentTimeMillis() - t0);
    return dilly;
  }
  @Autowired
  private AltaClasa altaClasa; // spring va injecta un proxy aici
  private Vodka fetchVodka() {
    log.info("fetching vodka");
    return rest.getForObject("http://localhost:9999/vodka", Vodka.class);
  }
  private Beer fetchBeer() {
    log.info("fetching beer in my thread");
    if (true) throw new RuntimeException("VALUE nu mai e bere");
    return rest.getForObject("http://localhost:9999/beer", Beer.class);
  }

  @GetMapping("drink-nonblocking")
  public CompletableFuture<DillyDilly> drink2() {
    MDC.put("user","bibi");
    CompletableFuture<Beer> futureBeer = supplyAsync(this::fetchBeer, poolBar)
        .exceptionally(e-> new Beer().setType("ceai"));
    CompletableFuture<Vodka> futureVodka = supplyAsync(this::fetchVodka, poolBar);
    return futureBeer.thenCombine(futureVodka, DillyDilly::new);
  }
}
@Slf4j
@Service
class AltaClasa {
  @Async // Spring's AOP proxy
  // nu merge inca pt ca apelul se face IN ACEEASI CLASA!
  // idem pt: @Transactional, @Secured, @RolesAllowed, @PreAuthorized,
  // @PostAuthorize, @Cacheable, @CacheEvict, @RateLimiter,
  // @Retryable, @CircuitBreaker
  @SneakyThrows
  public void audit(DillyDilly dilly) {
    log.info("start audit");
    Thread.sleep(1000); // pretend network
    if (true) throw new RuntimeException("Sh*t happens");
    log.info("end audit");
  }
}