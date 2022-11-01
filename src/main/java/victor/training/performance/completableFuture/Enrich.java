package victor.training.performance.completableFuture;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.util.concurrent.CompletableFuture.completedFuture;

public class Enrich {
    @AllArgsConstructor
    static class A {public final String a;}
    @AllArgsConstructor
    static class B {public final String b;}
    @AllArgsConstructor
    static class C {public final String c;}
    @Value
    static class AB {
        public A a;
        public B b;
    }
    @Value
    static class ABC {
        public A a;
        public B b;
        public C c;
    }
    interface Dependency {
        CompletableFuture<A> a(int id);
        CompletableFuture<B> b(int id);

        CompletableFuture<B> b1(A a);
        CompletableFuture<C> c(int id);

        CompletableFuture<C> c1(A a);
        CompletableFuture<C> c2(A a, B b);

        CompletableFuture<A> saveA(A a);
        CompletableFuture<Void> auditA(A a1, A a0);
    }
    protected final Dependency dependency;

    public Enrich(Dependency dependency) {
        this.dependency = dependency;
    }

    //  😇 😇 😇 😇 😇 😇 😇 😇 😇 😇 😇 😇 😇 😇 😇 😇 😇 😇 😇 😇 😇
    // ⚠️ ATTENTION ⚠️ ENTERING HEAVEN
    //
    // ALL THE NETWORK CALLS HAVE BEEN ALREADY CAREFULLY WRAPPED IN NON-BLOCKING FUNCTIONS
    //   eg. relying on WebClient or reactive drivers (R2DBC, reactive kafka, cassandra)
    // NO FUNCTION EVER BLOCKS ANY THREAD ANYMORE
    // ********************************


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
    public CompletableFuture<ABC> p05_a_b_c(int id) {
        return null;
    }

    // ==================================================================================================

    /**
     * a0 = a(id), b0 = b1(a0), c0 = c1(a0)
     * --
     * a1=logic(a0,b0,c0)
     * --
     * saveA(a1)
     * trackA(a1,a0) ;
     *
     * [HARD] the flow should NOT wait for trackA() to complete; but any errors occurred in trackA() should be logged
     * Play: propagate an Immutable Context
     */
    public CompletableFuture<Void> p06_complexFlow(int id) throws ExecutionException, InterruptedException {
        A a0 = dependency.a(id).get();
        B b0 = dependency.b1(a0).get();
        C c0 = dependency.c1(a0).get();
        A a1 = logic(a0, b0, c0);
        dependency.saveA(a1).get();
        dependency.auditA(a1, a0); // don't wait for this to complete (fire-and-forget)
        return completedFuture(null);
    }

    public A logic(A a, B b, C c) {
        A a1 = new A(a.a + b.b.toUpperCase() + c.c);
        return a1;
    }
}
