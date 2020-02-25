package victor.training.concurrency;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;

import static victor.training.concurrency.ConcurrencyUtil.log;
import static victor.training.concurrency.ConcurrencyUtil.sleep2;

public class DaiCuParunStream {

    public static void main(String[] args) {

//        List<Integer> l = new ArrayList<>();
//        l.stream
        IntStream range = IntStream.rangeClosed(1, 1000);

        IntStream intStream = range
                .parallel()
                .map(i -> {
                    log("Square " + i);

                    return i * i;
                })
                .peek(i -> {
                    log("Spionez pe " + i);
                    sleep2(1000); // NU
//                    writer.write // NU
//                    rs.next // NU
//DA:                     criptari, calcule, XML parsing in memorie
                })
                .filter(i -> i % 2 == 0);

        // ca sa inoti in piscina ta privata, ti-o sapi singur, si te arunci cu op terminala a streamului in sumbit().
        ForkJoinPool pool = new ForkJoinPool(50);
        pool.submit(() -> {intStream.forEach(n -> log("END: " + n));});
        pool.shutdown();
        sleep2(5000);
    }
}
