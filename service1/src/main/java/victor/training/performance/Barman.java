package victor.training.performance;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import victor.training.performance.drinks.Beer;
import victor.training.performance.drinks.DillyDilly;
import victor.training.performance.drinks.Vodka;

import static java.lang.System.currentTimeMillis;

@RestController
@Slf4j
public class Barman {
  @Autowired
  private RestTemplate rest;

  @GetMapping("/drink")
  public DillyDilly drink() {
    long t0 = currentTimeMillis();

    //  🛑 independent tasks executed sequentially ~> parallelize
    Beer beer = rest.getForObject("http://localhost:9999/beer", Beer.class);
    Vodka vodka = rest.getForObject("http://localhost:9999/vodka", Vodka.class);

    long t1 = currentTimeMillis();
    log.info("HTTP thread blocked for millis: " + (t1 - t0));
    return new DillyDilly(beer,vodka);
  }
}
