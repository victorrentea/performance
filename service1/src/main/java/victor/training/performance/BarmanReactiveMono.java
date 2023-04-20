package victor.training.performance;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import victor.training.performance.drinks.Beer;
import victor.training.performance.drinks.DillyDilly;
import victor.training.performance.drinks.Vodka;

@RestController
@Slf4j
public class BarmanReactiveMono {
  @Autowired
  private WebClient webClient;

  @GetMapping("/drink/mono")
  public Mono<DillyDilly> drink() {

    Mono<Beer> beerPromise = pourBeer();
    Mono<Vodka> vodkaPromise = pourVodka();

    return beerPromise
            .zipWith(vodkaPromise, DillyDilly::new);
  }

  private Mono<Vodka> pourVodka() {
    return webClient.get() // Non-blocking HTTP requests
            .uri("http://localhost:9999/vodka")
            .retrieve()
            .bodyToMono(Vodka.class);
  }

  private Mono<Beer> pourBeer() {
    return webClient.get()
            .uri("http://localhost:9999/beer")
            .retrieve()
            .bodyToMono(Beer.class);
  }

}


