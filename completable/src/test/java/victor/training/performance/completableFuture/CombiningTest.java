package victor.training.performance.completableFuture;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.MethodName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import victor.training.performance.completableFuture.CaptureSystemOutput.OutputCapture;
import victor.training.performance.completableFuture.Combining.Dependency;
import victor.training.performance.completableFuture.util.PerformanceUtil;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.util.concurrent.CompletableFuture.*;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodName.class)
@Timeout(1)
class CombiningTest {
    private static final Logger log = LoggerFactory.getLogger(CombiningTest.class);
    @Mock
    Dependency dependency;
    @InjectMocks
    CombiningSolved workshop;

    @Test
    void p01_transform() throws ExecutionException, InterruptedException {
        Mockito.when(dependency.call()).thenReturn(completedFuture("abc"));
        assertThat(workshop.p01_transform().get()).isEqualTo("ABC");
    }

    @Test
    void p02_chainRun() {
        CompletableFuture<Void> future = new CompletableFuture<>();
        Mockito.when(dependency.task("abc")).thenReturn(future);

        workshop.p02_chainRun("abc");

        Mockito.verify(dependency, Mockito.times(0)).cleanup();
        future.complete(null);
        Mockito.verify(dependency).cleanup();
    }

    @Test
    void p03_chainConsume() throws ExecutionException, InterruptedException {
        CompletableFuture<String> callFuture = new CompletableFuture<>();
        Mockito.when(dependency.call()).thenReturn(callFuture);

        workshop.p03_chainConsume();

        Mockito.verify(dependency, Mockito.times(0)).task("abc");
        callFuture.complete("abc");
        Mockito.verify(dependency).task("abc");
    }

    @Test
    void p04_chainFutures() throws ExecutionException, InterruptedException {
        Mockito.when(dependency.call()).thenReturn(completedFuture("a"));
        Mockito.when(dependency.parseIntRemotely("a")).thenReturn(completedFuture(1));

        assertThat(workshop.p04_chainFutures().get()).isEqualTo(1);
    }

    @Test
    void p05_chainFutures_returnFutureVoid() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = new CompletableFuture<>();
        Mockito.when(dependency.call()).thenReturn(future);
        Mockito.when(dependency.task("a")).thenReturn(completedFuture(null));

        CompletableFuture<Void> resultFuture = workshop.p05_chainFutures_returnFutureVoid();

        assertThat(resultFuture.isDone()).isFalse();
        future.complete("a");
        assertThat(resultFuture.isDone()).isTrue();
    }
    @Test
    void p05_chainFutures_returnFutureVoid_error() throws ExecutionException, InterruptedException {
        Mockito.when(dependency.call()).thenReturn(completedFuture("a"));
        Mockito.when(dependency.task("a")).thenReturn(failedFuture(new IllegalArgumentException()));

        CompletableFuture<Void> resultFuture = workshop.p05_chainFutures_returnFutureVoid();

        assertThatThrownBy(() -> resultFuture.get())
                .hasCauseInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void p06_forkJoin() throws ExecutionException, InterruptedException {
        CompletableFuture<String> callFuture = new CompletableFuture<>();
        CompletableFuture<Void> taskFuture = new CompletableFuture<>();
        Mockito.when(dependency.call()).thenReturn(callFuture);
        Mockito.when(dependency.task("a")).thenReturn(taskFuture);

        CompletableFuture<Void> resultFuture = workshop.p06_all();

        // initially
        Mockito.verify(dependency, Mockito.times(0)).cleanup();
        Mockito.verify(dependency, Mockito.times(0)).task(ArgumentMatchers.anyString());
        assertThat(resultFuture.isDone()).isFalse();

        // after #call() completes
        log.info("Call completes");
        callFuture.completeAsync(()->"a");
        PerformanceUtil.sleepMillis(50); // support parallelization Play
        Mockito.verify(dependency).cleanup();
        Mockito.verify(dependency).task("a");
        assertThat(resultFuture.isDone()).isFalse();

        // after #task() completes
        taskFuture.complete(null);
        PerformanceUtil.sleepMillis(50); // support parallelization Play
        assertThat(resultFuture.isDone()).isTrue();
    }

    @Test
    void p06_forkJoin_inParallel() throws ExecutionException, InterruptedException {
        Mockito.when(dependency.call()).thenReturn(completedFuture("a"));
        Mockito.when(dependency.task("a")).thenAnswer(c -> {
            log.debug("2Start Task");
            PerformanceUtil.sleepMillis(800);
            log.debug("2End Task");
            return completedFuture(null);
        });
        Mockito.doAnswer(c -> {
            log.debug("1Start Cleanup");
            PerformanceUtil.sleepMillis(800);
            log.debug("1End Cleanup");
            return null;
        }).when(dependency).cleanup();

        workshop.p06_all().get();
        // should finish <= 1sec, as per @Timeout(1) on class
    }

    @Test
    void p07_combine() throws ExecutionException, InterruptedException {
        CompletableFuture<String> callFuture = new CompletableFuture<>();
        CompletableFuture<Integer> ageFuture = new CompletableFuture<>();
        Mockito.when(dependency.call()).thenAnswer(x -> {
            log.info("Calling #call");
            return callFuture;
        });
        Mockito.when(dependency.fetchAge()).thenAnswer(x -> {
            log.info("Calling #fetchAge");
            return ageFuture;
        });

        CompletableFuture<String> resultFuture = workshop.p07_combine();

        // initially
        assertThat(resultFuture.isDone()).isFalse();
        // calls complete
        log.info("Complete the calls");
        callFuture.complete("John");
        ageFuture.complete(36);
        assertThat(resultFuture.get()).isEqualTo("John 36");
    }

    @Test
    void p08_fastest_1() throws ExecutionException, InterruptedException {
        Mockito.when(dependency.call()).thenReturn(supplyAsync(() -> "John", delayedExecutor(100, MILLISECONDS)));
        Mockito.when(dependency.fetchAge()).thenReturn(supplyAsync(() -> 36, delayedExecutor(200, MILLISECONDS)));

        assertThat(workshop.p08_fastest().get()).isEqualTo("John");
    }
    @Test
    void p08_fastest_2() throws ExecutionException, InterruptedException {
        Mockito.when(dependency.call()).thenReturn(supplyAsync(() -> "John", delayedExecutor(200, MILLISECONDS)));
        Mockito.when(dependency.fetchAge()).thenReturn(supplyAsync(() -> 36, delayedExecutor(100, MILLISECONDS)));

        assertThat(workshop.p08_fastest().get()).isEqualTo("36");
    }
    @Test
    @Disabled("HARD-CORE⭐️")
    void p08_fastest_2_err() throws ExecutionException, InterruptedException {
        Mockito.when(dependency.call()).thenReturn(supplyAsync(() -> "John", delayedExecutor(200, MILLISECONDS)));
        Mockito.when(dependency.fetchAge()).thenReturn(supplyAsync(() -> {
            throw new TestRootCauseException();
        }, delayedExecutor(100, MILLISECONDS)));

        assertThat(workshop.p08_fastest().get()).isEqualTo("John");
    }
    @Test
    @Disabled("HARD-CORE⭐️️⭐️️⭐️️")
    void p08_fastest_2_err_x_2() throws ExecutionException, InterruptedException {
        Mockito.when(dependency.call()).thenReturn(supplyAsync(() -> {
            throw new TestRootCauseException();
        }, delayedExecutor(200, MILLISECONDS)));
        Mockito.when(dependency.fetchAge()).thenReturn(supplyAsync(() -> {
            throw new TestRootCauseException();
        }, delayedExecutor(100, MILLISECONDS)));

        assertThatThrownBy(() ->workshop.p08_fastest().get());
    }


    @Test
    void p09_fireAndForget_normal() throws ExecutionException, InterruptedException {
        Mockito.when(dependency.call()).thenReturn(completedFuture("abc"));
        Mockito.when(dependency.audit("abc")).thenReturn(completedFuture(null));

        workshop.p09_fireAndForget().get();

        PerformanceUtil.sleepMillis(300);
        Mockito.verify(dependency).audit("abc");
    }
    @Test
    @Timeout(value = 450, unit = MILLISECONDS)
    void p09_fireAndForget_doesNotWait_forAudit() throws ExecutionException, InterruptedException {
        Mockito.when(dependency.call()).thenReturn(completedFuture("abc"));
        Mockito.when(dependency.audit("abc")).thenReturn(supplyAsync(()->null,delayedExecutor(500, MILLISECONDS)));

        workshop.p09_fireAndForget().get();
    }
    @Test
    @CaptureSystemOutput
    void p09_fireAndForget_doesNotFailForAudit_butLogs(OutputCapture outputCapture) throws ExecutionException, InterruptedException {
        Mockito.when(dependency.call()).thenReturn(completedFuture("abc"));
        Mockito.when(dependency.audit("abc")).thenReturn(failedFuture(new RuntimeException("TEST EXCEPTION")));

        workshop.p09_fireAndForget().get();

        Assertions.assertThat(outputCapture.toString()).contains("TEST EXCEPTION");
    }



}