package victor.training.performance.pools;

import java.util.concurrent.*;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static victor.training.performance.ConcurrencyUtil.log;
import static victor.training.performance.ConcurrencyUtil.sleep2;

public class GalusteFJP {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        measure(() -> treaba());
        sleep2(1000);
    }
    public static void measure(Runnable r) {
        long t0 = System.currentTimeMillis();
        r.run();
        long t1 = System.currentTimeMillis();
        System.out.println("Took " + (t1-t0));
    }

    private static void treaba() {
        log("Start");

        ForkJoinPool pool = new ForkJoinPool(100);

        CompletableFuture<String> futureA = supplyAsync(Galusca1::m, pool);
        CompletableFuture<String> futureAMare = futureA.thenApply(aMic -> Galusca3.m(aMic)); // agati o procesa viitoare .then() [JS]

        CompletableFuture<String> futureB = supplyAsync(Galusca2::m, pool);

        CompletableFuture<String> futureAbc = futureAMare.thenCombine(futureB, (aMare, b) -> Galusca4.m(aMare, b));


        CompletableFuture<String> futureD = supplyAsync(Galusca5::m, pool);
        CompletableFuture<String> futureAbcd = futureAbc
                .thenCombine(futureD, (Abc, d) -> Abc + d)
                .exceptionally(e -> {
                    e.printStackTrace();
                    return "ERROR";
                });

        CompletableFuture<Void> x = futureAbcd
                .thenAccept(Abcd -> log("Gata : " + Abcd));

        x.thenRunAsync(Galusca6::m, pool);
        x.thenRunAsync(Galusca7::m, pool);
        log("Ies");

    }
}

class Galusca1 {
    static String m() {
        sleep2(100);
        log("1");
        return "a";
    }
}
class Galusca2 {
    static String m() {
        sleep2(100);
        log("2");
        return "b";
    }
}
class Galusca3 {
    static String m(String a) {
        sleep2(100);
        log("3");
//        if (true) throw new RuntimeException();
        return a.toUpperCase();
    }
}
class Galusca4 {
    static String m(String A, String b) {
        sleep2(100);
        log("4");
        return A+b+"c";
    }
}
class Galusca5 {
    static String m() {
        sleep2(100);
        log("5");
        return "d";
    }
}
class Galusca6 {
    static String m() {
        log("6start");
        sleep2(100);
        log("6end");
        return "d";
    }
}
class Galusca7 {
    static String m() {
        log("7start");
        sleep2(100);
        log("7end");
        return "d";
    }
}
