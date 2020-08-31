package victor.training.performance;

import victor.training.performance.java8.CompletableFutures;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class APlusPlus {
    private static int population;
    private static final Object MONITOR = new Object();

    public static class Taska implements  Supplier<Integer> {
        @Override
        public Integer get()  {
            int localPopulation = 0;
            for (int i = 0; i < 100_000; i++) {
                localPopulation++;
            }
            return localPopulation;
        }
    }

    public static class Taskb implements Callable<Integer> {

        @Override
        public Integer call() throws Exception {
            int localPopulation = 0;
            for (int i = 0; i < 100_000; i++) {
                localPopulation++;
            }
            return localPopulation;
        }
    }

    // TODO (bonus): ConcurrencyUtil.useCPU(1)


    public static void main(String[] args) throws InterruptedException {
        Taska threadA = new Taska();
        Taskb threadB = new Taskb();

        long t0 = System.currentTimeMillis();

        CompletableFuture.supplyAsync(new Taska())

        threadA.start();
        threadB.start();
        threadA.join();
        threadB.join();

        long t1 = System.currentTimeMillis();
        System.out.println("Total = " + population);
        System.out.println("Took = " + (t1 - t0));
    }
}
