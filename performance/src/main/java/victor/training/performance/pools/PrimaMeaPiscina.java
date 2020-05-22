package victor.training.performance.pools;

import org.jooq.lambda.Unchecked;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class PrimaMeaPiscina {

    public static void main(String[] args) {
        BarmanJavaSE barman = new BarmanJavaSE();
        String comenzi ="b,b,v,v,v,b,b";

        // 90+% din cazuri, asta folosim: fixed.
//        ExecutorService pool = Executors.newFixedThreadPool(2); // posibil sa ai intarzieri mari pentru ca poti sta in coada (infinita) foarte mult timp

//        ExecutorService pool = Executors.newCachedThreadPool(); // riscant pentru ca poti da jos JVM/OS daca vine un spike de 10K de requesturi

        comenzi = IntStream.range(0,100).mapToObj(i->"b").collect(joining(","));

        ExecutorService pool = new ThreadPoolExecutor(
                20, 20, // best practice: max = core size
                1, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(80),
                new ThreadPoolExecutor.CallerRunsPolicy());

        List<Future<? extends Drink>> futureDrinks = new ArrayList<>();

        int i = 0;
        long t0 = System.currentTimeMillis();
        for (String comanda : comenzi.split(",")) {
            System.out.println("Submit " + ++i);
            if (comanda.equals("b")) {
                Future<Beer> futureBeer = pool.submit(barman::pourBeer);
                futureDrinks.add(futureBeer);
//                futureBeer.get();
            } else {
                Future<Vodka> futureVodka = pool.submit(barman::pourVodka);
                futureDrinks.add(futureVodka);
            }
        }
        // pana aici ai plasat comenzile

        List<? extends Drink> drinks = futureDrinks.stream()
                .map(Unchecked.function(Future::get)) // .get blocheaza threadul main pana e gata respectivul future
                .collect(toList());

        System.out.println("Beu: " + drinks);

        long t1 = System.currentTimeMillis();

        System.out.println("Took " + (t1-t0));
    }
}
