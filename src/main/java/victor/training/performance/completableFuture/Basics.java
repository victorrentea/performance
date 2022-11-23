package victor.training.performance.completableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
        }else
        return CompletableFuture.completedFuture("Hi");

    }

    // ==================================================================================================

    /**
     * BLOCK the current thread to get the value from the future using .get();
     * .join() (Not recommended in production).
     * Note: only runtime exceptions
     */
    public String p03_join(CompletableFuture<String> future) {
//        return future.get();
        return future.join();
    }

    // ==================================================================================================

    /**
     * BLOCK the current thread to get the value from the future using .join() (Not recommended in production).
     * If the computation failed with any exception return the message of that exception.
     * Note: the original exception comes wrapped in another exception - guess which one?
     */
    public String p04_joinException(CompletableFuture<String> future) {

//        try {
//            faceRetea();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }

        return null;
    }

//    public CompletableFuture<String> faceRetea() {
//
//    }

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
        CompletableFuture.runAsync(() -> {
            log.info("Hi");
        });
    }

    // ==================================================================================================

    /**
     * Run Thread.currentThread().getName() in another thread,
     * and complete the returned CF with that value.
     *
     * Hint: pass a Supplier to a static method of CF
     * Play: what is that thread name? Google that thread name [2 min].
     */
    public CompletableFuture<String> p07_supply() {
        ExecutorService alMeu = Executors.newFixedThreadPool(1);
        return CompletableFuture.supplyAsync(() ->
                 Thread.currentThread().getName()/*, alMeu*/);
    }
    // ==================================================================================================

    /**
     * Print the value in the future, whenever it's ready.
     */
    public void p08_accept(CompletableFuture<String> future) {
        // ex reale: sa dau send pe un kafka
        // sa dau un mail., sa inserez in baza.

        future.thenAccept(v -> {
            System.out.println(v); // callback atunci cand futureul se "completeaza"
            // 2 scenarii sunt posibile aici:
            //1) ruleaza pe ALT THREAD(ala care termina CF primit param)
            //2) rulez AICI PE LOC IN THREADUL MEU: daca CF deja e terminat
        });
    }


}
