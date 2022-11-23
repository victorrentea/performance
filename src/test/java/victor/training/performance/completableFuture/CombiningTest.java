package victor.training.performance.completableFuture;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.MethodName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import victor.training.performance.completableFuture.Combining.Dependency;
import victor.training.performance.util.CaptureSystemOutput;
import victor.training.performance.util.CaptureSystemOutput.OutputCapture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.util.concurrent.CompletableFuture.*;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static victor.training.performance.util.PerformanceUtil.sleepMillis;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodName.class)
@Timeout(1)
class CombiningTest {
    private static final Logger log = LoggerFactory.getLogger(CombiningTest.class);
    @Mock
    Dependency dependency;
    @InjectMocks
    Combining workshop;

    @Test
    void p01_transform() throws ExecutionException, InterruptedException {
        when(dependency.call()).thenReturn(completedFuture("abc"));
        assertThat(workshop.p01_transform().get()).isEqualTo("ABC");
    }

    @Test
    void p02_chainRun() {
        CompletableFuture<Void> future = new CompletableFuture<>();
        when(dependency.task("abc")).thenReturn(future);

        workshop.p02_chainRun("abc");

        verify(dependency, times(0)).cleanup();
        future.complete(null);
        verify(dependency).cleanup();
    }

    @Test
    void p03_chainConsume() throws ExecutionException, InterruptedException {
        CompletableFuture<String> callFuture = new CompletableFuture<>();
        when(dependency.call()).thenReturn(callFuture);

        workshop.p03_chainConsume();

        verify(dependency,times(0)).task("abc");
        callFuture.complete("abc");
        verify(dependency).task("abc");
    }

    @Test
    void p04_chainFutures() throws ExecutionException, InterruptedException {
        when(dependency.call()).thenReturn(completedFuture("a"));
        when(dependency.parseIntRemotely("a")).thenReturn(completedFuture(1));

        assertThat(workshop.p04_chainFutures().get()).isEqualTo(1);
    }

    @Test
    void p05_chainFutures_returnFutureVoid() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = new CompletableFuture<>();
        when(dependency.call()).thenReturn(future);
        when(dependency.task("a")).thenReturn(completedFuture(null));

        CompletableFuture<Void> resultFuture = workshop.p05_chainFutures_returnFutureVoid();

        assertThat(resultFuture.isDone()).isFalse();
        future.complete("a");
        assertThat(resultFuture.isDone()).isTrue();
    }

    @Test
    void p06_forkJoin_inParallel() throws ExecutionException, InterruptedException {
        when(dependency.call()).thenReturn(completedFuture("a"));
        when(dependency.task("a")).thenAnswer(c -> {
            log.debug("2Start Task");
            sleepMillis(800);
            log.debug("2End Task");
            return completedFuture(null);
        });
        doAnswer(c -> {
            log.debug("1Start Cleanup");
            sleepMillis(800);
            log.debug("1End Cleanup");
            return null;
        }).when(dependency).cleanup();

        workshop.p06_all().get();
        // should finish <= 1sec, as per @Timeout(1) on class
    }

    @Test
    void p06_forkJoin() throws ExecutionException, InterruptedException {
        CompletableFuture<String> callFuture = new CompletableFuture<>();
        CompletableFuture<Void> taskFuture = new CompletableFuture<>();
        when(dependency.call()).thenReturn(callFuture);
        when(dependency.task("a")).thenAnswer(c -> {
            log.debug("2Pe ce thread sunt aici ?");
            sleepMillis(100);
            log.debug("2Dupa");
            return taskFuture;
        });
        doAnswer(c -> {
            log.debug("1Pe ce thread sunt aici ?");
            sleepMillis(100);
            log.debug("1Dupa");
            return null;
        }).when(dependency).cleanup();


        CompletableFuture<Void> resultFuture = workshop.p06_all();

        // initially
        verify(dependency, times(0)).cleanup();
        verify(dependency, times(0)).task(anyString());
        assertThat(resultFuture.isDone()).isFalse();

        // after #call() completes
        log.info("Call completes");
        callFuture.completeAsync(()->"a");
        sleepMillis(50); // support parallelization Play
        verify(dependency).cleanup();
        verify(dependency).task("a");
        assertThat(resultFuture.isDone()).isFalse();

        // after #task() completes
        taskFuture.complete(null);
        sleepMillis(50); // support parallelization Play
        assertThat(resultFuture.isDone()).isTrue();
    }

    @Test
    void p07_combine() throws ExecutionException, InterruptedException {
        CompletableFuture<String> callFuture = new CompletableFuture<>();
        CompletableFuture<Integer> ageFuture = new CompletableFuture<>();
        when(dependency.call()).thenAnswer(x -> {
            log.info("Calling #call");
            return callFuture;
        });
        when(dependency.fetchAge()).thenAnswer(x -> {
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
        when(dependency.call()).thenReturn(supplyAsync(() -> "John", delayedExecutor(100, MILLISECONDS)));
        when(dependency.fetchAge()).thenReturn(supplyAsync(() -> 36, delayedExecutor(200, MILLISECONDS)));

        assertThat(workshop.p08_fastest().get()).isEqualTo("John");
    }
    @Test
    void p08_fastest_2() throws ExecutionException, InterruptedException {
        when(dependency.call()).thenReturn(supplyAsync(() -> "John", delayedExecutor(200, MILLISECONDS)));
        when(dependency.fetchAge()).thenReturn(supplyAsync(() -> 36, delayedExecutor(100, MILLISECONDS)));

        assertThat(workshop.p08_fastest().get()).isEqualTo("36");
    }
    @Test
    @Disabled("HARD-CORE⭐️")
    void p08_fastest_2_err() throws ExecutionException, InterruptedException {
        when(dependency.call()).thenReturn(supplyAsync(() -> "John", delayedExecutor(200, MILLISECONDS)));
        when(dependency.fetchAge()).thenReturn(supplyAsync(() -> {
            throw new TestRootCauseException();
        }, delayedExecutor(100, MILLISECONDS)));

        assertThat(workshop.p08_fastest().get()).isEqualTo("John");
    }
    @Test
    @Disabled("HARD-CORE⭐️️⭐️️⭐️️")
    void p08_fastest_2_err_x_2() throws ExecutionException, InterruptedException {
        when(dependency.call()).thenReturn(supplyAsync(() -> {
            throw new TestRootCauseException();
        }, delayedExecutor(200, MILLISECONDS)));
        when(dependency.fetchAge()).thenReturn(supplyAsync(() -> {
            throw new TestRootCauseException();
        }, delayedExecutor(100, MILLISECONDS)));

        assertThatThrownBy(() ->workshop.p08_fastest().get());
    }


    @Test
    void p09_fireAndForget_normal() throws ExecutionException, InterruptedException {
        when(dependency.call()).thenReturn(completedFuture("abc"));
        when(dependency.audit("abc")).thenReturn(completedFuture(null));

        workshop.p09_fireAndForget().get();

        sleepMillis(300);
        verify(dependency).audit("abc");
    }
    @Test
    @Timeout(value = 450, unit = MILLISECONDS)
    void p09_fireAndForget_doesNotWait_forAudit() throws ExecutionException, InterruptedException {
        when(dependency.call()).thenReturn(completedFuture("abc"));
        when(dependency.audit("abc")).thenReturn(supplyAsync(()->null,delayedExecutor(500, MILLISECONDS)));

        workshop.p09_fireAndForget().get();
    }
    @Test
    @CaptureSystemOutput
    void p09_fireAndForget_doesNotFailForAudit_butLogs(OutputCapture outputCapture) throws ExecutionException, InterruptedException {
        when(dependency.call()).thenReturn(completedFuture("abc"));
        when(dependency.audit("abc")).thenReturn(failedFuture(new RuntimeException("TEST EXCEPTION")));

        workshop.p09_fireAndForget().get();

        assertThat(outputCapture.toString()).contains("TEST EXCEPTION");
    }



}