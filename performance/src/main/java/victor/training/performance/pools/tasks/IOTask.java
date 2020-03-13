package victor.training.performance.pools.tasks;

import victor.training.performance.ConcurrencyUtil;

public class IOTask implements Runnable {
    private final long millis;

    public IOTask(long millis) {
        this.millis = millis;
    }

    public void run() {
        // emulate waiting for external resources filesystem/socket
        ConcurrencyUtil.sleep2(millis);
    }
}
