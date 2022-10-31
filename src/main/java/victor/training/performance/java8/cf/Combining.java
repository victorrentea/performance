package victor.training.performance.java8.cf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import victor.training.performance.util.PerformanceUtil;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.*;

public class Combining {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    interface Dependency {
        CompletableFuture<String> call();

        CompletableFuture<Void> task(String s); // inseamna ca fct asta NU face defapt nimic cand o chemi, ci doar
        // starteaza o procesare lunga IO nonblocanta

        void cleanup();

        CompletableFuture<Integer> fetchAge();
    }

    final Dependency dependency;

    public Combining(Dependency dependency) {
        this.dependency = dependency;
    }


    // ==================================================================================================

    /**
     * Return the uppercase of the future value, not blocking.
     */
    public CompletableFuture<String> p01_transform() {
        return dependency.call().thenApply(String::toUpperCase); // orice procesare in memory imediata
        // pe rezultatele unui CF se pun in thenApply eg unmarshall la un JSON, parsare de int, adaugare de noi date
        // f(x) {cf.thenApply(mama -> x.withMama(mama))
    }

    // ==================================================================================================

    /**
     * Run dependency#task(s) passing the string provided as parameter, then dependency#cleanup();
     * Hint: completableFuture.then....
     */
    public void p02_chainRun(String s) {
        // ASTEA NU RULEAZA DACA A SARIT EROARE MAI SUS IN LANT
//        dependency.task(s).thenAccept(v -> dependency.cleanup()); // naspa ca oricum nu faci nimic cu v
//        dependency.task(s).thenRun(dependency::cleanup); // tot nu e bun ca vreau sa curat oricum

        // ruleaza cleanup chiar daca task da chix
        dependency.task(s).whenComplete((v,eroare)->dependency.cleanup()); // ~ finally


    }

    // ==================================================================================================

    /**
     * Run dependency#task(s) passing the string provided by the dependency#call(). Do not block (get/join)!
     */
    public void p03_chainConsume() throws InterruptedException, ExecutionException {
        dependency.call().thenAccept(dependency::task);
    }


    // ==================================================================================================

    /**
     * Same as previous, but return a CF< Void > to let the caller know of when the task finishes, and of any exceptions
     */
    public CompletableFuture<Void> p04_flatMap() throws ExecutionException, InterruptedException {
//         dependency.call().thenApply(s -> dependency.task(s)); // CF<CF<Void>>

//        return dependency.call().thenAccept(s -> dependency.task(s));
        return dependency.call().thenCompose(s -> dependency.task(s)); // un fel de flatMap care asteapta sa
        // se termine si CF intors de lambda pana termina CF returnat de functie
    }

    // ==================================================================================================

    /**
     * Launch #call;
     * When it completes launch #task and #cleanup in parallel;
     * Wait for both to complete and then complete the returned future.
     * Not blocking.
     */
    public CompletableFuture<Void> p05_forkJoin() throws ExecutionException, InterruptedException {
        CompletableFuture<String> callFuture = dependency.call();
//        callFuture.whenComplete((s, err) -> {
//            if (err == null) dependency.task(s);
//        });
        CompletableFuture<Void> futureTask = callFuture.thenComposeAsync(s -> {
            log.info("Start task");
            PerformanceUtil.sleepMillis(10);
            log.info("end task");
            return dependency.task(s);
        });
        CompletableFuture<Void> futureCleanup = callFuture.thenRunAsync(() -> {
            log.info("Start cleanup");
            PerformanceUtil.sleepMillis(10);
            log.info("end cleanup");
            dependency.cleanup();
        });

//        return futureTask.thenCombine(futureCleanup, (v1,v2)-> null);
        return CompletableFuture.allOf(futureTask, futureCleanup); // mai sugestiv un pic
    }


    // ==================================================================================================

    /**
     * Launch #call and #fetchAge in parallel. When both complete, combine their values like so:
     * callResult + " " + ageResult
     * and complete the returned future with this value. Don't block.
     */
    public CompletableFuture<String> p06_combine() {
        return dependency.call().thenCombine(dependency.fetchAge(), (c, a) -> c + " " + a);
    }

    // ==================================================================================================

    /**
     * Launch #call and #fetchAge in parallel.
     * The value of the first to complete (ignore the other),
     * converted to string, should be used to complete the returned future.
     * Hint: thenCombine waits for all to complete.
     * Hint#2: Either... or anyOf()
     * [HARD⭐️] if the first completes with error, wait for the second.
     * [HARD⭐️⭐️⭐️] If both in error, complete in error.
     */
    public CompletableFuture<String> p07_fastest() {
        // concurs vreau: pornesc 2, primu care-mi da, ala il iau.
        //        return callFuture.applyToEither(ageFuture, v -> v);
//        return callFuture.applyToEither(ageFuture, Function.identity());

        return anyOf(dependency.call(), dependency.fetchAge())
                .thenApply(Objects::toString);
    }


}
