package victor.training.performance;

import java.sql.CallableStatement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class APlusPlus {
    private static AtomicInteger population = new AtomicInteger();
    static List<String> names = Collections.synchronizedList(new ArrayList<>());

    public static class ThreadA implements Callable<Integer> {
        public Integer call() {
            int localCount = 0;
            for (int i = 0; i < 1000_000; i++) {
                localCount++;
//                names.add("a");
            }
            return localCount;
        }
    }

    public static class ThreadB implements Callable<Integer> {
        public Integer call() {
            int localCount= 0;
            for (int i = 0; i < 1000_000; i++) {
//                names.add("a");
                localCount++;
            }
            return localCount;
        }
    }

    // TODO (bonus): ConcurrencyUtil.useCPU(1)
    // TODO (extra bonus): Analyze with JFR

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService pool = Executors.newFixedThreadPool(2);

        long t0 = System.currentTimeMillis();

        Future<Integer> fa = pool.submit(new ThreadA());
        Future<Integer> fb = pool.submit(new ThreadB());


        int population = fa.get() + fb.get();

        long t1 = System.currentTimeMillis();
        System.out.println("Total = " + population);
        System.out.println("Took = " + (t1 - t0));
    }
}
