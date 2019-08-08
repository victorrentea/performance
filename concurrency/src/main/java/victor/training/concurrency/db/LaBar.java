package victor.training.concurrency.db;

import victor.training.concurrency.ConcurrencyUtil;

import java.util.concurrent.*;

import static victor.training.concurrency.ConcurrencyUtil.log;
import static victor.training.concurrency.ConcurrencyUtil.sleep2;

public class LaBar {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        log("Intru la bar");

//        Thread.currentThread().setu

        Future<Beer> futureBeer = pool.submit(Barman::pourBeer);
        Future<Vodka> futureVodka = pool.submit(Barman::pourVodka);
        log("A plecat chelnerita cu comanda");

        Beer beer = futureBeer.get();
        Vodka vodka = futureVodka.get();

        log("Savurez: " + beer + " si " + vodka);

        Future<Void> futureBataie = pool.submit(() -> Barman.injural("!$#@!#@!$!"));
        futureBataie.get();
        log("Ies de la bar");
        pool.shutdown();
        pool.awaitTermination(1, TimeUnit.MINUTES);
    }
}


class Vodka {}
class Beer {}
class Barman {
    static Vodka pourVodka() {
        sleep2(1000);
        log("Torn Vodka");
        return new Vodka();
    }
    static Beer pourBeer() {
        sleep2(1000);
        if (Math.random() > 0.5) {
            log("Butoi gol!");
            throw new IllegalStateException("S-o golit butoiu'");
        }
        log("Torn Bere");
        return new Beer();
    }

    public static Void injural(String injuratura) {
        if (injuratura != null) {
            throw new IllegalArgumentException("Te casez!");
        }
        return null;
    }
}