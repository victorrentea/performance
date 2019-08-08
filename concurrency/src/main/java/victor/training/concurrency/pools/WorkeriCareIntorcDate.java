package victor.training.concurrency.pools;

import java.util.concurrent.*;

import static victor.training.concurrency.ConcurrencyUtil.log;
import static victor.training.concurrency.ConcurrencyUtil.sleep2;

public class WorkeriCareIntorcDate {

    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {

        ExecutorService executor = Executors.newFixedThreadPool(2);

        Future<Void> viitorNimic = executor.submit(() -> Barman.injura("^%!#^%@!^#!%^!#"));
        Future<Beer> futureBeer = executor.submit(() -> Barman.pourBeer());

        Future<Vodka> futureVodka = executor.submit(() -> Barman.pourVodka());


        log("A plecat fata cu comanda mea");
        Beer beer = futureBeer.get();
        Vodka vodka = futureVodka.get(10, TimeUnit.SECONDS);
        log("My drinks : " + beer + vodka);


        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.SECONDS);
        viitorNimic.get();
        log("Plec linistit acasa");
    }

    static class Barman {
        public static Beer pourBeer() {
            log("Torn bere");
            sleep2(1000);
            return new Beer();
        }

        public static Vodka pourVodka() {
            log("Torn vodka");
            sleep2(1000);
            return new Vodka();
        }

        public static Void injura(String injuratura) {
            if (injuratura != null) {
                log("Il omor!");
                throw new IllegalArgumentException("Iti fac buzunar!");
            }
            return null;
        }
    }
}

class Beer {}
class Vodka {}
