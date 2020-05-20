package victor.training.performance.pools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PrimaMeaPiscina {

    public static void main(String[] args) {
        Barman barman = new Barman();
        String comenzi ="b,b,v,v,v,b,b";

//        ExecutorService pool = Executors.newFixedThreadPool(3);
        List<Drink> drinks = new ArrayList<>();

        long t0 = System.currentTimeMillis();
        for (String comanda : comenzi.split(",")) {
            if (comanda.equals("b")) {
                drinks.add(barman.pourBeer());
            } else {
                drinks.add(barman.pourVodka());
            }
        }
        long t1 = System.currentTimeMillis();

        System.out.println("Beu: " + drinks);
        System.out.println("Took " + (t1-t0));
    }
}
