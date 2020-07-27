package victor.training.performance.pools.exercise;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import victor.training.performance.ConcurrencyUtil;

import java.util.concurrent.*;

@Slf4j
public class LaBar {
   //   private static final Logger log = LoggerFactory.getLogger(LaLigheane.class);

   public static void main(String[] args) throws ExecutionException, InterruptedException {
     new LaBar().mergi();
   }

   private BarMan barMan = new BarMan();

   public void mergi() throws ExecutionException, InterruptedException {
      log.debug("Trimit comanda");
      // cele doua apeluri nu depind unul de altul

      ExecutorService pool = Executors.newSingleThreadExecutor();

      Future<Bere> futureBeer = pool.submit(new Callable<Bere>() {
         @Override
         public Bere call() throws Exception {
            return barMan.toarnaBere();
         }
      });

      // mai jos, main-ul blocheaza unul dintre core-uri intergral (100% cpu usage pe 1 core)
      // multithreadingul de multe ori te faci sa cauti alt job (in paralel) - ca vrei tu sau sefu
      while (!futureBeer.isDone()) { // buzy waiting- o forma de polling -pe care nu vrei sa o faci niciodata
//         ConcurrencyUtil.sleep2(1);
         log.debug("Mai dureaza?");
      }
      Bere bere = futureBeer.get();

      pool.submit(new Callable<Vodka>() {
         @Override
         public Vodka call() throws Exception {
            return barMan.toarnaVodka();
         }
      });

      log.debug("Am trimis comanda");
      log.debug("Savurez " + bere + " cu " /*+ vodka*/);
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
