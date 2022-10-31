package victor.training.performance.java8.cf;

import org.junit.jupiter.api.MethodOrderer.MethodName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import victor.training.performance.java8.cf.Exceptions.Dependency;
import victor.training.performance.java8.cf.TestUtils.CaptureThreadName;
import victor.training.performance.util.CaptureSystemOutput;
import victor.training.performance.util.CaptureSystemOutput.OutputCapture;
import victor.training.performance.util.NamedThreadFactory;
import victor.training.performance.util.PerformanceUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.concurrent.CompletableFuture.*;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodName.class)
@Timeout(1)
class ExceptionsTest {
    @Mock
    Dependency dependencyMock;

    @InjectMocks
    Exceptions workshop;

    private static final ExecutorService secondExecutor = Executors.newFixedThreadPool(1, new NamedThreadFactory("second"));

    @Test
    @CaptureSystemOutput
    void p01_log_KO(OutputCapture outputCapture) {
        when(dependencyMock.call()).thenReturn(CompletableFuture.failedFuture(new TestRootCauseException()));

        CompletableFuture<String> returnedFuture = workshop.p01_log();

        assertThat(outputCapture.toString())
                .contains("Exception occurred")
                .contains(TestRootCauseException.class.getSimpleName());
        assertThatThrownBy(returnedFuture::get)
                .hasRootCauseInstanceOf(TestRootCauseException.class);
    }
    @Test
    @CaptureSystemOutput
    void p01_log_OK(OutputCapture outputCapture) {
        when(dependencyMock.call()).thenReturn(CompletableFuture.completedFuture("abc"));

        workshop.p01_log();

        assertThat(outputCapture.toString()).isEmpty();
    }

    @Test
    void p02_wrap() {
        when(dependencyMock.call()).thenReturn(CompletableFuture.failedFuture(new TestRootCauseException()));

        CompletableFuture<String> result = workshop.p02_wrap();

        assertThatThrownBy(result::get)
                .hasCauseInstanceOf(IllegalStateException.class) // get() wraps the exception within an ExecutionException
                .hasMessageContaining("Call failed") // set the instructed message
                .hasRootCauseInstanceOf(TestRootCauseException.class) // added original exception as the cause
        ;
    }

    @Test
    void p03_defaultValue_KO() throws ExecutionException, InterruptedException {
        when(dependencyMock.call()).thenReturn(CompletableFuture.failedFuture(new TestRootCauseException()));
        assertThat(workshop.p03_defaultValue().get()).isEqualTo("default");
    }

    @Test
    void p03_defaultValue_OK() throws ExecutionException, InterruptedException {
        when(dependencyMock.call()).thenReturn(completedFuture("OK"));
        assertThat(workshop.p03_defaultValue().get()).isEqualTo("OK");
    }


    @Test
    void p04_defaultFuture_OK() throws ExecutionException, InterruptedException {
        when(dependencyMock.call()).thenReturn(completedFuture("OK"));
        assertThat(workshop.p04_defaultFuture().get()).isEqualTo("OK");
    }

    @Test
    void p04_defaultFuture_KO() throws ExecutionException, InterruptedException {
        when(dependencyMock.call()).thenReturn(failedFuture(new TestRootCauseException()));
        when(dependencyMock.backup()).thenReturn(completedFuture("backup"));
        assertThat(workshop.p04_defaultFuture().get()).isEqualTo("backup");
    }

    @Test
    void p05_defaultFutureNonBlocking() throws ExecutionException, InterruptedException {
        // throw in commonPool
        when(dependencyMock.call()).thenAnswer(x -> supplyAsync(() -> {
            throw new TestRootCauseException();
        }));
        // provide the backup value on another executor. Using Answer<> to defer the startup of the work (otherwise the CF is already completed when called)
        when(dependencyMock.backup()).thenAnswer(x -> supplyAsync(() -> {
            PerformanceUtil.sleepMillis(10);
            return "backup";
        }, secondExecutor));
        // record the thread in which the backup value is emitted
        CaptureThreadName captureThreadName = new CaptureThreadName();

        String result = workshop.p05_defaultFutureNonBlocking()
                .whenComplete(captureThreadName).get();

        assertThat(result).isEqualTo("backup");
        System.out.println("Value emitted in thread " + captureThreadName.getThreadName());
        assertThat(captureThreadName.getThreadName()).contains("second");
    }

    @Test
    void p06_cleanup() throws ExecutionException, InterruptedException, IOException {
        when(dependencyMock.call()).thenAnswer(x -> supplyAsync(() -> "abc", delayedExecutor(100, MILLISECONDS)));

        workshop.p06_cleanup().get();

        File file = new File("out.txt");
        assertThat(Files.readString(file.toPath())).isEqualTo("abc");
        assertThat(file.delete()).isTrue();
    }

    @Test
    void p06_cleanup_KO() throws ExecutionException, InterruptedException, IOException {
        when(dependencyMock.call()).thenAnswer(x -> supplyAsync(() -> {throw new TestRootCauseException();}, delayedExecutor(100, MILLISECONDS)));

        assertThatThrownBy(() -> workshop.p06_cleanup().get());

        File file = new File("out.txt");
        assertThat(file.delete()).isTrue();
    }


}