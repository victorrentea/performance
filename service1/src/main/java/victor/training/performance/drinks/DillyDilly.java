package victor.training.performance.drinks;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value
public class DillyDilly {
  Beer beer;
  Vodka vodka;
  {
    log.info("Mixing Dilly (in what thread?)");
  }
}
