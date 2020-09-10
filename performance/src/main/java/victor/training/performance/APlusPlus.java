package victor.training.performance;

public class APlusPlus {
    private static Integer population = 0;

    public static class ThreadA extends Thread {
        public void run() {
            for (int i = 0; i < 10_000; i++) {
                population++;
            }
        }
    }

    public static class ThreadB extends Thread {
        public void run() {
            for (int i = 0; i < 10_000; i++) {
                population++;
            }
        }
    }

    // TODO (bonus): ConcurrencyUtil.useCPU(1)
    // TODO (extra bonus): Analyze with JFR

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
