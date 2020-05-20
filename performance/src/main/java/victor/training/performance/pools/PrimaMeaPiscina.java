package victor.training.performance.pools;

import org.jooq.lambda.Unchecked;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class PrimaMeaPiscina {

    public static void main(String[] args) {
        Barman barman = new Barman();
        String comenzi ="b,b,v,v,v,b,b";

        ExecutorService pool = Executors.newFixedThreadPool(3);
        Callable<Beer> pourBeer = barman::pourBeer;
        Callable<Vodka> pourVodka = barman::pourVodka;

        List<Future<? extends Drink>> futureDrinks = new ArrayList<>();

        long t0 = System.currentTimeMillis();
        for (String comanda : comenzi.split(",")) {
            if (comanda.equals("b")) {
                Future<Beer> futureBeer = pool.submit(pourBeer);
                futureDrinks.add(futureBeer);
//                futureBeer.get();
            } else {
                Future<Vodka> futureVodka = pool.submit(pourVodka);
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
