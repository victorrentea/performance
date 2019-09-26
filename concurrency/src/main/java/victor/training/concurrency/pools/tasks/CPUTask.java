package victor.training.concurrency.pools.tasks;

import java.util.Random;

public class CPUTask implements Runnable {
    private final long millis;

    public CPUTask(long millis) {
        this.millis = millis;
    }

    @Override
    public void run() {
        Random r = new Random();
        long tEnd = System.currentTimeMillis() + millis;
        double rez = 0;
        while (System.currentTimeMillis() < tEnd) {
            rez += Math.sqrt(r.nextDouble());
        }
    }
}
