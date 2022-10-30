package victor.training.performance.java8.cf;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.MethodName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import victor.training.performance.java8.cf.Combining.Dependency;
import victor.training.performance.util.PerformanceUtil;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.CompletableFuture.*;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodName.class)
@Timeout(1)
class CombiningTest {
    private static final Logger log = LoggerFactory.getLogger(CombiningTest.class);
    @Mock
    Dependency dependency;
    @InjectMocks
    Combining workshop;
    CombiningSolved workshopSolved;

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
    void p04_flatMap() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = new CompletableFuture<>();
        when(dependency.call()).thenReturn(future);
        when(dependency.task("a")).thenReturn(completedFuture(null));

        CompletableFuture<Void> resultFuture = workshop.p04_flatMap();

        assertThat(resultFuture.isDone()).isFalse();
        future.complete("a");
        assertThat(resultFuture.isDone()).isTrue();
    }

    @Test
    void p05_forkJoin() throws ExecutionException, InterruptedException {
        CompletableFuture<String> callFuture = new CompletableFuture<>();
        CompletableFuture<Void> taskFuture = new CompletableFuture<>();
        when(dependency.call()).thenReturn(callFuture);
        when(dependency.task("a")).thenAnswer(x -> {
            log.info("Calling #task");
            return taskFuture;
        });

        CompletableFuture<Void> resultFuture = workshop.p05_forkJoin();

        // initially
        verify(dependency, times(0)).cleanup();
        verify(dependency, times(0)).task(anyString());
        assertThat(resultFuture.isDone()).isFalse();
        // call completes
        log.info("Call completes");
        callFuture.complete("a");
        verify(dependency).cleanup();
        verify(dependency).task("a");
        assertThat(resultFuture.isDone()).isFalse();
        // task completes
        taskFuture.complete(null);
        assertThat(resultFuture.isDone()).isTrue();
    }

    @Test
    void p06_combine() throws ExecutionException, InterruptedException {
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

        CompletableFuture<String> resultFuture = workshop.p06_combine();

        // initially
        assertThat(resultFuture.isDone()).isFalse();
        // calls complete
        log.info("Complete the calls");
        callFuture.complete("John");
        ageFuture.complete(36);
        assertThat(resultFuture.get()).isEqualTo("John 36");
    }

    @Test
    void p07_fastest_1() throws ExecutionException, InterruptedException {
        when(dependency.call()).thenReturn(supplyAsync(() -> "John", delayedExecutor(100, MILLISECONDS)));
        when(dependency.fetchAge()).thenReturn(supplyAsync(() -> 36, delayedExecutor(200, MILLISECONDS)));

        assertThat(workshop.p07_fastest().get()).isEqualTo("John");
    }
    @Test
    void p07_fastest_2() throws ExecutionException, InterruptedException {
        when(dependency.call()).thenReturn(supplyAsync(() -> "John", delayedExecutor(200, MILLISECONDS)));
        when(dependency.fetchAge()).thenReturn(supplyAsync(() -> 36, delayedExecutor(100, MILLISECONDS)));

        assertThat(workshop.p07_fastest().get()).isEqualTo("36");
    }
    @Test
    @Disabled("HARD-CORE⭐️")
    void p07_fastest_2_err() throws ExecutionException, InterruptedException {
        when(dependency.call()).thenReturn(supplyAsync(() -> "John", delayedExecutor(200, MILLISECONDS)));
        when(dependency.fetchAge()).thenReturn(supplyAsync(() -> {
            throw new TestRootCauseException();
        }, delayedExecutor(100, MILLISECONDS)));

        assertThat(workshop.p07_fastest().get()).isEqualTo("John");
    }
    @Test
    @Disabled("HARD-CORE⭐️️⭐️️⭐️️")
    void p07_fastest_2_err_x_2() throws ExecutionException, InterruptedException {
        when(dependency.call()).thenReturn(supplyAsync(() -> {
            throw new TestRootCauseException();
        }, delayedExecutor(200, MILLISECONDS)));
        when(dependency.fetchAge()).thenReturn(supplyAsync(() -> {
            throw new TestRootCauseException();
        }, delayedExecutor(100, MILLISECONDS)));

        assertThatThrownBy(() ->workshop.p07_fastest().get());
    }
}