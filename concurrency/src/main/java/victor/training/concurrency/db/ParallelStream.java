package victor.training.concurrency.db;

import victor.training.concurrency.ConcurrencyUtil;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static victor.training.concurrency.ConcurrencyUtil.log;
import static victor.training.concurrency.ConcurrencyUtil.sleep2;

public class ParallelStream {
    public static void main(String[] args) throws InterruptedException {

        List<Integer> list = asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);


        new Thread(()->{
            sleep2(1000);
            asList("a","b","c","d","e").parallelStream()
                    .map(s-> {
                        log("Upper " + s);
                        return s.toUpperCase();
                    })
                    .forEach(System.out::println);
        }).start();

        Stream<Integer> stream = list.parallelStream()
                .filter(n -> {
                    log("Filter " + n);
                    return n % 2 == 1;
                })
                .distinct()
                .map(n -> {
                    log("Map " + n);
                    // fac un query in baza / citesc de pe disc / apel HTTP (orice iese din MEM-PROCESOR)
                    // NICIODATA nu face in parallelStream-ul rulat pe common pool apeluri IO
                    sleep2(3000);
                    log("Map done " + n);
                    return n * n;
                });



        ForkJoinPool poolManual = new ForkJoinPool(8);
//                .forEach(n -> log("Print " + n));
        poolManual.submit(() -> stream.forEachOrdered(n -> log("Print " + n))); // le reordoneaza

        poolManual.shutdown();;
        poolManual.awaitTermination(1, TimeUnit.MINUTES);

        // CAND FOLOSESC PARALLEL STREAM (cand ma ajuta):
        // - am un set de elemente si pe fiecare am de facut procesare considerabila:
        // a) DACA e doar CPU calcul => pot pe forkjoinpool-ul comun din JVM
        // b) daca e si I/O, sleep, query-uri, http ==> pot doar pe un forkjoinpool propriu
        //    (vezi mai sus cum termin streamul intr-un task rulat pe JFP-ul meu)

        // NU parallelStream daca pe elemente ai procesare foarte putina
        // -- overheadul paralelizarii va fi mai mare decat castigul
        // un for (e:lista) de muuult mai rapid daca ceea ce faci in for e foarte rapid.

    }
}
