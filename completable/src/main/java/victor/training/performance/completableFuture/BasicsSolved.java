package victor.training.performance.completableFuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

import static java.util.concurrent.CompletableFuture.*;

public class BasicsSolved extends Basics {

    public CompletableFuture<String> p01_completed() {
        return completedFuture("Hi");
    }

    public CompletableFuture<String> p02_failed(boolean failed) {
        if (failed) {
            return failedFuture(new IllegalArgumentException());
        } else {
            return completedFuture("Hi");
        }
    }

    public String p03_join(CompletableFuture<String> future) {
        return future.join();
    }

    public String p04_joinException(CompletableFuture<String> future) {
        try {
            return future.join();
        } catch (CompletionException e) {
            return e.getCause().getMessage();
        }
    }

    public String p05_get(CompletableFuture<String> future) throws InterruptedException, ExecutionException {
        try {
            return future.get();
        } catch (ExecutionException e) {
            return e.getCause().getMessage();
        }
    }


    public void p06_run() {
        CompletableFuture.runAsync(() -> log.info("Hi"));
    }

    public CompletableFuture<String> p07_supply() {
        return supplyAsync(() -> Thread.currentThread().getName());
    }

    public void p08_accept(CompletableFuture<String> future) {
        future.thenAccept(s -> System.out.println(s));
    }


}
