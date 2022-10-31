package victor.training.performance.java8.cf;

import org.jooq.lambda.Unchecked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

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
// Hint: multe solutiie contin cuvantul 'exception'
    /**
     * Log any exception from the future, but leave the exception unchanged.
     */
    public CompletableFuture<String> p01_log() {
        return dependency.call().exceptionally(e -> {
            log.error("Exception occurred: " + e, e);
            throw new RuntimeException(e);
        });
    }

    // ==================================================================================================

    /**
     * Wrap any exception from the future in a new IllegalStateException("Call failed", original).
     */
    public CompletableFuture<String> p02_wrap() {
       return dependency.call().exceptionally(e-> {
           throw new IllegalStateException("Call failed", e);
       });
    }

    // ==================================================================================================

    /**
     * Return "default" on any exception in the future.
     */
    public CompletableFuture<String> p03_defaultValue() {
            return dependency.call().exceptionally(e->"default");
    }

    // ==================================================================================================
    /**
     * Call dependency#backup() on any exception in the future.
     */
    public CompletableFuture<String> p04_defaultFuture() {
        return dependency.call().exceptionally(e -> dependency.backup().join());
    }
    // ==================================================================================================
    /**
     * [HARD⭐️] Call dependency#backup() on any exception in the future (same as previous), but do NOT use .get() or .join().
     * Hint: you'll need to use handle() and thenCompose()
     * Variation: retry?
     */
    public CompletableFuture<String> p05_defaultFutureNonBlocking() {
         return dependency.call().handle((s, err) -> {
             if (err != null) {
                 return dependency.backup();
             } else {
                 return completedFuture(s);
             }
         }).thenCompose(Function.identity()); // TODO retryable
    }

    // ==================================================================================================
    /**
     * Close the resource (Writer) after the future completes
     */
    public CompletableFuture<Void> p06_cleanup() throws IOException {
        Writer writer = new FileWriter("out.txt");
            return dependency.call()
                    .thenAccept(Unchecked.consumer(writer::write)) // Unchecked.consumer converts any exception into a runtime one
                    .whenComplete(Unchecked.biConsumer((v,e) -> writer.close())) // hook: nu afecteaza ce intoarce CF
                    ;
    }
}
