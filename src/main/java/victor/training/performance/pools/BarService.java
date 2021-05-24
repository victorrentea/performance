package victor.training.performance.pools;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import victor.training.performance.pools.drinks.Beer;
import victor.training.performance.pools.drinks.Vodka;

import java.util.List;

import static java.util.Arrays.asList;
import static victor.training.performance.PerformanceUtil.sleepq;

@Component
@Slf4j
public class BarService implements CommandLineRunner {
   @Autowired
   private Barman barman;

   @Autowired
   private MyRequestContext requestContext;

   @Override
   public void run(String... args) throws Exception {
      requestContext.setCurrentUser("jdoe");
      log.debug("" + orderDrinks());
   }

   public List<Object> orderDrinks() {
      log.debug("Submitting my order");
      long t0 = System.currentTimeMillis();
      Beer beer = barman.pourBeer();
      Vodka vodka = barman.pourVodka();
      long t1 = System.currentTimeMillis();
      log.debug("Got my order in {} ms : {}", t1-t0, asList(beer, vodka));
      return null;
   }

}

@Service
@Slf4j
class Barman {
   @Autowired
   private MyRequestContext requestContext;

   public Beer pourBeer() {
      String currentUsername = null; // TODO ThreadLocals... , requestContext.getCurrentUser()
      log.debug("Pouring Beer to " + currentUsername + "...");
      sleepq(1000);
      return new Beer();
   }

   public Vodka pourVodka() {
      log.debug("Pouring Vodka...");
      sleepq(1000);
      return new Vodka();
   }
}
