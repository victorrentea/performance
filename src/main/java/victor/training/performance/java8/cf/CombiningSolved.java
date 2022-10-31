package victor.training.performance.java8.cf;

import java.util.concurrent.CompletableFuture;

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

    public CompletableFuture<Void> p04_chainFutures() {
        return dependency.call().thenCompose(s -> dependency.task(s));
    }

    public CompletableFuture<Void> p05_all() {
        CompletableFuture<String> callFuture = dependency.call();
        return CompletableFuture.allOf(
                callFuture.thenCompose(s -> dependency.task(s)),
                callFuture.thenRun(() -> dependency.cleanup()));
    }

    public CompletableFuture<String> p06_combine() {
        return dependency.call().thenCombine(dependency.fetchAge(),
                (c, a) -> c + " " + a);
    }

    public CompletableFuture<String> p07_fastest() {
        return dependency.call().applyToEither(dependency.fetchAge().thenApply(i -> i.toString()), a -> a);
        //        return anyOf(dependency.call(), dependency.fetchAge().thenApply(i -> i.toString())).thenApply(o -> (String) o);
    }
    // https://stackoverflow.com/questions/33913193/completablefuture-waiting-for-first-one-normally-return


}
