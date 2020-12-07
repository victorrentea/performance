package victor.perf.tasks;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import static victor.perf.tasks.ConcurrencyUtil.measureCall;

public class CPUTask implements Callable<Integer> {
    private static final Map<Long, Long> WORK_FOR_MILLIS = new HashMap<>();
    private final long work;

    public CPUTask(long millis) {
        this.work = WORK_FOR_MILLIS.computeIfAbsent(millis, CPUTask::estimateWorkForMillis);
    }

    public Integer call() {
        return doWork(work);
    }

    private static int doWork(long n) {
        int s = 0;
        for (int i = 0; i < n; i++) {
            s+=new BigDecimal(i).pow(i).intValue();
        }
        return s;
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

}
