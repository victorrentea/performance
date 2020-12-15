package victor.training.performance.pools;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import victor.training.performance.pools.drinks.Beer;
import victor.training.performance.pools.drinks.Vodka;

import static victor.training.performance.ConcurrencyUtil.sleepq;

@Slf4j
@Value
public class DillyDilly {
   Beer beer;
   Vodka vodka;

   public DillyDilly(Beer beer, Vodka vodka) {
      this.beer = beer;
      this.vodka = vodka;
      log.debug("Amestec bere cu vodka!!!");
      sleepq(1000);
   }
}
