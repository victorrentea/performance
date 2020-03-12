package victor.training.concurrency.pools.tasks;

import victor.training.concurrency.ConcurrencyUtil;

public class IOTask implements Runnable {
    private final long millis;

    public IOTask(long millis) {
        this.millis = millis;
    }

    public void run() {
        // waiting for external resources filesystem/socket
        ConcurrencyUtil.sleep2(millis);
    }
}
