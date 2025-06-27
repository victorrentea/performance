package victor.training.performance;

import jakarta.annotation.PreDestroy;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import victor.training.performance.drinks.Beer;
import victor.training.performance.drinks.DillyDilly;
import victor.training.performance.drinks.Vodka;

import java.io.IOException;
import java.util.concurrent.*;

import static java.util.concurrent.CompletableFuture.delayedExecutor;
import static java.util.concurrent.CompletableFuture.supplyAsync;

@Slf4j
@RestController
public class Barman {
  @Autowired
  private RestTemplate rest;
  @Autowired
  private WebClient webClient;

//  @Autowired
//  ThreadPoolTaskExecutor executor;
  private static final ExecutorService executor =
    Executors.newFixedThreadPool(24);

  @PreDestroy
  public void onShutdown() throws InterruptedException {
//    executor.shutdown();
    executor.shutdownNow(); // daca sunt doar geturi running
    executor.awaitTermination(1, TimeUnit.MINUTES); // kill SIGTERM
  }

  // http://localhost:8080/drink
  @GetMapping("/drink")
//  @GET @Path
  public CompletableFuture<DillyDilly> drink() throws ExecutionException, InterruptedException {
    log.info("before");
    try {
      return supplyAsync(this::fetchHarta, executor)
          .thenCombine(supplyAsync(this::fetchVodka, executor),
              DillyDilly::new);
    } finally {
      log.info("after");
    }
  }

  @GetMapping("/drink")
  // de backup in caz ca var de mai sus nu merge
  public void drinkAsync(HttpServletRequest request) throws ExecutionException, InterruptedException {
//    ServerSocket serverSocket
    // intre timp, pana vine jetty, hai sa pornim 2 x httpnano pe 2 porturi diferite din mainu meu ca sa blocheze separat sofia req de MAPS
    // posibile emotii cu CORS.
    var asyncContext = request.startAsync();
    CompletableFuture.runAsync(() -> {
      try {
        asyncContext.getResponse().getWriter().write("salut din viitor");
        asyncContext.complete();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }, delayedExecutor(3, TimeUnit.SECONDS));

    log.info("before");
    try{

    } finally {
      log.info("after");
    }
  }

  private Vodka fetchVodka() {
    return rest.getForObject("http://localhost:9999/vodka", Vodka.class);
  }

  private Beer fetchHarta() {
    return rest.getForObject("http://localhost:9999/beer", Beer.class);
  }
}
