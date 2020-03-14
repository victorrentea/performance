package victor.training.performance.pools.exercise;

import victor.training.performance.pools.tasks.CPUTask;
import victor.training.performance.pools.tasks.DegradingTask;
import victor.training.performance.pools.tasks.FragileEndpointTask;
import victor.training.performance.pools.tasks.IOTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class Exercise {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        long t0 = System.currentTimeMillis();
        ForkJoinPool fragilePool = new ForkJoinPool(2);
        ForkJoinPool degradingPool = new ForkJoinPool(3);
        ForkJoinPool hugePool = new ForkJoinPool(30);

        List<Future<String>> results = new ArrayList<>();

        for (int i = 0; i < 20; i++) { // Reading data is usually fast
            String element = i + "";
            CompletableFuture<String> future = supplyAsync(() -> Tasks.parse(element))
                    .thenApplyAsync(Tasks::notify, fragilePool)
                    .thenApplyAsync(Tasks::insert, degradingPool)
                    .thenApplyAsync(Tasks::marshall, ForkJoinPool.commonPool())
                    .thenApplyAsync(Tasks::linearWs, hugePool);
//                .thenApplyAsync(Tasks::notify, fragilePool);

            // 16395
            results.add(future);
        }
        for (Future<String> result : results) {
            result.get();
        }
        long t1 = System.currentTimeMillis();
        System.out.println("Delta = " + (t1-t0));
        System.out.println("Processed " + results.size());
    }

}

class Tasks {

    private static final IOTask constantTask = new IOTask(100);
    static String parse(String element) {
        parseTask.run();
        return element + " parsed";
    }

    private static final FragileEndpointTask notifyTask = new FragileEndpointTask(2,10);
    static String notify(String element) {
        notifyTask.run();
        return element + " notified";
    }

    private static final DegradingTask insertTask = new DegradingTask();
    static String insert(String element) {
        insertTask.run();
        return element + " inserted";
    }

    private static final CPUTask marshallTask = new CPUTask(200);
    static String marshall(String element) {
        marshallTask.run();
        return element + " marshalled";
    }

    private static final CPUTask parseTask = new CPUTask(100);

    static String linearWs(String element) {
        constantTask.run();
        return element + " ws";
    }
}
