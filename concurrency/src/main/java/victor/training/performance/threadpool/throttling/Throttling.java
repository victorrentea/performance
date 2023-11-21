package victor.training.performance.threadpool.throttling;

import victor.training.performance.util.PerformanceUtil;
import victor.training.performance.threadpool.tasks.FragileEndpointTask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Throttling {
    private static final FragileEndpointTask fragile = new FragileEndpointTask();

    public static void main(String[] args) {
        ExecutorService httpPool = Executors.newFixedThreadPool(20);
        for (int i = 0; i < 100; i++) {
            PerformanceUtil.log("Requesting " + i);
            httpPool.submit(fragile);
        }
        httpPool.shutdown();
    }

}

