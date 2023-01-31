package victor.training.performance.completableFuture;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.MethodOrderer.MethodName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import victor.training.performance.completableFuture.CaptureSystemOutput.OutputCapture;
import victor.training.performance.completableFuture.Exceptions.Dependency;
import victor.training.performance.completableFuture.util.NamedThreadFactory;
import victor.training.performance.completableFuture.util.PerformanceUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.*;

import static java.util.concurrent.CompletableFuture.*;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodName.class)
@Timeout(1)
class ExceptionsTest {
    @Mock
    Dependency dependencyMock;

    @InjectMocks
    ExceptionsSolved workshop;

    private static final ExecutorService secondExecutor = Executors.newFixedThreadPool(1, new NamedThreadFactory("second"));

    @Test
    @CaptureSystemOutput
    void p01_log_KO(OutputCapture outputCapture) {
        Mockito.when(dependencyMock.call()).thenReturn(failedFuture(new TestRootCauseException()));

        assertThatThrownBy(() -> workshop.p01_log().get())
                .hasCauseInstanceOf(TestRootCauseException.class);

        Assertions.assertThat(outputCapture.toString())
                .contains(TestRootCauseException.class.getSimpleName());
    }
    @Test
    @CaptureSystemOutput
    void p01_log_OK(OutputCapture outputCapture) {
        Mockito.when(dependencyMock.call()).thenReturn(completedFuture("abc"));

        workshop.p01_log();

        Assertions.assertThat(outputCapture.toString()).isEmpty();
    }

    @Test
    void p02_wrap() {
        Mockito.when(dependencyMock.call()).thenReturn(failedFuture(new TestRootCauseException()));

        CompletableFuture<String> result = workshop.p02_wrap();

        assertThatThrownBy(result::get)
                .hasCauseInstanceOf(IllegalStateException.class) // get() wraps the exception within an ExecutionException
                .hasRootCauseInstanceOf(TestRootCauseException.class) // added original exception as the cause
        ;
    }

    @Test
    void p03_defaultValue_KO() throws ExecutionException, InterruptedException {
        Mockito.when(dependencyMock.call()).thenReturn(failedFuture(new TestRootCauseException()));
        assertThat(workshop.p03_defaultValue().get()).isEqualTo("default");
    }

    @Test
    void p03_defaultValue_OK() throws ExecutionException, InterruptedException {
        Mockito.when(dependencyMock.call()).thenReturn(completedFuture("OK"));
        assertThat(workshop.p03_defaultValue().get()).isEqualTo("OK");
    }


    @Test
    void p04_defaultFuture_OK() throws ExecutionException, InterruptedException {
        Mockito.when(dependencyMock.call()).thenReturn(completedFuture("OK"));
        assertThat(workshop.p04_defaultFuture().get()).isEqualTo("OK");
    }

    @Test
    void p04_defaultFuture_KO() throws ExecutionException, InterruptedException {
        Mockito.when(dependencyMock.call()).thenReturn(failedFuture(new TestRootCauseException()));
        Mockito.when(dependencyMock.backup()).thenReturn(completedFuture("backup"));
        assertThat(workshop.p04_defaultFuture().get()).isEqualTo("backup");
    }

    @Test
    void p05_defaultFutureNonBlocking() throws ExecutionException, InterruptedException {
        // throw in commonPool
        Mockito.when(dependencyMock.call()).thenAnswer(x -> supplyAsync(() -> {
            throw new TestRootCauseException();
        }));
        // provide the backup value on another executor. Using Answer<> to defer the startup of the work (otherwise the CF is already completed when called)
        Mockito.when(dependencyMock.backup()).thenAnswer(x -> supplyAsync(() -> {
            PerformanceUtil.sleepMillis(10);
            return "backup";
        }, secondExecutor));
        // record the thread in which the backup value is emitted
        TestUtils.CaptureThreadName captureThreadName = new TestUtils.CaptureThreadName();

        String result = workshop.p05_defaultFutureNonBlocking()
                .whenComplete(captureThreadName).get();

        assertThat(result).isEqualTo("backup");
        System.out.println("Value emitted in thread " + captureThreadName.getThreadName());
        assertThat(captureThreadName.getThreadName()).contains("second");
    }

    @Test
    void p06_cleanup() throws ExecutionException, InterruptedException, IOException {
        Mockito.when(dependencyMock.call()).thenAnswer(x -> supplyAsync(() -> "abc", delayedExecutor(100, MILLISECONDS)));

        workshop.p06_cleanup().get();

        File file = new File("out.txt");
        assertThat(Files.readString(file.toPath())).isEqualTo("abc");
        assertThat(file.delete()).isTrue();
    }

    @Test
    void p06_cleanup_KO() throws ExecutionException, InterruptedException, IOException {
        Mockito.when(dependencyMock.call()).thenAnswer(x -> supplyAsync(() -> {throw new TestRootCauseException();}, delayedExecutor(100, MILLISECONDS)));

        assertThatThrownBy(() -> workshop.p06_cleanup().get());

        File file = new File("out.txt");
        assertThat(file.delete()).isTrue();
    }


}