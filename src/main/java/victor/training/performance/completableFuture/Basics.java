package victor.training.performance.completableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Basics {
    protected final Logger log = LoggerFactory.getLogger(getClass());


    // ==================================================================================================

    /**
     * Create a completed future with the value "Hi"
     */
    public CompletableFuture<String> p01_completed() {
        return CompletableFuture.completedFuture("Hi");
    }

    // ==================================================================================================

    /**
     * Create a failed future with a new IllegalArgumentException() if 'failed' is false;
     * otherwise complete with "Hi".
     * Note: a method returning CompletableFuture is NOT allowed to throw exceptions, so this is a good practice!👌
     */
    public CompletableFuture<String> p02_failed(boolean failed) {
        if (failed) {
            return CompletableFuture.failedFuture(new IllegalArgumentException());
        }
        return CompletableFuture.completedFuture("Hi");
    }

    // ==================================================================================================

    /**
     * BLOCK the current thread to get the value from the future using .join() (Not recommended in production).
     * Note: only runtime exceptions
     */
    public String p03_join(CompletableFuture<String> future) {
        return null; // SKIP
    }

    // ==================================================================================================

    /**
     * BLOCK the current thread to get the value from the future using .join() (Not recommended in production).
     * If the computation failed with any exception return the message of that exception.
     * Note: the original exception comes wrapped in another exception - guess which one?
     */
    public String p04_joinException(CompletableFuture<String> future) {
        try {
            return future.join();
        } catch (Exception e) {
            return e.getCause().getMessage();
        }
    }

    // ==================================================================================================

    /**
     * BLOCK the current thread to get the value from the future using get(). (Not recommended in production).
     * If the computation failed with any exception return the message of that exception.
     * Note: the original exception comes wrapped in another exception - guess which one?
     * #Play: note the InterruptedException; try to .cancel(true) the future and see what happens.
     */
    public String p05_get(CompletableFuture<String> future) throws InterruptedException, ExecutionException {
        return null;
    }


    // ==================================================================================================

    /**
     * Run log.info("Hi") in another thread.
     * Hint: use a method ending in ..Async(Runnable)
     */
    public void p06_run() {

    }

    // ==================================================================================================

    /**
     * Run Thread.currentThread().getName() in another thread, and complete the returned CF with that value.
     * Hint: pass a Supplier to a static method of CF
     * Play: what is that thread name? Google that thread name [2 min].
     */
    public CompletableFuture<String> p07_supply() {
        return null;
    }
    // ==================================================================================================

    /**
     * Print the value in the future, whenever it's ready.
     */
    public void p08_accept(CompletableFuture<String> future) {
//        while (!future.isDone()) ; // get fired
        future.thenAccept(s -> System.out.println(s));
    }


}
