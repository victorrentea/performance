package victor.training.performance.pools.tasks;

import victor.training.performance.PerformanceUtil;

public class IOTask implements Runnable {
    private final long millis;

    public IOTask(long millis) {
        this.millis = millis;
    }

    public void run() {
        // emulate waiting for external resources eg: filesystem/network
        PerformanceUtil.sleepq(millis);
    }
}
