package victor.training.performance.java8.cf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CombiningSolved extends Combining{
    public CombiningSolved(Dependency dependency) {
        super(dependency);
    }

    public CompletableFuture<String> p01_transform() {
        return dependency.call().thenApply(s -> s.toUpperCase());
    }

      public void p02_chainRun(String s) {
        dependency.task(s).thenRun(()->dependency.cleanup());
    }

    public void p03_chainConsume() {
         dependency.call().thenAccept(s -> dependency.task(s));
    }

   public CompletableFuture<Void> p04_flatMap() {
        return dependency.call().thenCompose(s->dependency.task(s));
    }

    public CompletableFuture<Void> p05_forkJoin() throws ExecutionException, InterruptedException {
        CompletableFuture<String> callFuture = dependency.call();
        return CompletableFuture.allOf(
                callFuture.thenCompose(s -> dependency.task(s)),
                callFuture.thenRun(() -> dependency.cleanup()));
    }

    public CompletableFuture<String> p06_combine() throws ExecutionException, InterruptedException {
        return dependency.call().thenCombine(dependency.fetchAge(),
                (c, a) -> c + " " + a);
    }

    public CompletableFuture<String> p07_fastest() throws ExecutionException, InterruptedException {
        return dependency.call().applyToEither(dependency.fetchAge().thenApply(i -> i.toString()), a -> a);
//        return anyOf(dependency.call(), dependency.fetchAge().thenApply(i -> i.toString())).thenApply(o -> (String) o);
    }


}
