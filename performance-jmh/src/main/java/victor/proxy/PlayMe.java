package victor.proxy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlayMe {

    public static void main(String[] args) throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        pool.submit(() -> {
            for (int i = 0; true; i++) {
                i+=Math.sqrt(i);
                if (Thread.currentThread().isInterrupted()) return;
            }
        });
        System.out.println("Steady...");
        Thread.sleep(1000);
        pool.shutdownNow();
        System.out.println("Died");

    }
}
