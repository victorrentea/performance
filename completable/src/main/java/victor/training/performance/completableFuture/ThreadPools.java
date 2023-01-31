package victor.training.performance.completableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import victor.training.performance.completableFuture.util.NamedThreadFactory;
import victor.training.performance.completableFuture.util.PerformanceUtil;

import java.util.concurrent.*;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.supplyAsync;

public class ThreadPools {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    interface Dependency {
        String blockNetwork();
        String blockDisk();
        String cpuWork(String s);
    }
    final Dependency dependency;

    public ThreadPools(Dependency dependency) {
        this.dependency = dependency;
    }

    final ExecutorService customExecutor = Executors.newFixedThreadPool(2, new NamedThreadFactory("customExecutor"));


    // ==================================================================================================
    /**
     * Call dependency#cpuWork() on the ForkJoinPool.commonPool, no matter on what thread you are called.
     */
    public CompletableFuture<String> p01_cpu(String s) {
        String result = dependency.cpuWork(s);
        return completedFuture(result);
    }

    // ==================================================================================================
    /**
     * Call #blockNetwork() on 'customExecutor' then pass its result to #cpuWork() running on ForkJoinPool.commonPool
     * Hint: you'll need to use two methods ending in ...Async
     */
    public CompletableFuture<String> p02_network_then_cpu() {
        String s = dependency.blockNetwork();
        return completedFuture(dependency.cpuWork(s));
    }


    // ==================================================================================================
    /**
     * Call #cpuWork(s1), then #cpuWork(s2), then and return the combined value.
     * Note: Don't block the current thread (execute ...Async)
     */
    public CompletableFuture<String> p03_cpu_then_cpu(String s1, String s2) {
        String r1 = dependency.cpuWork(s1);
        String r2 = dependency.cpuWork(s2);
        return completedFuture(r1 + r2);
    }

    // ==================================================================================================
    /**
     * Call #cpuWork(s1) in parallel with #cpuWork(s2) and return the combined value.
     * Note: Don't block the current thread (execute ...Async)
     */
    public CompletableFuture<String> p04_cpu_par_cpu(String s1, String s2) {
        String r1 = dependency.cpuWork(s1);
        String r2 = dependency.cpuWork(s2);
        return completedFuture(r1 + r2);
    }

    // ==================================================================================================
    /**
     * Call #blockNetwork() and #blockDisk() on 'customExecutor' then combine their results (blockNetwork + ' ' + blockDisk)
     * and pass the data to #cpuWork() running on commonPool.
     */
    public CompletableFuture<String> p05_combineAsync() {
        String net = dependency.blockNetwork();
        String disk = dependency.blockDisk();
        String result = dependency.cpuWork(net + " " + disk);
        return completedFuture(result);
    }

    // ==================================================================================================
    /**
     * The returned future should complete in 500 millis with the value "Surprise!" [Java 11]
     */
    public CompletableFuture<String> p06_delayed() {
        PerformanceUtil.sleepMillis(500);
        return completedFuture("Surprise!");
    }

    // ==================================================================================================

    /**
     * Allow #blockNetwork() 500 millis to complete in 'customExecutor', otherwise complete with "default".
     */
    public CompletableFuture<String> p07_defaultAfterTimeout() {
        return CompletableFuture.supplyAsync(() -> dependency.blockNetwork());
    }
}
