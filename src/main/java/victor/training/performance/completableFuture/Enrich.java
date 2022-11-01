package victor.training.performance.completableFuture;

import lombok.Value;

import java.util.concurrent.CompletableFuture;

public class Enrich {
    static class A {}
    static class B {}
    static class C {}
    @Value
    static class AB {
        A a;
        B b;
    }
    @Value
    static class ABC {
        A a;
        B b;
        C c;
    }
    interface Dependency {
        CompletableFuture<A> a(int id);
        CompletableFuture<B> b(int id);

        CompletableFuture<B> b1(A a);
        CompletableFuture<C> c(int id);

        CompletableFuture<C> c1(A a);
        CompletableFuture<C> c2(A a, B b);
    }
    protected final Dependency dependency;

    public Enrich(Dependency dependency) {
        this.dependency = dependency;
    }

    // ==================================================================================================

    /**
     * a(id) || b(id) ==> AB(a,b)
     */
    public CompletableFuture<AB> p01_a_par_b(int id) {
        return null;
    }

    // ==================================================================================================

    /**
     * a(id), then b1(a) ==> AB(a,b)
     */
    public CompletableFuture<AB> p02_a_then_b1(int id) {
        return null;
    }

    // ==================================================================================================

    /**
     * a(id), then b1(a) || c1(a) ==> ABC(a,b,c)
     */
    public CompletableFuture<ABC> p03_a_then_b1_par_c1(int id) {
        return null;
    }

    // ==================================================================================================

    /**
     * a(id), then b1(a), then c2(a,b) ==> ABC(a,b,c)
     */
    public CompletableFuture<ABC> p04_a_then_b1_then_c2(int id) {
        return null;
    }
    // ==================================================================================================

    /**
     * a(id) || b(id) || c(id) ==> ABC(a,b,c)
     */
    public CompletableFuture<ABC> p05_a_then_b1_then_c2(int id) {
        return null;
    }
}
