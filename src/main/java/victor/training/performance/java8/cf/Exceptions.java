package victor.training.performance.java8.cf;

import lombok.RequiredArgsConstructor;
import org.jooq.lambda.Unchecked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.util.concurrent.CompletableFuture.completedFuture;

@RequiredArgsConstructor
public class Exceptions {
    private static final Logger log = LoggerFactory.getLogger(Exceptions.class);

    interface Dependency {
        CompletableFuture<String> call();
        CompletableFuture<String> backup();
    }

    private final Dependency dependency;


    // ==================================================================================================

    /**
     * Log any exception from the future, but leave the exception unchanged.
     */
    public CompletableFuture<String> p01_log() {
        try {
            return dependency.call()
                    .whenComplete((r, e) -> log.error("Exception occurred: " + e, e));
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
            return dependency.call()
                    .exceptionally(e -> {
                        throw new IllegalStateException("Call failed", e);
                    });
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
            return dependency.call()
                    .exceptionally(e -> "default");
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
            return dependency.call()
                    .exceptionally(e -> {
                        try {
                            return dependency.backup().get();
                        } catch (InterruptedException | ExecutionException ex) {
                            throw new RuntimeException(ex);
                        }
                    });
        } catch (Exception e) {
            return dependency.backup(); // <-- do this on any exception in the future, then delete this USELESS catch
        }
    }
    // ==================================================================================================
    /**
     * [HARD⭐️] Call dependency#backup() on any exception in the future (same as previous), but do NOT use .get() or .join().
     * Hint: you'll need to use handle() and thenCompose()
     */
    public CompletableFuture<String> p05_defaultFutureNonBlocking() {
        try {
            return dependency.call()
                    .handle((v,e) -> {
                        if (v!= null) {
                            return completedFuture(v);
                        } else {
                            return dependency.backup();
                        }
                    })
                    .thenCompose(i -> i);
        } catch (Exception e) {
            return dependency.backup(); // <-- do this on any exception in the future, then delete this USELESS catch
        }
    }

    // ==================================================================================================
    /**
     * Close the resource (Writer) after the future completes
     */
    public CompletableFuture<Void> p06_cleanup() throws IOException {
        Writer writer = new FileWriter("out.txt");
//        try(Writer writer = new FileWriter("out.txt")) {// <-- make sure you close the writer AFTER the CF completes!
            return dependency.call()
                    .thenAccept(Unchecked.consumer(s -> writer.write(s))) // this converts any exception into a runtime one
                    .whenComplete(Unchecked.biConsumer((v, t) -> writer.close()))
                    ;
//        }
    }
}
