package victor.training.performance.java8.cf;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static java.util.concurrent.CompletableFuture.allOf;
import static java.util.concurrent.CompletableFuture.completedFuture;

public class Enrich {
    static class A {
        public String a;
        public A() {}

        public A(String a) {
            this.a = a;
        }
    }

    static class B {
        public String b;
    }

    static class C {
        public String c;
    }

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

        CompletableFuture<A> saveA(A a);

        CompletableFuture<Void> trackA(A a1, A a0);
    }

    protected final Dependency dependency;

    public Enrich(Dependency dependency) {
        this.dependency = dependency;
    }


    // ATENTIE ⚠️ ALL THE NETWORK CALLS HAVE BEEN ALREADY CONVERTED TO NON-BLOCKING
    // THIS IS HEAVEN. nu mai blocam nimic (pe sub vor folosi un driver reactiv / WebClient sa vb cu exterioru)
    // ==================================================================================================

    /**
     * a(id) || b(id) ==> AB(a,b)
     */
    public CompletableFuture<AB> p01_a_par_b(int id) {
        // REGULA: daca in java ai CHEMAT functia care ti-a dat CF, acel request de retea DEJA A FOST TRIMIS cand fct ti-a dat CF.
        return dependency.a(id).thenCombine(dependency.b(id), (a, b) -> new AB(a, b));
    }

    // ==================================================================================================

    /**
     * a(id), then b1(a) ==> AB(a,b)
     */
    public CompletableFuture<AB> p02_a_then_b1(int id) {
        //         return dependency.a(id).thenApply(a -> dependency.b1(a).thenApply(b->new AB(a,b)))
        //                 .thenCompose(Function.identity()); // thenCompose(identity()) - evita

        // orizontal
        //        return dependency.a(id).thenCompose(a -> dependency.b1(a).thenApply(b -> new AB(a, b)));

        // vertical ⭐️
        CompletableFuture<A> fa = dependency.a(id); // 1 req se face, ori de cate ori chainuiesti din fa
        CompletableFuture<B> fb = fa.thenCompose(a1 -> dependency.b1(a1));
        return fa.thenCombine(fb, (a, b) -> new AB(a, b));

    }


    // ==================================================================================================

    /**
     * a(id), then b1(a) || c1(a) ==> ABC(a,b,c)
     */
    public CompletableFuture<ABC> p03_a_then_b1_par_c1(int id) {
        // verticala - multe variabile
        //        CompletableFuture<A> fa = dependency.a(id);
        //        CompletableFuture<B> fb = fa.thenCompose(a -> dependency.b1(a));
        //        CompletableFuture<C> fc = fa.thenCompose(a -> dependency.c1(a));
        //        CompletableFuture<AB> fab = fa.thenCombine(fb, (a, b) -> new AB(a, b));
        //        return fab.thenCombine(fc, (ab, c) -> new ABC(ab.a, ab.b, c));

        // mijloc a > b > serial si tin mult memoria ocupata, prea mult...
        // la fiecare pas IMBOGATESTI ce aveai deja.
        return dependency.a(id)
                .thenCompose(a -> dependency.b1(a)
                        .thenApply(b -> new AB(a, b)))
                .thenCompose(ab -> dependency.c1(ab.a)
                        .thenApply(c -> new ABC(ab.a, ab.b, c)));

        // orizontal (prea geek)
        //        return dependency.a(id).thenCompose(a -> dependency.b1(a).thenCombine(dependency.c1(a), (b,c)-> new ABC(a,b,c)));
    }
    // ==================================================================================================

    /**
     * a(id), then b1(a), then c2(a,b) ==> ABC(a,b,c)
     */
    public CompletableFuture<ABC> p04_a_then_b1_then_c2(int id) {
        return dependency.a(id)
                .thenCompose(a -> dependency.b1(a)
                        .thenApply(b -> new AB(a, b)))
                .thenCompose(ab -> dependency.c2(ab.a, ab.b)
                        .thenApply(c -> new ABC(ab.a, ab.b, c)));
    }
    // ==================================================================================================

    /**
     * a(id) || b(id) || c(id) ==> ABC(a,b,c)
     */
//    public CompletableFuture<ABC> p05_a_then_b1_then_c2(int id) {
//        CompletableFuture<A> fa = dependency.a(id);
//        CompletableFuture<B> fb = dependency.b(id);
//        CompletableFuture<C> fc = dependency.c(id);
//
//        return allOf(fa, fb, fc).thenApply(v -> new ABC(fa.join(), fb.join(), fc.join()));
//    }
    // cu context pattern


    /**
     * a0 = a(id), b0 = b1(a0), c0 = c2(a0, b0)
     * --
     * a1=logic(a0,b0,c0)
     * --
     * trackA(a1,a0) // telemetrie
     * saveA(a1)
     * <p>
     * Play: propagate an Immutable Context
     */
    @GetMapping
    public CompletableFuture<Void> playtikaPlays(int id) {


        CompletableFuture<UC3251Context> futureContext = dependency.a(id)
                .thenApply(a -> new UC3251Context().withA(a))
                .thenCombine(dependency.b(id), UC3251Context::withB)
                .thenCombine(dependency.c(id), UC3251Context::withC)
                .thenApply(this::logica);

        // fire and forget. putin importanta
        futureContext.thenAccept(context -> dependency.trackA(context.getNewA(), context.getA()))
//                .exceptionally(e-> log.error(e))
        ;
        CompletableFuture<Void> fs = futureContext.thenAccept(c -> dependency.saveA(c.getNewA()));
        return fs;
    // 2 probleme pt care nu vreau allOf(trackFuture, si saveFuture):
        // 1) track imi poate returna eroare -> client
        // 2) track ma face sa astept catre client
    }

    private UC3251Context logica(UC3251Context sacosa) { // tot ce-am adunat de pe la altii
        A newA = new A(sacosa.getA().a + sacosa.getB().b + sacosa.getC().c);
        return sacosa.withNewA(newA);
    }

    // ==================================================================================================

    /**
     * a0 = a(id), b0 = b1(a0), c0 = c2(a0, b0)
     * --
     * a1=logic(a0,b0,c0)
     * --
     * trackA(a1,a0) // telemetrie
     * saveA(a1)
     * <p>
     * Play: propagate an Immutable Context
     */
    public CompletableFuture<Void> p06_complexFlow(int id) {
        return null;
    }

    public A logic(A a, B b, C c) {
        A aUpdated = new A();
        aUpdated.a = a.a + b.b + c.c;
        return aUpdated;
    }
}

@Value
@AllArgsConstructor
class UC3251Context {
    @With
    Enrich.A a;
    @With
    Enrich.B b;
    @With
    Enrich.C c;
    @With
    Enrich.A newA;
    public UC3251Context() {
        this(null, null, null, null);
    }
}
