package victor.training.performance.java8.cf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import victor.training.performance.util.NamedThreadFactory;
import victor.training.performance.util.PerformanceUtil;

import java.util.concurrent.*;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.supplyAsync;

public class ThreadPools {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    interface Dependency {
        String network();
        String disk();
        String cpuWork(String s);
    }
    final Dependency dependency;

    public ThreadPools(Dependency dependency) {
        this.dependency = dependency;
    }

    final ExecutorService customExecutor = Executors.newFixedThreadPool(2, new NamedThreadFactory("mypool"));


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
     * Call #network() on 'customExecutor' then pass its result to #cpuWork() running on ForkJoinPool.commonPool
     */
    public CompletableFuture<String> p02_network_then_cpu() {
        String s = dependency.network();
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
     * Call #network() and #disk() on 'customExecutor' then combine their results (network + ' ' + disk)
     * and pass the data to #cpuWork() running on commonPool.
     */
    public CompletableFuture<String> p05_combineAsync() {
        String net = dependency.network();
        String disk = dependency.disk();
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
     * Allow #network() 500 millis to complete in 'customExecutor', otherwise complete with "default".
     */
    public CompletableFuture<String> p07_defaultAfterTimeout() {
        return CompletableFuture.supplyAsync(() -> dependency.network());
    }
}
