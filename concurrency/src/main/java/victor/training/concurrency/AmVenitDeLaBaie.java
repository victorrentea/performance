package victor.training.concurrency;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

import static victor.training.concurrency.ConcurrencyUtil.log;
import static victor.training.concurrency.ConcurrencyUtil.sleep2;

public class AmVenitDeLaBaie {
    public static void main(String[] args) throws ExecutionException, InterruptedException {


        List<Integer> ids = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);


//        ExecutorService pool = Executors.newCachedThreadPool(); // aproape mereu o idee proasta ca nu stii cate elemente ai maxim de procesat simultan
        ExecutorService pool = Executors.newFixedThreadPool(2); // aproape mereu o idee proasta ca nu stii cate elemente ai maxim de procesat simultan

        long t0 = System.currentTimeMillis();
        List<Integer> results = new ArrayList<>();
        List<Future<Integer>> futures = new ArrayList<>();
        for (Integer id : ids) {
            Future<Integer> futureResult = pool.submit(() -> callWebService(id));
            futures.add(futureResult);
        }
        log("Am lansat cererile");
        for (Future<Integer> future : futures) {
            Integer r = future.get();
            results.add(r);
        }
        System.out.println("Results: " + results);

        pool.submit(() -> dauSms("Gata sefu"));

        System.out.println("Gata tot.");
        long t1 = System.currentTimeMillis();
        System.out.println(t1 - t0);
        pool.shutdown();


    }
    private static void dauSms(String message) {
        log("Sending SMS: " + message);
        sleep2(2000);
        if (true) {
            throw new IllegalArgumentException();
        }
        log("Sent SMS");
    }

    public static int callWebService(int id) {
        log("I'm calling you... " + id);
        sleep2(100);
        int r = id * 2;
        if (Math.random() < 0.3) {
            throw new IllegalArgumentException();
        }
        log("Gata: " + r);
        return r;
    }
}
