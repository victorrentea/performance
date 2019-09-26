package victor.training.concurrency.pools.tasks;

import java.util.concurrent.atomic.AtomicInteger;

import static victor.training.concurrency.ConcurrencyUtil.log;
import static victor.training.concurrency.ConcurrencyUtil.sleep2;

public class LimitedExternalSystemTask implements Runnable {
    private AtomicInteger concurrentCalls = new AtomicInteger(0);
    public void run() {
        int concurrent = concurrentCalls.incrementAndGet();
        int delta = 100;
        if (concurrent > 3) delta += Math.pow(concurrent - 3, 2) * 30;
        // log("Respoding in " + delta + " millis due to " + concurrent + " concurrent requests");
        sleep2(delta);
        concurrentCalls.decrementAndGet();
    }
}
