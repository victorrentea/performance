package victor.training.performance.completableFuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CombiningSolved extends Combining {
    public CombiningSolved(Dependency dependency) {
        super(dependency);
    }

    public CompletableFuture<String> p01_transform() {
        return dependency.call().thenApply(s -> s.toUpperCase());
    }

    public void p02_chainRun(String s) {
        dependency.task(s).thenRun(() -> dependency.cleanup());
    }

    public void p03_chainConsume() throws InterruptedException {
        dependency.call().thenAccept(s -> dependency.task(s));
    }

    public CompletableFuture<Void> p05_chainFutures_returnFutureVoid() {
        // CF<CF<Void>>  << wow! too much wrapping
        // dependency.call().thenApply(s -> dependency.task(s));

        // the returned CF terminates too early when task has only started!
        // return dependency.call().thenAccept(s -> dependency.task(s));

        // the returned CF terminates when the composed CF (returned by the lambda) finishes
        return dependency.call().thenCompose(s -> dependency.task(s));
    }

    public CompletableFuture<Integer> p04_chainFutures() {
        return dependency.call().thenCompose(s -> dependency.parseIntRemotely(s));
    }

    public CompletableFuture<Void> p06_all() {
        CompletableFuture<String> callFuture = dependency.call();
        return CompletableFuture.allOf(
                callFuture.thenCompose(s -> dependency.task(s)),
                callFuture.thenRun(() -> dependency.cleanup()));
    }

    public CompletableFuture<String> p07_combine() {
        return dependency.call().thenCombine(dependency.fetchAge(),
                (c, a) -> c + " " + a);
    }

    public CompletableFuture<String> p08_fastest() {
        return dependency.call().applyToEither(dependency.fetchAge().thenApply(i -> i.toString()), a -> a);
        //        return anyOf(dependency.call(), dependency.fetchAge().thenApply(i -> i.toString())).thenApply(o -> (String) o);
    }
    // https://stackoverflow.com/questions/33913193/completablefuture-waiting-for-first-one-normally-return


    public CompletableFuture<String> p09_fireAndForget() throws ExecutionException, InterruptedException {
        return dependency.call()
                .whenComplete((s, err) -> {
                    if (s != null) dependency.audit(s).exceptionally(e-> {
                        log.error("ERROR", e);
                        return null;
                    });
                });
    }
}
