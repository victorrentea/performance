package victor.training.performance;

import java.util.concurrent.atomic.AtomicInteger;

public class APlusPlus {
//    private static AtomicInteger population = new AtomicInteger(0);
    private static int population;
    public static final Object LOCK = new Object();

    public static class ThreadA extends Thread {
        public synchronized void run() {
            int localPopulation = 0;
            for (int i = 0; i < 1000_000; i++) {
//                synchronized (LOCK) {
//                    population.incrementAndGet();
//                }
                localPopulation++;
            }
            synchronized (LOCK) {
                population += localPopulation;
            }
        }
    }

    public static class ThreadB extends Thread {
        public /*synchronized*/ void run() {
            int localPopulation = 0;

            for (int i = 0; i < 1000_000; i++) {
//                synchronized (LOCK) {
    //                population++;
//                }
//                    population.incrementAndGet();
                localPopulation++;
            }
            synchronized (LOCK) {
                population += localPopulation;
            }
        }
    }

    // TODO (bonus): ConcurrencyUtil.useCPU(1)
    // TODO (extra bonus): Analyze with JFR

    public static void main(String[] args) throws InterruptedException {
//        synchronized (APlusPlus.class) {
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
//        }
    }
}
