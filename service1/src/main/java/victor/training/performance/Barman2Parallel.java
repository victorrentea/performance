package victor.training.performance;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import victor.training.performance.drinks.Beer;
import victor.training.performance.drinks.DillyDilly;
import victor.training.performance.drinks.Vodka;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static java.lang.System.currentTimeMillis;

@RestController
@Slf4j
public class Barman2Parallel {
  @Autowired
  private RestTemplate rest;
  @Autowired
  private ThreadPoolTaskExecutor barPool;

  @GetMapping("/drink/parallel")
  public DillyDilly drink() throws ExecutionException, InterruptedException {
    long t0 = currentTimeMillis();
    Future<Beer> futureBeer = barPool.submit(() -> rest.getForObject("http://localhost:9999/beer", Beer.class));
    Future<Vodka> futureVodka = barPool.submit(() -> rest.getForObject("http://localhost:9999/vodka", Vodka.class));

    //  🛑 blocking http threads ~> non-blocking calls (AsyncRestTemplate or Reactive)
    Beer beer = futureBeer.get();
    Vodka vodka = futureVodka.get();
    long t1 = currentTimeMillis();
    log.debug("HTTP thread blocked for millis: " + (t1 - t0));
    return new DillyDilly(beer, vodka);
  }

  @GetMapping("buy")
  public void acceptMoneyFromClient() {
    log.debug("This is NOT allowed to timeout!!! because it takes the client's money");
  }


  @Configuration
  public static class BarPoolConfig {
    @Bean
    public ThreadPoolTaskExecutor barPool(MeterRegistry meterRegistry, @Value("${bar.pool.size}") int barPoolSize) {
      ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
      executor.setCorePoolSize(barPoolSize);
      executor.setMaxPoolSize(barPoolSize);
      executor.setQueueCapacity(500);
      executor.setThreadNamePrefix("bar-");
      executor.setTaskDecorator(new MonitorQueueWaitingTime(meterRegistry.timer("barman-queue-time")));
      executor.initialize();
      return executor;
    }
  }

}


