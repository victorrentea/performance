package victor.training.performance.java8.cf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import victor.training.performance.util.NamedThreadFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class ThreadPoolsSolved extends ThreadPools{
    public ThreadPoolsSolved(Dependency dependency) {
        super(dependency);
    }

    public CompletableFuture<String> p01_cpu(String s) {
        return supplyAsync(() -> dependency.cpuWork(s));
    }

    public CompletableFuture<String> p02_network_then_cpu() {
        return supplyAsync(() -> dependency.network(), customExecutor)
                .thenApplyAsync(s -> dependency.cpuWork(s));
    }

    public CompletableFuture<String> p03_combineAsync() {
        CompletableFuture<String> networkFuture = supplyAsync(() -> dependency.network(), customExecutor);
        CompletableFuture<String> diskFuture = supplyAsync(() -> dependency.disk(), customExecutor);
        return networkFuture.thenCombineAsync(diskFuture, (n, d) -> dependency.cpuWork(n + " " + d));
    }

    public CompletableFuture<String> p04_delayed() {
        return supplyAsync(() -> "Surprise!", CompletableFuture.delayedExecutor(500, MILLISECONDS));
    }
}
