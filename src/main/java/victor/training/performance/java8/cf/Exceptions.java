package victor.training.performance.java8.cf;

import org.jooq.lambda.Unchecked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.util.concurrent.CompletableFuture.completedFuture;

public class Exceptions {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    public Exceptions(Dependency dependency) {
        this.dependency = dependency;
    }

    final Dependency dependency;

    interface Dependency {
        CompletableFuture<String> call();
        CompletableFuture<String> backup();
    }

    // ==================================================================================================

    /**
     * Log any exception from the future, but leave the exception unchanged.
     */
    public CompletableFuture<String> p01_log() {
        try {
            return dependency.call();
        } catch (Exception e) {
            log.error("Exception occurred: " + e, e);  // <-- do this on any exception in the future, then delete this USELESS catch
            throw e;
        }
    }

    // ==================================================================================================

    /**
     * Wrap any exception from the future in a new IllegalStateException("Call failed", original).
     */
    public CompletableFuture<String> p02_wrap() {
        try {
            return dependency.call();
        } catch (Exception e) {
            throw new IllegalStateException("Call failed", e); // <-- do this on any exception in the future, then delete this USELESS catch
        }
    }

    // ==================================================================================================

    /**
     * Return "default" on any exception in the future.
     */
    public CompletableFuture<String> p03_defaultValue() {
        try {
            return dependency.call();
        } catch (Exception e) {
            return completedFuture("default"); // <-- do this on any exception in the future, then delete this USELESS catch
        }
    }

    // ==================================================================================================
    /**
     * Call dependency#backup() on any exception in the future.
     */
    public CompletableFuture<String> p04_defaultFuture() {
        try {
            return dependency.call();
        } catch (Exception e) {
            return dependency.backup(); // <-- do this on any exception in the future, then delete this USELESS catch
        }
    }
    // ==================================================================================================
    /**
     * [HARD⭐️] Call dependency#backup() on any exception in the future (same as previous), but do NOT use .get() or .join().
     * Hint: you'll need to use handle() and thenCompose()
     * Variation: retry?
     */
    public CompletableFuture<String> p05_defaultFutureNonBlocking() {
        try {
            return dependency.call();
        } catch (Exception e) {
            return dependency.backup(); // <-- do this on any exception in the future, then delete this USELESS catch
        }
    }

    // ==================================================================================================
    /**
     * Close the resource (Writer) after the future completes
     */
    public CompletableFuture<Void> p06_cleanup() throws IOException {
        try(Writer writer = new FileWriter("out.txt")) {// <-- make sure you close the writer AFTER the CF completes!
            return dependency.call()
                    .thenAccept(Unchecked.consumer(s -> writer.write(s))) // Unchecked.consumer converts any exception into a runtime one
                    ;
        }
    }
}
