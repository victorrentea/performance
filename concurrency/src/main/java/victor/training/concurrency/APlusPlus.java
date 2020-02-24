package victor.training.concurrency;

import java.util.concurrent.atomic.AtomicInteger;

public class APlusPlus {
    private static int population;
    private static final Object monitor = new Object();

    public static class ThreadA extends Thread {
        public void run() {
            int localPopulation = 0;
            for (int i = 0; i < 1000_000; i++) {
                localPopulation++;
            }
            synchronized (monitor) {
                population += localPopulation;
            }
        }
    }

    public static class ThreadB extends Thread {
        public void run() {
            // map-reduce https://ro.wikipedia.org/wiki/MapReduce
            int localPopulation = 0;
            for (int i = 0; i < 1000_000; i++) {
                localPopulation++;
            }
            synchronized (monitor) {
                population += localPopulation;
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
        System.out.println("Total = " + population);
        System.out.println("Took = " + (t1 - t0));
    }
}
