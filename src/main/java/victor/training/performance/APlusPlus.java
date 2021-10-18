package victor.training.performance;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

// race bug
public class APlusPlus {
//    private static Integer population = 0;
//    private static final Object LOCK = new Object();
//    private static AtomicInteger population = new AtomicInteger(0);


//    static List<String> ssnSet = Collections.synchronizedList(new ArrayList<>());


    public static class ThreadA implements Callable<Integer> {
        public Integer call() {
            List<String> ssns = new ArrayList<>();
            int localPopulation = 0;
            for (int i = 0; i < 10_000; i++) {
                localPopulation ++;

                String mySSN = UUID.randomUUID().toString();

//                if (ssns.contains(mySSN)) {
//                    ssns.add(mySSN);
//                    // extra logic
//                }
            }
            return localPopulation;
        }
    }

    public static class ThreadB implements Callable<Integer> {
        public Integer call() {
            int localPopulation = 0;
            for (int i = 0; i < 10_000; i++) {
                localPopulation ++;

                String mySSN = UUID.randomUUID().toString();

//                if (ssnSet.contains(mySSN)) {
//                    ssnSet.add(mySSN);
//                }
            }
            return localPopulation;
        }
    }

    // TODO (bonus): ConcurrencyUtil.useCPU(1)
    // TODO (extra bonus): Analyze with JFR

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        ExecutorService pool = Executors.newFixedThreadPool(2);

        Future<Integer> f1 = pool.submit(new ThreadA());
        Future<Integer> f2 = pool.submit(new ThreadB());

        long t0 = System.currentTimeMillis();


        long t1 = System.currentTimeMillis();
        int population = f1.get() + f2.get ();
        System.out.println("Total = " + population);
        System.out.println("Took = " + (t1 - t0));
//        System.out.println("SSN size = "  +ssnSet.size());
    }
}
