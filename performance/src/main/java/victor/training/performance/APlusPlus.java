package victor.training.performance;

import java.util.concurrent.atomic.AtomicInteger;

public class APlusPlus {
    private static int population;
    public static final Object LOCK = new Object();

    public static class ThreadA extends Thread {
        public void run() {
            int populatieLocala = 0;
            for (int i = 0; i < 100_000; i++) {
                populatieLocala++;
            }
            synchronized (LOCK) {
                population += populatieLocala;
            }
        }
    }

    public static class ThreadB extends Thread {
        public void run() {
            int populatieLocala = 0;
            for (int i = 0; i < 100_000; i++) {
                populatieLocala ++;
            }
            synchronized (LOCK) {
                population += populatieLocala;
            }
        }
    }
    // merge!
//    public static void m() {
//        synchronized (APlusPlus.class) {
//            population++;
//        }
//    }

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
