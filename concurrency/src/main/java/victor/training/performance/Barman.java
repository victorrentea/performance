package victor.training.performance;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import victor.training.performance.drinks.Beer;
import victor.training.performance.drinks.DillyDilly;
import victor.training.performance.drinks.Vodka;

import static java.lang.System.currentTimeMillis;

@Slf4j
@RestController
public class Barman {
  @Autowired
  private RestTemplate rest;
  @Autowired
  private WebClient webClient;

  // http://localhost:8080/drink
  @GetMapping("/drink")
  public DillyDilly drink() {
    long t0 = currentTimeMillis();

    Beer beer = rest.getForObject("http://localhost:9999/beer", Beer.class);

    Vodka vodka = rest.getForObject("http://localhost:9999/vodka", Vodka.class);

    DillyDilly dilly = new DillyDilly(beer, vodka);

    log.info("HTTP thread blocked for {}ms", currentTimeMillis() - t0);
    return dilly;
  }
}
