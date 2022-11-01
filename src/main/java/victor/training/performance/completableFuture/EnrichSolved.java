package victor.training.performance.completableFuture;

import java.util.concurrent.CompletableFuture;

public class EnrichSolved extends Enrich {
    public EnrichSolved(Dependency dependency) {
        super(dependency);
    }

    // ==================================================================================================

    /**
     * a(id) || b(id) ==> AB(a,b)
     */
    public CompletableFuture<AB> p01_a_par_b(int id) {
        return dependency.a(id).thenCombine(dependency.b(id), AB::new);
    }

    // ==================================================================================================

    /**
     * a(id), then b1(a) ==> AB(a,b)
     */
    public CompletableFuture<AB> p02_a_then_b1(int id) {
        return dependency.a(id).thenCompose(a -> dependency.b1(a).thenApply(b -> new AB(a,b)));
    }

    // ==================================================================================================

    /**
     * a(id), then b1(a) || c1(a) ==> ABC(a,b,c)
     */
    public CompletableFuture<ABC> p03_a_then_b1_par_c1(int id) {
        CompletableFuture<A> futureA = dependency.a(id);
        CompletableFuture<B> futureB = futureA.thenComposeAsync(a -> dependency.b1(a));
        CompletableFuture<C> futureC = futureA.thenComposeAsync(a -> dependency.c1(a));
        return futureB.thenCombine(futureC, (b, c) -> new ABC(futureA.join(), b, c));
    }

    // ==================================================================================================

    /**
     * a(id), then b1(a), then c2(a,b) ==> ABC(a,b,c)
     */
    public CompletableFuture<ABC> p04_a_then_b1_then_c2(int id) {
        CompletableFuture<A> futureA = dependency.a(id);
        CompletableFuture<B> futureB = futureA.thenCompose(a -> dependency.b1(a));
        CompletableFuture<C> futureC = futureB.thenCompose(b -> dependency.c2(futureA.join(), b));
        return futureC.thenApply(c -> new ABC(futureA.join(), futureB.join(), c));
    }
    // ==================================================================================================

    /**
     * a(id) || b(id) || c(id) ==> ABC(a,b,c)
     */
    public CompletableFuture<ABC> p05_a_then_b1_then_c2(int id) {
        CompletableFuture<A> futureA = dependency.a(id);
        CompletableFuture<B> futureB = dependency.b(id);
        CompletableFuture<C> futureC = dependency.c(id);
        return CompletableFuture.allOf(futureA, futureB, futureC)
                .thenApply(v -> new ABC(futureA.join(), futureB.join(), futureC.join()));
    }
}
