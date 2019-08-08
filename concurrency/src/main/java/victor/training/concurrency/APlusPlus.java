package victor.training.concurrency;

import java.util.concurrent.atomic.AtomicInteger;

/** Thread safe */
class Suspect {
    private int date;
//    private final Object monitor = new Object();

    void m() {
        int date2;
    }
    synchronized void inc() {
        date++;
    }
}


public class APlusPlus {
    private static int populatie;
    private static final Object monitor = new Object();
    private static Suspect s = new Suspect();
    private static Suspect s2 = new Suspect();



    public static class ThreadA extends Thread {
        public void run() {
            s.inc();
            int populatieLocala = 0;
            for (int i = 0; i < 1000000; i++) {
                populatieLocala++;
            }
            synchronized (monitor) {
                populatie+=populatieLocala;
            }
        }
    }

    public static class ThreadB extends Thread {
        public void run() {
            s2.inc();
            int populatieLocala = 0;
            for (int i = 0; i < 1000000; i++) {
                populatieLocala++;
            }
            synchronized (monitor) {
                populatie += populatieLocala;
            }
        }
    }


    public static void main(String[] args) throws InterruptedException {
        ThreadA threadA = new ThreadA();
        ThreadB threadB = new ThreadB();

        long t0 = System.currentTimeMillis();
        threadA.start();
        threadB.start();
        threadA.join();
        threadB.join();
        long t1 = System.currentTimeMillis();
        System.out.println(populatie);
        System.out.println("Delta = " + (t1 - t0));
    }
}
