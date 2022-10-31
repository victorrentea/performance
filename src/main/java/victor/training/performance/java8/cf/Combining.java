package victor.training.performance.java8.cf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import victor.training.performance.util.PerformanceUtil;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.*;

public class Combining {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    interface Dependency {
        CompletableFuture<String> call();

        CompletableFuture<Void> task(String s);

        void cleanup();

        CompletableFuture<Integer> fetchAge();
    }

    final Dependency dependency;

    public Combining(Dependency dependency) {
        this.dependency = dependency;
    }


    // ==================================================================================================

    /**
     * Return the uppercase of the future value, not blocking.
     */
    public CompletableFuture<String> p01_transform() {
        return dependency.call();
    }

    // ==================================================================================================

    /**
     * Run dependency#task(s) passing the string provided as parameter, then dependency#cleanup();
     * Hint: completableFuture.then....
     */
    public void p02_chainRun(String s) {
        dependency.task(s);
        dependency.cleanup();
    }

    // ==================================================================================================

    /**
     * Run dependency#task(s) passing the string provided by the dependency#call(). Do not block (get/join)!
     */
    public void p03_chainConsume() throws InterruptedException, ExecutionException {
        String s = dependency.call().get();
        dependency.task(s);
    }


    // ==================================================================================================

    /**
     * Same as previous, but return a CF< Void > to let the caller know of when the task finishes, and of any exceptions
     */
    public CompletableFuture<Void> p04_flatMap() throws ExecutionException, InterruptedException {
        String s = dependency.call().get();
        dependency.task(s);
        return completedFuture(null);
    }

    // ==================================================================================================

    /**
     * Launch #call;
     * When it completes launch #task and #cleanup in parallel;
     * Wait for both to complete and then complete the returned future.
     * Not blocking.
     */
    public CompletableFuture<Void> p05_forkJoin() throws ExecutionException, InterruptedException {
        String s = dependency.call().get();
        dependency.task(s).get();
        dependency.cleanup();
        return completedFuture(null);
    }

    // ==================================================================================================

    /**
     * Launch #call and #fetchAge in parallel. When both complete, combine their values like so:
     * callResult + " " + ageResult
     * and complete the returned future with this value. Don't block.
     */
    public CompletableFuture<String> p06_combine() {
        return null;
    }

    // ==================================================================================================

    /**
     * Launch #call and #fetchAge in parallel.
     * The value of the first to complete, converted to string, should be used to complete the returned future.
     * [HARD⭐️] if the first completes with error, wait for the second.
     * [HARD⭐️⭐️⭐️] If both in error, complete in error.
     */
    public CompletableFuture<String> p07_fastest() {
        return null;
    }


}
