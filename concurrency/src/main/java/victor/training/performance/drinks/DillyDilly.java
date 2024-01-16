package victor.training.performance.drinks;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value
public class DillyDilly {
  Beer beer;
  Vodka vodka;

  public DillyDilly(Beer beer, Vodka vodka) {
    if (beer.getType().equals("DARK")) {
      throw new IllegalArgumentException();
    }
    this.beer = beer;
    this.vodka = vodka;
  }
}
