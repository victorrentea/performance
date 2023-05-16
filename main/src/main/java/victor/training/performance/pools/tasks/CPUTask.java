package victor.training.performance.pools.tasks;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static victor.training.spring.batch.util.PerformanceUtil.log;
import static victor.training.spring.batch.util.PerformanceUtil.measureCall;

public class CPUTask implements Runnable {
    private static final Map<Long, Long> WORK_FOR_MILLIS = new HashMap<>();
    private final long work;

    public CPUTask(long millis) {
        this.work = WORK_FOR_MILLIS.computeIfAbsent(millis, CPUTask::estimateWorkForMillis);
    }

    @Override
    public void run() {
        doWork(work);
    }

    private static void doWork(long n) {
        for (int i = 0; i < n; i++) {
            new BigDecimal(i).pow(i);
        }
    }

    private static long estimateWorkForMillis(long targetMillis) {
        System.out.print("Warming up... ");
        for (int i = 100; i < 1000; i++) {
            doWork(i);
        }
        System.out.println("Done");
        System.out.println("Estimating work to take " + targetMillis + " ms of CPU on 1 thread");
        for (long n = 100; ; n *= 1.05) {
            System.gc();
            long nn = n;
            int dt = measureCall(() -> doWork(nn));
            if (dt > targetMillis) {
                System.out.println("n=" + n);
                return n;
            }
        }
    }

    public static void main(String[] args) {
        CPUTask cpu = new CPUTask(100);
        log("Checking...");
        for (int i = 0; i < 20; i++) {
            System.out.println(measureCall(() -> cpu.run()));
        }
    }
}
