package victor.training.performance.completableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import static java.util.concurrent.CompletableFuture.*;

public class Combining {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    interface Dependency {
        CompletableFuture<String> call();

        CompletableFuture<Void> task(String s);

        CompletableFuture<Integer> parseIntRemotely(String s);

        void cleanup();

        CompletableFuture<Integer> fetchAge();

        CompletableFuture<Void> audit(String s);
    }

    final Dependency dependency;

    public Combining(Dependency dependency) {
        this.dependency = dependency;
    }


    // ==================================================================================================

    /**
     * Return the uppercase of the future value, without blocking (.get() or .join()).
     */
    public CompletableFuture<String> p01_transform() {
        return dependency.call()
                .thenApply(v->v.toUpperCase());
    }

    // ==================================================================================================

    /**
     * Run dependency#task(s), then dependency#cleanup();
     * Hint: completableFuture.then....
     */
    public void p02_chainRun(String s) {
        Void v = null;
        CompletableFuture<Void> cfVoid = dependency.task(s);
        // De ce intoarce oare task CF<Void> daca io stiu ca singura valoare pote veni null
        // - sa afli eroarea
        // - sa poti sa astepti sa se termine
        cfVoid.thenRun(() -> dependency.cleanup());
    }

    // ==================================================================================================

    /**
     * Run dependency#task(s) passing the string returned by the dependency#call().
     * Obviously, Do not block (get/join)!
     */
    public void p03_chainConsume() throws InterruptedException, ExecutionException {
//        String s = dependency.call().get(); // punctele de fidele
//
//        dependency.task(s); // promotii disponibilia


        dependency.call()
                .thenAccept(s -> dependency.task(s));
    }

    // ==================================================================================================
    /**
     * Launch #call(); when it completes, call #parseIntRemotely(s) with the result,
     * and return the parsed int.
     */
    public CompletableFuture<Integer> p04_chainFutures() throws ExecutionException, InterruptedException {
//        String s = dependency.call().get();
//        int i = dependency.parseIntRemotely(s).get();
//        return completedFuture(i);
//
        return dependency.call()
                .thenCompose(s -> dependency.parseIntRemotely(s))
                ;

                //                .thenCombine(dependency.parseIntRemotely(s))
        //flatMap
    // Optional<Optional<Integer>> ->
    /// Stream<Stream<>>
//                .thenAccept(s -> dependency.parseIntRemotely(s));

    }

    // ==================================================================================================

    /**
     * Same as previous, but return a CompletableFuture< Void > to let the caller:
     * (a) know when the task finished, and/or
     * (b) find out of any exceptions
     */
    public CompletableFuture<Void> p05_chainFutures_returnFutureVoid() throws ExecutionException, InterruptedException {
//        String s = dependency.call().get();
//        dependency.task(s);
//        return completedFuture(null);

        return dependency.call()
                .thenAccept(s -> dependency.task(s));
    }


    // ==================================================================================================

    /**
     * Launch #call; when it completes launch #task and #cleanup (necesar dupa call);
     * After both complete, complete the returned future.
     * Reminder: Don't block! (no .get or .join) in the entire workshop!
     * Play: try to run #task() and #cleanup() in parallel (log.info prints the thread name) Hint: ...Async(
     */
    public CompletableFuture<Void> p06_all() throws ExecutionException, InterruptedException {
//        String s = dependency.call().get();
//        dependency.task(s).get();
//        dependency.cleanup();
//        return completedFuture(null);

        CompletableFuture<String> futureString = dependency.call();

        // Fork: dintr-un singur CF pornesti 2,3...
        // astea 2 nu ruleaza in paralel!! acum
        CompletableFuture<Void> future1 = futureString
                .thenComposeAsync(s -> dependency.task(s));
        CompletableFuture<Void> future2 = futureString
                .thenRunAsync(() -> dependency.cleanup());

        // join
//        return future1.thenCombine(future2, (v1,v2) -> null);
        return CompletableFuture.allOf(future1, future2);
//        , future3)
//                .thenApply(v -> {
//                    future1.get() // sigur non-blocante
//                    future2.get()
//                    future3.get()
//                    return new Obiect
//                });
    }

    // ==================================================================================================

    /**
     * Launch #call and #fetchAge. When BOTH complete, combine their values like so:
     * callResult + " " + ageResult
     * and complete the returned future with this value. Don't block.
     */
    public CompletableFuture<String> p07_combine() {
        return null;
    }

    // ==================================================================================================

    /**
     * === The contest ===
     * Launch #call and #fetchAge in parallel.
     * The value of the FIRST to complete (ignore the other),
     *      converted to string, should be used to complete the returned future.
     * Hint: thenCombine waits for all to complete. - Not good
     * Hint#2: Either... or anyOf()
     * -- after solving Exceptions.java  --
     * [HARD⭐️] if the first completes with error, wait for the second.
     * [HARD⭐️⭐️⭐️] If both in error, complete in error.
     */
    public CompletableFuture<String> p08_fastest() {
         return null;
    }

    // ==================================================================================================
    /**
     * Launch #call(); When it completes, call #audit() with the value and then return it.
     * ⚠️ Don't wait for #audit() to complete (useful in case it takes time)
     * ⚠️ If #audit() fails, ignore that error (don't fail the returned future), but also log the error!
     */
    public CompletableFuture<String> p09_fireAndForget() throws ExecutionException, InterruptedException {
        String s = dependency.call().get();
        dependency.audit(s).get(); // <- run this in fire-and-forget style
        return completedFuture(s);
    }

}
