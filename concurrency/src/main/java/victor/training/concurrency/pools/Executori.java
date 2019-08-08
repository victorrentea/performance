package victor.training.concurrency.pools;

import java.util.concurrent.*;

import static victor.training.concurrency.ConcurrencyUtil.log;

public class Executori {


    public static void main(String[] args) {
//        ExecutorService executor = Executors.newFixedThreadPool(2);
//        ExecutorService executor = Executors.newSingleThreadExecutor();
//        ExecutorService executor = Executors.newCachedThreadPool(); // nu!

        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(10);
        RejectedExecutionHandler rh = new ThreadPoolExecutor.DiscardOldestPolicy();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 2, 100, TimeUnit.MILLISECONDS, queue
        ,rh);

        for (int i = 0; i < 100; i++) {
            int j = i;
            executor.submit(() -> task(j));
        }
        log("Am trimis tot");

        executor.shutdown();
    }

    static void task(int i) {
        log("Halo! " + i);
    }
}
