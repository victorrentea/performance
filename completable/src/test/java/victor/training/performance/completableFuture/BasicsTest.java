package victor.training.performance.completableFuture;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.MethodOrderer.MethodName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;
import victor.training.performance.completableFuture.CaptureSystemOutput.OutputCapture;
import victor.training.performance.completableFuture.util.PerformanceUtil;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.failedFuture;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@TestMethodOrder(MethodName.class)
@Timeout(1)
class BasicsTest {
    Basics workshop = new BasicsSolved();

    @Test
    void p01_completed() throws ExecutionException, InterruptedException {
        assertThat(workshop.p01_completed().get()).isEqualTo("Hi");
    }

    @Test
    void p02_failed_KO() {
        assertThatThrownBy(() -> workshop.p02_failed(true).get()).hasCauseInstanceOf(IllegalArgumentException.class);
    }
    @Test
    void p02_failed_OK() throws ExecutionException, InterruptedException {
        assertThat(workshop.p02_failed(false).get()).isEqualTo("Hi");
    }

    @Test
    void p03_join() {
        assertThat(workshop.p03_join(completedFuture("abc"))).isEqualTo("abc");
    }

    @Test
    void p04_joinException_OK() {
        assertThat(workshop.p04_joinException(completedFuture("abc"))).isEqualTo("abc");
    }
    @Test
    void p04_joinException_KO() {
        CompletableFuture<String> failedFuture = failedFuture(new IllegalArgumentException("TEST ERROR MESSAGE"));
        assertThat(workshop.p04_joinException(failedFuture)).isEqualTo("TEST ERROR MESSAGE");
    }

    @Test
    void p05_get_OK() throws ExecutionException, InterruptedException {
        assertThat(workshop.p05_get(completedFuture("abc"))).isEqualTo("abc");
    }
    @Test
    void p05_get_KO() throws ExecutionException, InterruptedException {
        CompletableFuture<String> failedFuture = failedFuture(new IllegalArgumentException("TEST ERROR MESSAGE"));
        assertThat(workshop.p05_get(failedFuture)).isEqualTo("TEST ERROR MESSAGE");
    }

    @Test
    @CaptureSystemOutput
    void p06_run(OutputCapture outputCapture) {
        workshop.p06_run();
        PerformanceUtil.sleepMillis(100);
        Assertions.assertThat(outputCapture.toString()).contains("Hi");
    }

    @Test
    void p07_supply() throws ExecutionException, InterruptedException {
        assertThat(workshop.p07_supply().get()).contains("ForkJoinPool.commonPool");
    }

    @Test
    @CaptureSystemOutput
    void p08_accept(OutputCapture outputCapture) {
        workshop.p08_accept(completedFuture("abc"));
        Assertions.assertThat(outputCapture.toString()).contains("abc");
    }

}