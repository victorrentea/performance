package victor.training.performance.completableFuture;

import java.util.concurrent.CompletableFuture;

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

    public CompletableFuture<String> p03_cpu_then_cpu(String s1, String s2) {
        return supplyAsync(() -> dependency.cpuWork(s1))
                .thenCompose(r1-> supplyAsync(() -> dependency.cpuWork(s2)).thenApply(r2 -> r1 + r2));
    }

    public CompletableFuture<String> p04_cpu_par_cpu(String s1, String s2) {
        return supplyAsync(() -> dependency.cpuWork(s1))
                .thenCombine(supplyAsync(() -> dependency.cpuWork(s2)), (r1,r2)-> r1 + r2);
    }

    public CompletableFuture<String> p05_combineAsync() {
        CompletableFuture<String> networkFuture = supplyAsync(() -> dependency.network(), customExecutor);
        CompletableFuture<String> diskFuture = supplyAsync(() -> dependency.disk(), customExecutor);
        return networkFuture.thenCombineAsync(diskFuture, (n, d) -> dependency.cpuWork(n + " " + d));
    }

    public CompletableFuture<String> p06_delayed() {
        return supplyAsync(() -> "Surprise!", CompletableFuture.delayedExecutor(500, MILLISECONDS));
    }

    public CompletableFuture<String> p07_defaultAfterTimeout() {
        return CompletableFuture.supplyAsync(() -> dependency.network())
                .completeOnTimeout("default", 500, MILLISECONDS);
    }
}
