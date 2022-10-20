package victor.training.performance.pools.tasks;

import java.util.concurrent.atomic.AtomicInteger;

import static victor.training.performance.util.PerformanceUtil.log;
import static victor.training.performance.util.PerformanceUtil.sleepMillis;

public class FragileEndpointTask implements Runnable {
    private final int maxParallel;
    private final int delay;
    private AtomicInteger requestIndex = new AtomicInteger(0);
    private AtomicInteger parallelRequests = new AtomicInteger(0);
    public FragileEndpointTask(int maxParallel, int delay) {
        this.maxParallel = maxParallel;
        this.delay = delay;
    }
    public FragileEndpointTask() {
        this(2, 1000);
    }

    public void run() {
        int parallel = parallelRequests.incrementAndGet();
        try {
            if (parallel > maxParallel) {
                log("KABOOOM!!");
                throw new IllegalArgumentException("Per SLA, you are not allowed to be called by more than two parallel requests. Prepared to be sued!");
            }
            log("Handling request #" + requestIndex.incrementAndGet());
            sleepMillis(delay);
            log("Done");
        } finally {
            parallelRequests.decrementAndGet();
        }
    }
}
