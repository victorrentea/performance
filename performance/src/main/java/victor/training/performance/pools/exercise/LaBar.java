package victor.training.performance.pools.exercise;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import victor.training.performance.ConcurrencyUtil;

import java.util.concurrent.*;

@Slf4j
public class LaBar {
   //   private static final Logger log = LoggerFactory.getLogger(LaLigheane.class);

      // mai jos, main-ul blocheaza unul dintre core-uri intergral (100% cpu usage pe 1 core)
      // multithreadingul de multe ori te faci sa cauti alt job (in paralel) - ca vrei tu sau sefu
//      while (!futureBeer.isDone()) { // buzy waiting- o forma de polling -pe care nu vrei sa o faci niciodata
//         ConcurrencyUtil.sleep2(1);
//         log.debug("Mai dureaza?");
//      }

      // daca totusi trebuie sa afli daca ceva in exteriorul java s-a schimbat
      // dar acel 'sistem' nu-ti ofera un mod de a te notifica cand s-a intamplat (Observer Design Pattern)
      // atunci TREBUIE sa faci polling.
      // call REST care-ti zica ca 'mai intreaba-ma tu daca e gata din cand in cand'

      //  Cum faci polling corect:
      // while(!gata) {sleep(<configurabil>); callREST }
      // nivelul 2: sa persisti in baza un rand cu STATUS=PENDING + id-ul primit din exterior pt polling

   public static void main(String[] args) throws ExecutionException, InterruptedException {
      LaBar bar = new LaBar();
      bar.mergi();
      bar.inchide();
   }

   private void inchide() {
      pool.shutdown();
   }

   private ExecutorService pool = Executors.newFixedThreadPool(2);
   private BarMan barMan = new BarMan();

   // apel HTTP
   public void mergi() throws ExecutionException, InterruptedException {
      log.debug("Trimit comanda");
      // cele doua apeluri nu depind unul de altul


      Future<Bere> futureBeer = pool.submit(new Callable<Bere>() {
         @Override
         public Bere call() throws Exception {
            return barMan.toarnaBere();
         }
      });


      Future<Vodka> futureVodka = pool.submit(new Callable<Vodka>() {
         @Override
         public Vodka call() throws Exception {
            return barMan.toarnaVodka();
         }
      });
      log.debug("astept...");
      Bere bere = futureBeer.get();
      Vodka vodka = futureVodka.get();

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
