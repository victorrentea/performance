package victor.training.performance;

import java.util.concurrent.atomic.AtomicInteger;

public class APlusPlus {

    private static final Object MUTEX = new Object();
    private static int infected;

    public static class ThreadA extends Thread {
        public void run() {
            int localInfected = 0;
            for (int i = 0; i < 100_000; i++) {
                 localInfected++;
            }
            synchronized (MUTEX) {
                infected += localInfected;
            }
        }
    }
    public static class ThreadB extends Thread {
        public void run() {
            int localInfected = 0;
            for (int i = 0; i < 100_000; i++) {
                localInfected++;
            }
            synchronized (MUTEX) {
                infected += localInfected;
            }
        }
    }
    // TODO (bonus): ConcurrencyUtil.useCPU(1)

    public static void main(String[] args) throws InterruptedException {
        ThreadA threadA = new ThreadA();
        ThreadB threadB = new ThreadB();

        long t0 = System.currentTimeMillis();

        threadA.start();
        threadB.start();
        threadA.join();
        threadB.join();

        long t1 = System.currentTimeMillis();
        System.out.println("Total = " + infected);
        System.out.println("Took = " + (t1 - t0));
    }
}
