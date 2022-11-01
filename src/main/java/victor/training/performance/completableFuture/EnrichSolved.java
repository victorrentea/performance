package victor.training.performance.completableFuture;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;
import lombok.With;

import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.allOf;
import static java.util.concurrent.CompletableFuture.completedFuture;

public class EnrichSolved extends Enrich {
    public EnrichSolved(Dependency dependency) {
        super(dependency);
    }

    public CompletableFuture<AB> p01_a_par_b(int id) {
        return dependency.a(id).thenCombine(dependency.b(id), AB::new);
    }

    public CompletableFuture<AB> p02_a_then_b1(int id) {
        return dependency.a(id).thenCompose(a -> dependency.b1(a).thenApply(b -> new AB(a,b)));
    }


    public CompletableFuture<ABC> p03_a_then_b1_par_c1(int id) {
//        CompletableFuture<A> futureA = dependency.a(id);
//        CompletableFuture<B> futureB = futureA.thenComposeAsync(a -> dependency.b1(a));
//        CompletableFuture<C> futureC = futureA.thenComposeAsync(a -> dependency.c1(a));
//        return futureB.thenCombine(futureC, (b, c) -> new ABC(futureA.join(), b, c)); // Cons: join()

        return dependency.a(id)
                .thenCompose(a -> dependency.b1(a).thenCombine(dependency.c1(a), (b,c) -> new ABC(a,b,c)));
    }


    public CompletableFuture<ABC> p04_a_then_b1_then_c2(int id) {
        // vertical solution
//        CompletableFuture<A> futureA = dependency.a(id);
//        CompletableFuture<B> futureB = futureA.thenCompose(a -> dependency.b1(a));
//        CompletableFuture<C> futureC = futureB.thenCompose(b -> dependency.c2(futureA.join(), b));
//        return futureC.thenApply(c -> new ABC(futureA.join(), futureB.join(), c)); // CONS: join

        // horizontal solution (chaining incremental accumulating results)
        return dependency.a(id)
                .thenCompose(a -> dependency.b1(a)
                        .thenApply(b -> new AB(a, b)))
                .thenCompose(ab -> dependency.c2(ab.a, ab.b)
                        .thenApply(c -> new ABC(ab.a, ab.b, c)));
    }

    public CompletableFuture<ABC> p05_a_b_c(int id) {
        CompletableFuture<A> futureA = dependency.a(id);
        CompletableFuture<B> futureB = dependency.b(id);
        CompletableFuture<C> futureC = dependency.c(id);
        return CompletableFuture.allOf(futureA, futureB, futureC)
                .thenApply(v -> new ABC(futureA.join(), futureB.join(), futureC.join()));
    }

    public CompletableFuture<Void> p06_complexFlow(int id) {
//        CompletableFuture<A> fa = dependency.a(id);
//        CompletableFuture<B> fb = fa.thenCompose(dependency::b1);
//        CompletableFuture<C> fc = fa.thenCompose(dependency::c1);
//        CompletableFuture<A> fa1 = allOf(fa, fb, fc).thenApply(v -> logic(fa.join(), fb.join(), fc.join()));
//        fa1.thenAccept(a1 -> dependency.auditA(a1, fa.join()));
//        return fa1.thenAccept(a1 -> dependency.saveA(a1));


        return dependency.a(id)
            .thenApply(a -> new MyContext().withA(a))

            // *** sequential
            // .thenCompose(context -> dependency.b1(context.a).thenApply(context::withB)
            // .thenCompose(context -> dependency.c1(context.a).thenApply(context::withC))

            // *** parallel
             .thenCompose(context->
                     dependency.b1(context.a).thenCombine(
                     dependency.c1(context.a), (b,c)->context.withB(b).withC(c)))

            .thenApply(context -> context.withA1(logic(context.a, context.b, context.c)))

            .thenCompose(context -> dependency.saveA(context.a1).thenApply(a -> context))

            .whenComplete((context, e__) -> {
                if (context != null) dependency.auditA(context.a1, context.a)
                        .whenComplete((v, err)-> {
                            if (err != null) {
                                err.printStackTrace();
                            }
                        });
            })
            .thenApply(context -> null);
    }
    @Data
    @AllArgsConstructor
    @With
    private static class MyContext {
        public final A a;
        public final B b;
        public final C c;
        public final A a1;
        public MyContext() {
            this(null, null, null, null);
        }
    }
}
