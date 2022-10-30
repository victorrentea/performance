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

public class ExceptionsSolved extends Exceptions {
    private static final Logger log = LoggerFactory.getLogger(ExceptionsSolved.class);

    public ExceptionsSolved(Dependency dependency) {
        super(dependency);
    }

    public CompletableFuture<String> p01_log() {
        return dependency.call()
                .whenComplete((r, e) -> {
                    if (e != null) log.error("Exception occurred: " + e, e);
                });
    }

    public CompletableFuture<String> p02_wrap() {
        return dependency.call()
                .exceptionally(e -> {
                    throw new IllegalStateException("Call failed", e);
                });
    }

    public CompletableFuture<String> p03_defaultValue() {
        return dependency.call()
                .exceptionally(e -> "default");
    }

    public CompletableFuture<String> p04_defaultFuture() {
        return dependency.call()
                .exceptionally(e -> {
                    try {
                        return dependency.backup().get();
                    } catch (InterruptedException | ExecutionException ex) {
                        throw new RuntimeException(ex);
                    }
                });
    }

    public CompletableFuture<String> p05_defaultFutureNonBlocking() {
        return dependency.call()
                .handle((v, e) -> {
                    if (v != null) {
                        return completedFuture(v);
                    } else {
                        return dependency.backup();
                    }
                })
                .thenCompose(i -> i);
    }

    public CompletableFuture<Void> p06_cleanup() throws IOException {
        Writer writer = new FileWriter("out.txt");
        return dependency.call()
                .thenAccept(Unchecked.consumer(s -> writer.write(s))) // this converts any exception into a runtime one
                .whenComplete(Unchecked.biConsumer((v, t) -> writer.close()))
                ;
    }
}
