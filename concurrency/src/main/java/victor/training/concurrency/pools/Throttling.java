package victor.training.concurrency.pools;

import victor.training.concurrency.ConcurrencyUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static victor.training.concurrency.ConcurrencyUtil.log;
import static victor.training.concurrency.ConcurrencyUtil.sleep2;

public class Throttling {
    private static final FragileResource fragile = new FragileResource();
    public static void main(String[] args) {
        ExecutorService httpPool = Executors.newFixedThreadPool(4);
        IntStream.range(1,10).forEach(i -> httpPool.submit(() -> processHttpRequest(i)));
        httpPool.shutdown();
    }

    private static void processHttpRequest(int i) {
        fragile.call(i);
    }
}

class FragileResource {
    private int parallelRequests;
    public void call(int i) {
        parallelRequests++;
        try {
            if (parallelRequests >= 3) {
                log("KABOOOM!!");
                throw new IllegalArgumentException("Not allowed to be called by more than two parallel requests");
            }
            log("Handling request " + i);
            sleep2(1000);
            log("Done");
        } finally {
            parallelRequests--;
        }
    }
}
