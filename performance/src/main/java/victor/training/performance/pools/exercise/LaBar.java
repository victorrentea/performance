package victor.training.performance.pools.exercise;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import victor.training.performance.ConcurrencyUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class LaBar {
   //   private static final Logger log = LoggerFactory.getLogger(LaLigheane.class);

   public static void main(String[] args) {
     new LaBar().mergi();
   }

   private BarMan barMan = new BarMan();

   public void mergi() {
      log.debug("Trimit comanda");
      // cele doua apeluri nu depind unul de altul

      for (int i = 0; i < 10_000; i++) {
         Thread t = new Thread() {
            @Override
            public void run() {
               // INTELIGENT

               // Daca vrei sa vezi de ce te concediaza sefu cand faci new Thread, citeste din cartea Java Concurrency in Practice despre .interrupt.
            }
         };

      }

      Bere bere = barMan.toarnaBere();
      Vodka vodka = barMan.toarnaVodka();
      log.debug("Am trimis comanda");
      log.debug("Savurez " + bere + " cu " + vodka);
   }
}

class BarMan {
   private static final Logger log = LoggerFactory.getLogger(BarMan.class);
   public Bere toarnaBere() {
      log.debug("Torn bere");
      ConcurrencyUtil.sleep2(1000);
      // Dezipuirea unui fisier
      // verificari de semnaturi digitale
      // decriptari
      // SELECT
      // Apel de REST catre un 3rd party
      log.debug("Gata!");
      return new Bere();
   }

   public Vodka toarnaVodka() {
      log.debug("Torn vodka");
      ConcurrencyUtil.sleep2(1000);
      log.debug("Gata!");
      return new Vodka();
   }
}
class Bere {}
class Vodka {}
